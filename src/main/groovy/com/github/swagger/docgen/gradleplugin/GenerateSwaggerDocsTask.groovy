package com.github.swagger.docgen.gradleplugin

import com.github.kongchen.swagger.docgen.AbstractDocumentSource
import com.github.kongchen.swagger.docgen.GenerateException
import com.github.kongchen.swagger.docgen.mavenplugin.ApiSource
import com.github.kongchen.swagger.docgen.mavenplugin.MavenDocumentSource
import com.github.kongchen.swagger.docgen.mavenplugin.SpringMavenDocumentSource
import io.swagger.models.Info
import io.swagger.models.License
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * GradleSwaggerTask copied from ApiDocumentMojo.java
 */
class GenerateSwaggerDocsTask extends DefaultTask {
    public static final String TASK_NAME = 'swagger'

    String description = "Generates swagger documentation"

    @TaskAction
    def generateSwaggerDocuments() {
        SwaggerPluginExtension swagger = project.swagger
        Iterable dependencies = project.configurations.runtime.resolve()
        File classesDir = project.sourceSets.main.output.classesDir

        // TODO: Figure out how to skip
//        if (skipSwaggerGeneration) {
//            log.info("Swagger generation is skipped.");
//            return;
//        }

        if (swagger == null) {
            throw new GradleException("You must configure at least one apiSources element");
        }
        if (useSwaggerSpec11()) {
            throw new GradleException("You may use an old version of swagger which is not supported by swagger-maven-plugin 2.0+\n" +
                "swagger-maven-plugin 2.0+ only supports swagger-core 1.3.x");
        }

        if (useSwaggerSpec13()) {
            throw new GradleException("You may use an old version of swagger which is not supported by swagger-maven-plugin 3.0+\n" +
                "swagger-maven-plugin 3.0+ only supports swagger spec 2.0");
        }

        try {
            // TODO: Set from SwaggerPluginExtension
            License license = new License()
            license.setName("license");
            Info info = new Info();
            info.setTitle("title");
            info.setVersion("v1");
            info.setLicense(license)
            ApiSource apiSource = new ApiSource();
            apiSource.setInfo(info)
            apiSource.setLocations("com.sky.search")
            apiSource.setSwaggerDirectory("build/swaggerui")
//            for (ApiSource apiSource : apiSources) {

            validateConfiguration(apiSource);

            AbstractDocumentSource documentSource;

            if (apiSource.isSpringmvc()) {
                documentSource = new SpringMavenDocumentSource(apiSource);
            } else {
                documentSource = new MavenDocumentSource(apiSource);
            }

            documentSource.loadTypesToSkip();
            documentSource.loadModelModifier();
            documentSource.loadDocuments();
            if (apiSource.getOutputPath() != null) {
                File outputDirectory = new File(apiSource.getOutputPath()).getParentFile();
                if (outputDirectory != null && !outputDirectory.exists()) {
                    if (!outputDirectory.mkdirs()) {
                        throw new GradleException("Create directory[" + apiSource.getOutputPath() + "] for output failed.");
                    }
                }
            }
            if (apiSource.getTemplatePath() != null) {
                documentSource.toDocuments();
            }
            documentSource.toSwaggerDocuments(
                apiSource.getSwaggerUIDocBasePath() == null
                    ? apiSource.getBasePath()
                    : apiSource.getSwaggerUIDocBasePath(),
                apiSource.getOutputFormats());


            if (apiSource.isAttachSwaggerArtifact() && apiSource.getSwaggerDirectory() != null && this.project != null) {
                String classifier = new File(apiSource.getSwaggerDirectory()).getName();
                File swaggerFile = new File(apiSource.getSwaggerDirectory(), "swagger.json");
                this.projectHelper.attachArtifact(project, "json", classifier, swaggerFile);
            }
//            }

        } catch (GenerateException e) {
            throw new GradleException(e.getMessage(), e);
        } catch (Exception e) {
            throw new GradleException(e.getMessage(), e);
        }
    }

    /**
     * validate configuration according to swagger spec and plugin requirement
     *
     * @param apiSource
     * @throws GenerateException
     */
    private static void validateConfiguration(ApiSource apiSource) throws GenerateException {
        if (apiSource == null) {
            throw new GenerateException("You do not configure any apiSource!");
        } else if (apiSource.getInfo() == null) {
            throw new GenerateException("`<info>` is required by Swagger Spec.");
        }
        if (apiSource.getInfo().getTitle() == null) {
            throw new GenerateException("`<info><title>` is required by Swagger Spec.");
        }

        if (apiSource.getInfo().getVersion() == null) {
            throw new GenerateException("`<info><version>` is required by Swagger Spec.");
        }

        if (apiSource.getInfo().getLicense() != null && apiSource.getInfo().getLicense().getName() == null) {
            throw new GenerateException("`<info><license><name>` is required by Swagger Spec.");
        }

        if (apiSource.getLocations() == null) {
            throw new GenerateException("<locations> is required by this plugin.");
        }

    }

    private static boolean useSwaggerSpec11() {
        try {
            Class.forName("com.wordnik.swagger.annotations.ApiErrors");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static boolean useSwaggerSpec13() {
        try {
            Class.forName("com.wordnik.swagger.model.ApiListing");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}

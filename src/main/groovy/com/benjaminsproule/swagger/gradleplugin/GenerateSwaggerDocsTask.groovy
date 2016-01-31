package com.benjaminsproule.swagger.gradleplugin

import com.github.kongchen.swagger.docgen.AbstractDocumentSource
import com.github.kongchen.swagger.docgen.GenerateException
import com.github.kongchen.swagger.docgen.mavenplugin.MavenDocumentSource
import com.github.kongchen.swagger.docgen.mavenplugin.SpringMavenDocumentSource
import org.apache.maven.monitor.logging.DefaultLog
import org.codehaus.plexus.logging.console.ConsoleLogger
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * GradleSwaggerTask copied from {@link com.github.kongchen.swagger.docgen.mavenplugin.ApiDocumentMojo}
 */
class GenerateSwaggerDocsTask extends DefaultTask {
    public static final String TASK_NAME = 'swagger'

    String description = "Generates swagger documentation"

    @TaskAction
    def generateSwaggerDocuments() {
        SwaggerPluginExtension swaggerPluginExtension = project.swagger

        if (swaggerPluginExtension == null) {
            throw new GradleException("You must configure at least one swaggerPluginExtensions element");
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
//            for (swaggerPluginExtension swaggerPluginExtension : swaggerPluginExtensions) {
            processSwaggerPluginExtension(swaggerPluginExtension)
//            }

        } catch (GenerateException e) {
            throw new GradleException(e.getMessage(), e);
        } catch (Exception e) {
            throw new GradleException(e.getMessage(), e);
        }
    }

    private void processSwaggerPluginExtension(SwaggerPluginExtension swaggerPluginExtension) {
        validateConfiguration(swaggerPluginExtension);

        AbstractDocumentSource documentSource;

        if (swaggerPluginExtension.isSpringmvc()) {
            documentSource = new SpringMavenDocumentSource(swaggerPluginExtension, new DefaultLog(new ConsoleLogger(0, "name")));
        } else {
            documentSource = new MavenDocumentSource(swaggerPluginExtension, new DefaultLog(new ConsoleLogger(0, "name")));
        }

        documentSource.loadTypesToSkip();
        documentSource.loadModelModifier();
        documentSource.loadDocuments();
        if (swaggerPluginExtension.getOutputPath() != null) {
            File outputDirectory = new File(swaggerPluginExtension.getOutputPath()).getParentFile();
            if (outputDirectory != null && !outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    throw new GradleException("Create directory[" + swaggerPluginExtension.getOutputPath() + "] for output failed.");
                }
            }
        }
        if (swaggerPluginExtension.getTemplatePath() != null) {
            documentSource.toDocuments();
        }
        documentSource.toSwaggerDocuments(
            swaggerPluginExtension.getSwaggerUIDocBasePath() == null
                ? swaggerPluginExtension.getBasePath()
                : swaggerPluginExtension.getSwaggerUIDocBasePath(),
            swaggerPluginExtension.getOutputFormats());

        if (swaggerPluginExtension.isAttachSwaggerArtifact() && swaggerPluginExtension.getSwaggerDirectory() != null && this.project != null) {
            String classifier = new File(swaggerPluginExtension.getSwaggerDirectory()).getName();
            File swaggerFile = new File(swaggerPluginExtension.getSwaggerDirectory(), "swagger.json");
//                this.projectHelper.attachArtifact(project, "json", classifier, swaggerFile);
        }
    }

    /**
     * validate configuration according to swagger spec and plugin requirement
     *
     * @param swaggerPluginExtension
     * @throws GenerateException
     */
    private static void validateConfiguration(SwaggerPluginExtension swaggerPluginExtension) throws GenerateException {
        if (swaggerPluginExtension == null) {
            throw new GenerateException("You do not configure any swaggerPluginExtension!");
        } else if (swaggerPluginExtension.getInfo() == null) {
            throw new GenerateException("`<info>` is required by Swagger Spec.");
        }
        if (swaggerPluginExtension.getInfo().getTitle() == null) {
            throw new GenerateException("`<info><title>` is required by Swagger Spec.");
        }

        if (swaggerPluginExtension.getInfo().getVersion() == null) {
            throw new GenerateException("`<info><version>` is required by Swagger Spec.");
        }

        if (swaggerPluginExtension.getInfo().getLicense() != null && swaggerPluginExtension.getInfo().getLicense().getName() == null) {
            throw new GenerateException("`<info><license><name>` is required by Swagger Spec.");
        }

        if (swaggerPluginExtension.getLocations() == null) {
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

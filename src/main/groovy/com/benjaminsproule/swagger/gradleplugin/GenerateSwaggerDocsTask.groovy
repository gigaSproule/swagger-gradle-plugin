package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.extension.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.extension.SwaggerExtension
import com.benjaminsproule.swagger.gradleplugin.logger.Slf4jWrapper
import com.github.kongchen.swagger.docgen.AbstractDocumentSource
import com.github.kongchen.swagger.docgen.GenerateException
import com.github.kongchen.swagger.docgen.mavenplugin.MavenDocumentSource
import com.github.kongchen.swagger.docgen.mavenplugin.SpringMavenDocumentSource
import org.apache.maven.monitor.logging.DefaultLog
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar

/**
 * GradleSwaggerTask copied from {@link com.github.kongchen.swagger.docgen.mavenplugin.ApiDocumentMojo}
 */
class GenerateSwaggerDocsTask extends DefaultTask {
    public static final String TASK_NAME = 'generateSwaggerDocumentation'

    String description = 'Generates swagger documentation'

    String group = 'swagger'

    @TaskAction
    generateSwaggerDocuments() {
        SwaggerExtension swaggerExtension = project.swagger

        if (swaggerExtension == null) {
            throw new GradleException('You must configure at least one swaggerPluginExtensions element')
        }

        if (useSwaggerSpec11()) {
            throw new GradleException('You may use an old version of swagger which is not supported by swagger-maven-plugin 2.0+\n' +
                'swagger-maven-plugin 2.0+ only supports swagger-core 1.3.x')
        }

        if (useSwaggerSpec13()) {
            throw new GradleException('You may use an old version of swagger which is not supported by swagger-maven-plugin 3.0+\n' +
                'swagger-maven-plugin 3.0+ only supports swagger spec 2.0')
        }

        try {
            for (ApiSourceExtension apiSourceExtension : swaggerExtension.apiSourceExtensions) {
                processSwaggerPluginExtension(apiSourceExtension)
            }
        } catch (GenerateException e) {
            throw new GradleException(e.getMessage(), e)
        } catch (Exception e) {
            throw new GradleException(e.getMessage(), e)
        }
    }

    private void processSwaggerPluginExtension(ApiSourceExtension swaggerPluginExtension) {
        validateConfiguration(swaggerPluginExtension)

        AbstractDocumentSource documentSource

        def encoding = project.compileJava.options.encoding
        if (encoding == null) {
            encoding = 'UTF-8'
        }

        if (swaggerPluginExtension.isSpringmvc()) {
            documentSource = new SpringMavenDocumentSource(swaggerPluginExtension, new DefaultLog(new Slf4jWrapper()), encoding)
        } else {
            documentSource = new MavenDocumentSource(swaggerPluginExtension, new DefaultLog(new Slf4jWrapper()), encoding)
        }

        documentSource.loadTypesToSkip()
        documentSource.loadModelModifier()
        documentSource.loadModelConverters()
        documentSource.loadDocuments()

        if (swaggerPluginExtension.getOutputPath() != null) {
            File outputDirectory = new File(swaggerPluginExtension.getOutputPath()).getParentFile()
            if (outputDirectory != null && !outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    throw new GradleException("Create directory[${swaggerPluginExtension.getOutputPath()}] for output failed.")
                }
            }
        }

        if (swaggerPluginExtension.getTemplatePath() != null) {
            documentSource.toDocuments()
        }

        documentSource.toSwaggerDocuments(
            swaggerPluginExtension.getSwaggerUIDocBasePath() == null
                ? swaggerPluginExtension.getBasePath()
                : swaggerPluginExtension.getSwaggerUIDocBasePath(),
            swaggerPluginExtension.getOutputFormats(),
            swaggerPluginExtension.getSwaggerFileName(),
            encoding)

        if (swaggerPluginExtension.isAttachSwaggerArtifact() && swaggerPluginExtension.getSwaggerDirectory() != null && this.project != null) {
            String classifierName = new File(swaggerPluginExtension.getSwaggerDirectory()).getName()
            File swaggerFile = new File(swaggerPluginExtension.getSwaggerDirectory(), 'swagger.json')

            project.task('createSwaggerArtifact', type: Jar, dependsOn: project.tasks.classes) {
                classifier = classifierName
                from swaggerFile
            }

            project.artifacts {
                archives project.tasks.createSwaggerArtifact
            }

            project.tasks.createSwaggerArtifact.execute()
        }
    }

    /**
     * validate configuration according to swagger spec and plugin requirement
     *
     * @param apiSourceExtension
     * @throws GenerateException
     */
    private static void validateConfiguration(ApiSourceExtension apiSourceExtension) throws GenerateException {
        if (apiSourceExtension == null) {
            throw new GenerateException('You do not configure any apiSourceExtension!')
        } else if (apiSourceExtension.getInfo() == null) {
            throw new GenerateException('`<info>` is required by Swagger Spec.')
        }
        if (apiSourceExtension.getInfo().getTitle() == null) {
            throw new GenerateException('`<info><title>` is required by Swagger Spec.')
        }

        if (apiSourceExtension.getInfo().getVersion() == null) {
            throw new GenerateException('`<info><version>` is required by Swagger Spec.')
        }

        if (apiSourceExtension.getInfo().getLicense() != null && apiSourceExtension.getInfo().getLicense().getName() == null) {
            throw new GenerateException('`<info><license><name>` is required by Swagger Spec.')
        }

        if (apiSourceExtension.getLocations() == null) {
            throw new GenerateException('<locations> is required by this plugin.')
        }

    }

    private static boolean useSwaggerSpec11() {
        try {
            Class.forName('com.wordnik.swagger.annotations.ApiErrors')
            return true
        } catch (ClassNotFoundException ignored) {
            return false
        }
    }

    private static boolean useSwaggerSpec13() {
        try {
            Class.forName('com.wordnik.swagger.model.ApiListing')
            return true
        } catch (ClassNotFoundException ignored) {
            return false
        }
    }
}

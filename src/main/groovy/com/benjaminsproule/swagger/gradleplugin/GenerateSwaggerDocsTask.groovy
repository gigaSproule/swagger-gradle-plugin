package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.generator.GeneratorFactory
import com.benjaminsproule.swagger.gradleplugin.misc.EnvironmentConfigurer
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import com.benjaminsproule.swagger.gradleplugin.reader.ReaderFactory
import com.benjaminsproule.swagger.gradleplugin.validator.ApiSourceValidator
import io.swagger.config.FilterFactory
import io.swagger.core.filter.SpecFilter
import io.swagger.models.Swagger
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.Jar

import static com.benjaminsproule.swagger.gradleplugin.VersionUtils.ensureCompatibleSwaggerSpec

/**
 * GradleSwaggerTask originally from kongchen swagger maven plugin.
 */
class GenerateSwaggerDocsTask extends DefaultTask {
    public static final String TASK_NAME = 'generateSwaggerDocumentation'

    @OutputDirectories
    Iterable<File> outputDirectories

    @OutputFiles
    Iterable<File> outputFile

    @InputFiles
    Iterable<File> inputFiles

    @Internal
    ClassFinder classFinder
    @Internal
    ReaderFactory readerFactory
    @Internal
    GeneratorFactory generatorFactory
    @Internal
    ApiSourceValidator apiSourceValidator

    @TaskAction
    generateSwaggerDocuments() {
        SwaggerExtension swaggerExtension = project.extensions.getByName(SwaggerExtension.EXTENSION_NAME) as SwaggerExtension

        ensureCompatibleSwaggerSpec()

        try {
            for (ApiSourceExtension apiSourceExtension : swaggerExtension.apiSourceExtensions) {
                // TODO: Replace below
                def environmentConfigurer = new EnvironmentConfigurer(apiSourceExtension, classFinder)
                    .configureModelModifiers()
                    .configureModelConverters()
                    .configureSwaggerFilter()
                    .initOutputDirectory()


                validateApiSourceExtension(apiSourceExtension)

                processApiSourceExtension(apiSourceExtension)

                environmentConfigurer.cleanUp()
            }
        } catch (InvalidUserDataException iude) {
            throw iude
        } catch (Exception e) {
            throw new GradleException(e.getMessage(), e)
        }
    }

    private void validateApiSourceExtension(ApiSourceExtension apiSourceExtension) {
        // Validate Extensions without inputting annotated defaults
        def errors = apiSourceValidator.isValid(apiSourceExtension)

        if (errors) {
            throw new InvalidUserDataException(errors.join(","))
        }
    }

    private void processApiSourceExtension(ApiSourceExtension apiSourceExtension) {
        // Create Swagger object - throwing exception if errors
        def reader = readerFactory.reader(apiSourceExtension)
        Swagger swagger = reader.read()
        swagger = applySwaggerFilter(swagger)

        def generator = generatorFactory.generator(apiSourceExtension)
        generator.generate(swagger)

        if (apiSourceExtension.attachSwaggerArtifact && apiSourceExtension.swaggerDirectory && this.project) {
            String classifierName = new File(apiSourceExtension.swaggerDirectory).getName()
            File swaggerFile = new File(apiSourceExtension.swaggerDirectory)

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

    private static Swagger applySwaggerFilter(Swagger swagger) {
        if (FilterFactory.getFilter()) {
            return new SpecFilter().filter(swagger, FilterFactory.getFilter(), [:], [:], [:])
        }
        swagger
    }
}

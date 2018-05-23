package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.classpath.ResourceFinder
import com.benjaminsproule.swagger.gradleplugin.generator.GeneratorFactory
import com.benjaminsproule.swagger.gradleplugin.misc.EnvironmentConfigurer
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import com.benjaminsproule.swagger.gradleplugin.reader.ReaderFactory
import io.swagger.config.FilterFactory
import io.swagger.core.filter.SpecFilter
import io.swagger.models.Swagger
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Task
import org.gradle.api.internal.TaskInternal
import org.gradle.api.specs.AndSpec
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar

import static com.benjaminsproule.swagger.gradleplugin.VersionUtils.ensureCompatibleSwaggerSpec

/**
 * GradleSwaggerTask originally from kongchen swagger maven plugin.
 */
class GenerateSwaggerDocsTask extends DefaultTask {
    public static final String TASK_NAME = 'generateSwaggerDocumentation'

    String description = 'Generates swagger documentation'

    String group = 'swagger'

    private ClassFinder classFinder
    private ResourceFinder resourceFinder

    @Override
    Spec<? super TaskInternal> getOnlyIf() {
        return new AndSpec<Task>(new Spec<Task>() {
            boolean isSatisfiedBy(Task element) {
                return element == GenerateSwaggerDocsTask.this && enabled && !(project.findProperty('swagger.skip') ?: false)
            }
        })
    }

    @TaskAction
    generateSwaggerDocuments() {
        SwaggerExtension swaggerExtension = project.extensions.getByName(SwaggerExtension.EXTENSION_NAME) as SwaggerExtension
        classFinder = ClassFinder.getInstance(project)
        resourceFinder = ResourceFinder.getInstance(project)

        ensureCompatibleSwaggerSpec()

        try {
            for (ApiSourceExtension apiSourceExtension : swaggerExtension.apiSourceExtensions) {
                classFinder.clearClassCache() // TODO: Maybe do something better here?
                processSwaggerPluginExtension(apiSourceExtension)
            }
        } catch (InvalidUserDataException iude) {
            throw iude
        } catch (Exception e) {
            throw new GradleException(e.getMessage(), e)
        }
    }

    private void processSwaggerPluginExtension(ApiSourceExtension apiSourceExtension) {
        def errors = apiSourceExtension.isValid()

        if (errors) {
            throw new InvalidUserDataException(errors.join(","))
        }

        // TODO: Replace below
        new EnvironmentConfigurer(apiSourceExtension, classFinder, resourceFinder)
            .configureModelModifiers()
            .configureModelConverters()
            .configureSwaggerFilter()
            .initOutputDirectory()

        def reader = new ReaderFactory(classFinder, apiSourceExtension).reader()
        Swagger swagger = reader.read()
        swagger = applySwaggerFilter(swagger)

        def generator = GeneratorFactory.generator(apiSourceExtension)
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
            return new SpecFilter().filter(swagger, FilterFactory.getFilter(), new HashMap<String, List<String>>(), new HashMap<String, String>(),
                new HashMap<String, List<String>>())
        }
        swagger
    }
}

package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.generator.GeneratorFactory
import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import com.benjaminsproule.swagger.gradleplugin.reader.ReaderFactory
import com.benjaminsproule.swagger.gradleplugin.validator.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleSwaggerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        SwaggerExtension swaggerExtension = project.extensions.create('swagger', SwaggerExtension, project)

        def createdClassFinder = new ClassFinder(project)
        def generateSwaggerDocsTask = project.task(type: GenerateSwaggerDocsTask,
            dependsOn: 'classes',
            group: 'swagger',
            description: 'Generates swagger documentation',
            GenerateSwaggerDocsTask.TASK_NAME,
            {
                classFinder = createdClassFinder
                readerFactory = new ReaderFactory(createdClassFinder)
                generatorFactory = new GeneratorFactory(createdClassFinder)
                apiSourceValidator = new ApiSourceValidator(new InfoValidator(new LicenseValidator()), new SecurityDefinitionValidator(new ScopeValidator()), new TagValidator(new ExternalDocsValidator()))
            }) as GenerateSwaggerDocsTask

        if (project.hasProperty('swagger.skip')) {
            generateSwaggerDocsTask.enabled = false
        }

        project.afterEvaluate {
            generateSwaggerDocsTask.outputDirectories = swaggerExtension.apiSourceExtensions.collect {
                if (it.swaggerDirectory) {
                    return new File(it.swaggerDirectory)
                }
                return null as File
            }.findAll {
                it != null
            }
            generateSwaggerDocsTask.outputFile = swaggerExtension.apiSourceExtensions.collect {
                if (it.outputPath) {
                    return new File(it.outputPath)
                }
                return null as File
            }.findAll {
                it != null
            }
            def classLoader = createdClassFinder.getClassLoader() as URLClassLoader
            generateSwaggerDocsTask.inputFiles = classLoader.getURLs().collect {
                new File(it.toURI())
            }
        }
    }
}

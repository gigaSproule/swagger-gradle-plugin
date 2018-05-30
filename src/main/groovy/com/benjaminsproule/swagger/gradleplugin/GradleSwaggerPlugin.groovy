package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.classloader.VisitableURLClassLoader

class GradleSwaggerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ClassFinder classFinder = ClassFinder.getInstance(project)
        SwaggerExtension swaggerExtension = project.extensions.create('swagger', SwaggerExtension, project, classFinder)

        def generateSwaggerDocsTask = project.task(type: GenerateSwaggerDocsTask,
            dependsOn: 'classes',
            constructorArgs: [classFinder],
            GenerateSwaggerDocsTask.TASK_NAME) as GenerateSwaggerDocsTask

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
            generateSwaggerDocsTask.inputFiles = ((classFinder.getClassLoader() as URLClassLoader).parent as VisitableURLClassLoader).URLs.collect {
                new File(it.toURI())
            }
        }
    }
}

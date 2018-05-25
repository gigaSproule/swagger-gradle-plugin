package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.classpath.ResourceFinder
import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.classloader.VisitableURLClassLoader

class GradleSwaggerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ClassFinder classFinder = ClassFinder.getInstance(project)
        ResourceFinder resourceFinder = ResourceFinder.getInstance(project)
        SwaggerExtension swaggerExtension = project.extensions.create('swagger', SwaggerExtension, project, classFinder, resourceFinder)

        def generateSwaggerDocsTask = project.task(type: GenerateSwaggerDocsTask,
            dependsOn: 'classes',
            constructorArgs: [classFinder, resourceFinder],
            GenerateSwaggerDocsTask.TASK_NAME) as GenerateSwaggerDocsTask

        project.afterEvaluate {
            generateSwaggerDocsTask.outputDirectories = swaggerExtension.apiSourceExtensions.collect {
                def directory = it.getSwaggerDirectory()
                if (!directory) {
                    directory = it.getOutputPath()
                }
                new File(directory)
            }
            generateSwaggerDocsTask.inputFiles = ((classFinder.classLoader as URLClassLoader).parent as VisitableURLClassLoader).URLs.collect {
                new File(it.toURI())
            }
        }
    }
}

package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.classpath.ResourceFinder
import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleSwaggerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ClassFinder classFinder = ClassFinder.getInstance(project)
        ResourceFinder resourceFinder = ResourceFinder.getInstance(project)
        project.extensions.create('swagger', SwaggerExtension, project, classFinder, resourceFinder)

        project.task(GenerateSwaggerDocsTask.TASK_NAME,
            type: GenerateSwaggerDocsTask,
            dependsOn: 'classes')
    }
}

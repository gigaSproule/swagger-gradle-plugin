package com.github.swagger.docgen.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * GradleSwaggerPlugin
 */
class GradleSwaggerPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create("swagger", SwaggerPluginExtension)

        project.task(GenerateSwaggerDocsTask.TASK_NAME, type: GenerateSwaggerDocsTask, dependsOn: 'classes')
    }
}


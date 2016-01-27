package com.github.swagger.docgen.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * GradleSwaggerPlugin
 */
class GradleSwaggerPlugin implements Plugin<Project> {

    void apply(Project project) {
        // TODO: Best way to do this?
        project.dependencies.add('compile', 'io.swagger:swagger-core:1.5.4')
        project.configurations.getByName('compile').exclude([group: 'javax.ws.rs', module: 'jsr311-api'])

        project.extensions.create("swagger", SwaggerPluginExtension)

        project.task(GenerateSwaggerDocsTask.TASK_NAME, type: GenerateSwaggerDocsTask, dependsOn: 'classes')
    }
}


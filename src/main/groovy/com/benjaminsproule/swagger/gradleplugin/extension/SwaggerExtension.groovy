package com.benjaminsproule.swagger.gradleplugin.extension

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class SwaggerExtension {
    Collection<ApiSourceExtension> apiSourceExtensions = new ArrayList<>()

    private Project project
    boolean skipSwaggerGeneration

    SwaggerExtension(Project project) {
        this.project = project
    }

    boolean getSkipSwaggerGeneration() {
        if (project.hasProperty('swagger.skip')) {
            return project.getProperty('swagger.skip')
        }

        return this.skipSwaggerGeneration
    }

    ApiSourceExtension apiSource(Closure closure) {
        ApiSourceExtension apiSourceExtension = project.configure(new ApiSourceExtension(project), closure) as ApiSourceExtension
        apiSourceExtensions.add(apiSourceExtension)
        return apiSourceExtension
    }

}

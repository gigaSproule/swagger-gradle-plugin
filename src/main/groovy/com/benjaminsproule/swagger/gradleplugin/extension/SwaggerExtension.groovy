package com.benjaminsproule.swagger.gradleplugin.extension

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class SwaggerExtension {
    Collection<ApiSourceExtension> apiSourceExtensions = new ArrayList<>()

    private Project project

    SwaggerExtension(Project project) {
        this.project = project
    }

    ApiSourceExtension apiSource(Closure closure) {
        ApiSourceExtension apiSourceExtension = project.configure(new ApiSourceExtension(project), closure) as ApiSourceExtension
        apiSourceExtensions.add(apiSourceExtension)
        return apiSourceExtension
    }

}

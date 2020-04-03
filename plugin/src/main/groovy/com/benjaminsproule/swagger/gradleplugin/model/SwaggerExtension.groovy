package com.benjaminsproule.swagger.gradleplugin.model

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class SwaggerExtension {
    public static final String EXTENSION_NAME = 'swagger'

    List<ApiSourceExtension> apiSourceExtensions = []

    private Project project

    SwaggerExtension(Project project) {
        this.project = project
    }

    /**
     * Used for taking in configuration for api source object.
     * @param closure {@link ApiSourceExtension} closure
     */
    void apiSource(Closure closure) {
        apiSourceExtensions += project.configure(new ApiSourceExtension(project), closure) as ApiSourceExtension
    }
}

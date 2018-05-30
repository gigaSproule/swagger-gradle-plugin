package com.benjaminsproule.swagger.gradleplugin.model

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class SwaggerExtension implements ModelValidator {
    public static final String EXTENSION_NAME = 'swagger'

    private Project project
    private ClassFinder classFinder

    Collection<ApiSourceExtension> apiSourceExtensions = new ArrayList<>()

    SwaggerExtension(Project project, ClassFinder classFinder) {
        this.project = project
        this.classFinder = classFinder
        this.apiSourceExtensions = new ArrayList<>()
    }

    /**
     * Used for taking in configuration for api source object.
     * @param closure InfoExtension closure
     */
    void apiSource(Closure closure) {
        ApiSourceExtension apiSourceExtension = project.configure(new ApiSourceExtension(project, classFinder), closure) as ApiSourceExtension
        apiSourceExtensions.add(apiSourceExtension)
    }

    @Override
    List<String> isValid() {
        if (!apiSourceExtensions) {
            return ['You must specify at least one apiSource element']
        }

        def errors = []
        apiSourceExtensions.forEach { apiSource ->
            errors.addAll(apiSource.isValid())
        }

        return errors
    }
}

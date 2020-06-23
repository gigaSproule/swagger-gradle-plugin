package com.benjaminsproule.swagger.gradleplugin.model

import org.gradle.api.Project

class TagExtension {
    String name
    String description
    ExternalDocsExtension externalDocs

    private Project project

    TagExtension(Project project) {
        this.project = project
    }

    /**
     * Used for taking in configuration for external doc object.
     * @param closure {@link ExternalDocsExtension} closure
     */
    void externalDocs(Closure closure) {
        externalDocs = project.configure(new ExternalDocsExtension(), closure) as ExternalDocsExtension
    }
}

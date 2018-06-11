package com.benjaminsproule.swagger.gradleplugin.model

import org.gradle.api.Project

class InfoExtension {
    String description
    String termsOfService
    String title
    String version
    ContactExtension contact
    LicenseExtension license
    Project project

    InfoExtension(Project project) {
        this.project = project
    }

    /**
     * Used for taking in configuration for contact object.
     * @param closure {@link ContactExtension} closure
     */
    void contact(Closure closure) {
        this.contact = project.configure(new ContactExtension(), closure) as ContactExtension
    }

    /**
     * Used for taking in configuration for license object.
     * @param closure {@link LicenseExtension} closure
     */
    void license(Closure closure) {
        this.license = project.configure(new LicenseExtension(), closure) as LicenseExtension
    }
}

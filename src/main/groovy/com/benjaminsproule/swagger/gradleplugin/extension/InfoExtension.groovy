package com.benjaminsproule.swagger.gradleplugin.extension

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class InfoExtension {
    String description
    String termsOfService
    String title
    String version
    ContactExtension contactExtension
    LicenseExtension licenseExtension
    private Project project

    InfoExtension(Project project) {
        this.project = project
    }

    void contact(Closure closure) {
        this.contactExtension = project.configure(new ContactExtension(), closure) as ContactExtension
    }

    void license(Closure closure) {
        this.licenseExtension = project.configure(new LicenseExtension(), closure) as LicenseExtension
    }
}

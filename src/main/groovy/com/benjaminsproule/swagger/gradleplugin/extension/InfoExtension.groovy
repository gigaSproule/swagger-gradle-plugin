package com.benjaminsproule.swagger.gradleplugin.extension

import groovy.transform.ToString
import io.swagger.models.Contact
import io.swagger.models.Info
import io.swagger.models.License
import org.gradle.api.Project

@ToString(includeNames = true)
class InfoExtension extends Info {
    private Project project

    InfoExtension(Project project) {
        this.project = project
    }

    void contact(Closure closure) {
        this.contact = project.configure(new Contact(), closure) as Contact
    }

    void license(Closure closure) {
        this.license = project.configure(new License(), closure) as License
    }
}

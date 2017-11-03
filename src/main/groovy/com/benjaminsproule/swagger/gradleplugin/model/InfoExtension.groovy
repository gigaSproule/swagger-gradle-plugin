package com.benjaminsproule.swagger.gradleplugin.model

import io.swagger.models.Info
import org.gradle.api.Project

class InfoExtension implements ModelValidator, Swagerable<Info> {
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

    void contact(Closure closure) {
        this.contact = project.configure(new ContactExtension(), closure) as ContactExtension
    }

    void license(Closure closure) {
        this.license = project.configure(new LicenseExtension(), closure) as LicenseExtension
    }

    void mergeWith(InfoExtension info) {
        if (info) {
            if (this.description == null) {
                this.description = info.description
            }

            if (this.version == null) {
                this.version = info.version
            }

            if (this.title == null) {
                this.title = info.title
            }

            if (this.termsOfService == null) {
                this.termsOfService = info.termsOfService
            }

            if (this.contact == null) {
                this.contact = info.contact
            }

            if (this.license == null) {
                this.license = info.license
            }
        }
    }

    @Override
    Info asSwaggerType() {
        Info swaggerInfo = new Info()
        swaggerInfo.setDescription(description)
        swaggerInfo.setTermsOfService(termsOfService)
        swaggerInfo.setVersion(version)
        swaggerInfo.setTitle(title)

        if (contact != null) {
            swaggerInfo.setContact(contact.getSwaggerContact())
        }

        if (license != null) {
            swaggerInfo.setLicense(license.getSwaggerLicence())
        }

        return swaggerInfo
    }

    @Override
    List<String> isValid() {
        if (!license) {
            return ['info.licence is required by the swagger spec']
        }

        def errors = []
        if (!title) {
            errors.add('info.title is required by the swagger spec')
        }

        if (!version) {
            errors.add('info.version is required by the swagger spec')
        }

        errors.addAll(license.isValid())
        return errors
    }
}

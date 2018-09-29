package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.model.*
import io.swagger.annotations.Contact
import io.swagger.annotations.Info
import io.swagger.annotations.License
import io.swagger.annotations.SwaggerDefinition
import org.gradle.api.Project

class AnnotationPopulator {

    private Project project
    private ClassFinder classFinder

    AnnotationPopulator(Project project, ClassFinder classFinder) {
        this.project = project
        this.classFinder = classFinder
    }

    void populateExtensionsFromAnnotations(ApiSourceExtension apiSourceExtension) {
        if (!apiSourceExtension.locations) {
            return
        }

        if (!apiSourceExtension.host) {
            apiSourceExtension.host = getHostFromAnnotation(apiSourceExtension)
        }

        if (!apiSourceExtension.basePath) {
            apiSourceExtension.basePath = getBasePathFromAnnotation(apiSourceExtension)
        }

        if (!apiSourceExtension.tags) {
            apiSourceExtension.tags = getTagsFromAnnotation(apiSourceExtension)
        }

        if (!apiSourceExtension.info) {
            apiSourceExtension.info = getInfoFromAnnotation(apiSourceExtension)
        }
    }

    private String getHostFromAnnotation(ApiSourceExtension apiSourceExtension) {
        def swaggerDefinition = classFinder.getAnnotations(SwaggerDefinition, apiSourceExtension.locations).first()
        if (swaggerDefinition) {
            return swaggerDefinition.host()
        }

        return null
    }

    private String getBasePathFromAnnotation(ApiSourceExtension apiSourceExtension) {
        def swaggerDefinition = classFinder.getAnnotations(SwaggerDefinition, apiSourceExtension.locations).first()
        if (swaggerDefinition) {
            return swaggerDefinition.basePath()
        }

        return null
    }

    private List<TagExtension> getTagsFromAnnotation(ApiSourceExtension apiSourceExtension) {
        def tags = []
        classFinder.getAnnotations(SwaggerDefinition, apiSourceExtension.locations).each { swaggerDefinition ->
            swaggerDefinition.tags().each { tag ->
                if (!tag.name() && !tag.description()) {
                    return
                }
                def tagExtension = new TagExtension(project)
                tagExtension.name = tag.name()
                tagExtension.description = tag.description()

                tags.add(tagExtension)

                if (!tag.externalDocs().value() && !tag.externalDocs().url()) {
                    return
                }
                def externalDocsExtension = new ExternalDocsExtension()
                externalDocsExtension.description = tag.externalDocs().value()
                externalDocsExtension.url = tag.externalDocs().url()
                tagExtension.externalDocs = externalDocsExtension
            }
        }
        if (!tags) {
            return null
        }
        return tags
    }

    private InfoExtension getInfoFromAnnotation(ApiSourceExtension apiSourceExtension) {
        Info infoAnnotation = classFinder.getAnnotations(SwaggerDefinition, apiSourceExtension.locations).findResult {
            it.info()
        }
        InfoExtension infoExtension = new InfoExtension(project)
        infoExtension.title = infoAnnotation.title()
        infoExtension.version = infoAnnotation.version()
        infoExtension.termsOfService = infoAnnotation.termsOfService()
        infoExtension.description = infoAnnotation.description()
        infoExtension.contact = getContactFromAnnotation(infoAnnotation.contact())
        infoExtension.license = getLicenseFromAnnotation(infoAnnotation.license())
        infoExtension
    }

    // TODO: Test the logic for when it's default
    private static ContactExtension getContactFromAnnotation(Contact contactAnnotation) {
        if (!contactAnnotation.name() && !contactAnnotation.url() && !contactAnnotation.email()) {
            return null
        }

        ContactExtension contactExtension = new ContactExtension()
        contactExtension.name = contactAnnotation.name()
        contactExtension.url = contactAnnotation.url()
        contactExtension.email = contactAnnotation.email()
        contactExtension
    }

    // TODO: Test the logic for when it's default
    private static LicenseExtension getLicenseFromAnnotation(License licenseAnnotation) {
        if (!licenseAnnotation.name() && !licenseAnnotation.url()) {
            return null
        }

        LicenseExtension licenseExtension = new LicenseExtension()
        licenseExtension.name = licenseAnnotation.name()
        licenseExtension.url = licenseAnnotation.url()
        licenseExtension
    }

}

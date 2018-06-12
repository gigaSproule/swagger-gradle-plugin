package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.model.*
import org.gradle.api.Project
import org.reflections.Reflections
import spock.lang.Specification

import java.lang.annotation.Annotation

class AnnotationPopulatorTest extends Specification {

    private Project mockProject
    private ClassFinder mockClassFinder
    private AnnotationPopulator annotationPopulator

    def setup() {
        mockProject = Mock(Project)
        mockClassFinder = Mock(ClassFinder)
        annotationPopulator = new AnnotationPopulator(mockProject, mockClassFinder)

        mockClassFinder.getAnnotations(_, _) >> { args ->
            def annotation = args[0] as Class<? extends Annotation>
            new Reflections(getClass().getClassLoader(), args[1]).getTypesAnnotatedWith(annotation).collect {
                it.getAnnotation(annotation)
            }
        }
    }

    def 'Should generate host, basePath, info, contact and license from annotations when not present'() {
        given:
        def apiSourceExtension = createApiSourceExtension()
        fields.each {
            apiSourceExtension.setProperty(it, null)
        }

        when:
        annotationPopulator.populateExtensionsFromAnnotations(apiSourceExtension)

        then:
        assertApiSourceExtensionPopulated(apiSourceExtension, fields)

        where:
        fields << [
            [],
            ['host'],
            ['basePath'],
            ['tags'],
            ['info']
        ]
    }

    def 'Should not generate contact and license from annotations when info present but not contact or license'() {
        given:
        def apiSourceExtension = createApiSourceExtension()
        apiSourceExtension.info.contact = null
        apiSourceExtension.info.license = null

        when:
        annotationPopulator.populateExtensionsFromAnnotations(apiSourceExtension)

        then:
        apiSourceExtension.info.contact == null
        apiSourceExtension.info.license == null
    }

    def 'Should not generate external docs from annotations when tag present but not external docs'() {
        given:
        def apiSourceExtension = createApiSourceExtension()
        apiSourceExtension.tags[0].externalDocs = null

        when:
        annotationPopulator.populateExtensionsFromAnnotations(apiSourceExtension)

        then:
        apiSourceExtension.tags[0].externalDocs == null
    }

    def 'Should not generate tags from annotations when tag has nothing populated (i.e. is default)'() {
        given:
        def apiSourceExtension = createApiSourceExtension()
        apiSourceExtension.tags = null

        when:
        annotationPopulator.populateExtensionsFromAnnotations(apiSourceExtension)

        then:
        apiSourceExtension.tags.size() == 2
        apiSourceExtension.tags[0].name == 'TagName 1'
        apiSourceExtension.tags[0].description == 'TagDescription 1'
        apiSourceExtension.tags[0].externalDocs.url == 'ExternalDocsUrl'
        apiSourceExtension.tags[0].externalDocs.description == 'ExternalDocsDescription'
        apiSourceExtension.tags[1].name == 'TagName 2'
        apiSourceExtension.tags[1].description == 'TagDescription 2'
        apiSourceExtension.tags[1].externalDocs == null
    }

    private ApiSourceExtension createApiSourceExtension() {
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['com.benjaminsproule.swagger.gradleplugin.swagger']
        apiSourceExtension.host = 'OriginalHost'
        apiSourceExtension.basePath = 'OriginalBasePath'

        def tagExtension = new TagExtension(mockProject)
        tagExtension.name = 'OriginalTagName'
        tagExtension.description = 'OriginalTagDescription'

        def externalDocsExtension = new ExternalDocsExtension()
        externalDocsExtension.url = 'OriginalExternalDocsUrl'
        externalDocsExtension.description = 'OriginalExternalDocsDescription'
        tagExtension.externalDocs = externalDocsExtension
        apiSourceExtension.tags = [tagExtension]

        def infoExtension = new InfoExtension(mockProject)
        infoExtension.title = 'OriginalInfoTitle'
        infoExtension.version = 'OriginalInfoVersion'
        infoExtension.termsOfService = 'OriginalInfoTermsOfService'
        infoExtension.description = 'OriginalInfoDescription'

        def contactExtension = new ContactExtension()
        contactExtension.name = 'OriginalContactName'
        contactExtension.url = 'OriginalContactUrl'
        contactExtension.email = 'OriginalContactEmail'
        infoExtension.contact = contactExtension

        def licenseExtension = new LicenseExtension()
        licenseExtension.name = 'OriginalLicenseName'
        licenseExtension.url = 'OriginalLicenseUrl'
        infoExtension.license = licenseExtension

        apiSourceExtension.info = infoExtension
        apiSourceExtension
    }

    private static void assertApiSourceExtensionPopulated(ApiSourceExtension apiSourceExtension, List<String> fields) {
        if (fields.contains('host')) {
            assert apiSourceExtension.host == 'Host'
        } else {
            assert apiSourceExtension.host == 'OriginalHost'
        }

        if (fields.contains('basePath')) {
            assert apiSourceExtension.basePath == 'BasePath'
        } else {
            assert apiSourceExtension.basePath == 'OriginalBasePath'
        }

        if (fields.contains('tags')) {
            assert apiSourceExtension.tags[0].name == 'TagName 1'
            assert apiSourceExtension.tags[0].description == 'TagDescription 1'
            assert apiSourceExtension.tags[0].externalDocs.url == 'ExternalDocsUrl'
            assert apiSourceExtension.tags[0].externalDocs.description == 'ExternalDocsDescription'
            assert apiSourceExtension.tags[1].name == 'TagName 2'
            assert apiSourceExtension.tags[1].description == 'TagDescription 2'
            assert apiSourceExtension.tags[1].externalDocs == null
        } else {
            assert apiSourceExtension.tags[0].name == 'OriginalTagName'
            assert apiSourceExtension.tags[0].description == 'OriginalTagDescription'
            assert apiSourceExtension.tags[0].externalDocs.url == 'OriginalExternalDocsUrl'
            assert apiSourceExtension.tags[0].externalDocs.description == 'OriginalExternalDocsDescription'
        }

        if (fields.contains('info')) {
            assert apiSourceExtension.info.title == 'InfoTitle'
            assert apiSourceExtension.info.version == 'InfoVersion'
            assert apiSourceExtension.info.termsOfService == 'InfoTermsOfService'
            assert apiSourceExtension.info.description == 'InfoDescription'
            assert apiSourceExtension.info.contact.name == 'ContactName'
            assert apiSourceExtension.info.contact.url == 'ContactUrl'
            assert apiSourceExtension.info.contact.email == 'ContactEmail'
            assert apiSourceExtension.info.license.name == 'LicenseName'
            assert apiSourceExtension.info.license.url == 'LicenseUrl'
        } else {
            assert apiSourceExtension.info.title == 'OriginalInfoTitle'
            assert apiSourceExtension.info.version == 'OriginalInfoVersion'
            assert apiSourceExtension.info.termsOfService == 'OriginalInfoTermsOfService'
            assert apiSourceExtension.info.description == 'OriginalInfoDescription'
            assert apiSourceExtension.info.contact.name == 'OriginalContactName'
            assert apiSourceExtension.info.contact.url == 'OriginalContactUrl'
            assert apiSourceExtension.info.contact.email == 'OriginalContactEmail'
            assert apiSourceExtension.info.license.name == 'OriginalLicenseName'
            assert apiSourceExtension.info.license.url == 'OriginalLicenseUrl'
        }
    }
}

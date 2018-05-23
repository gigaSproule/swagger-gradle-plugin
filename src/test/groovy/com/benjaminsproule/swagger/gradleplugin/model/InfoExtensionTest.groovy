package com.benjaminsproule.swagger.gradleplugin.model

import spock.lang.Specification

class InfoExtensionTest extends Specification {
    InfoExtension infoExtension

    def setup() {
        infoExtension = new InfoExtension(null)
    }

    def 'Valid info returns no errors'() {
        given:
        infoExtension.title = 'title'
        infoExtension.version = 'v1.0'
        infoExtension.license = [isValid: { new ArrayList<>() }] as LicenseExtension
        infoExtension.contact = [isValid: { new ArrayList<>() }] as ContactExtension

        when:
        def result = infoExtension.isValid()

        then:
        assert !result
    }

    def 'Valid info returns no errors with null license and contact'() {
        given:
        infoExtension.title = 'title'
        infoExtension.version = 'v1.0'
        infoExtension.license = [isValid: { new ArrayList<>() }] as LicenseExtension

        when:
        def result = infoExtension.isValid()

        then:
        assert !result
    }

    def 'Info with no title should provide missing title error'() {
        given:
        infoExtension.license = [isValid: { new ArrayList<>() }] as LicenseExtension
        infoExtension.version = 'v1.0'

        when:
        def result = infoExtension.isValid()

        then:
        assert result
        assert result.contains('info.title is required by the swagger spec')
    }

    def 'Info with no version should provide missing version error'() {
        given:
        infoExtension.license = [isValid: { new ArrayList<>() }] as LicenseExtension
        infoExtension.title = 'title'

        when:
        def result = infoExtension.isValid()

        then:
        assert result
        assert result.contains('info.version is required by the swagger spec')
    }

    def 'Info with nested errors should return them'() {
        given:
        infoExtension.title = 'title'
        infoExtension.version = 'v1.0'
        infoExtension.license = [isValid: { ['nested error'] }] as LicenseExtension

        when:
        def result = infoExtension.isValid()

        then:
        assert result
        assert result.contains('nested error')
    }

    def 'Merge Info objects'() {
        given:
        def newInfo = new InfoExtension()
        newInfo.description = 'new info description'
        newInfo.termsOfService = 'new ToS'
        newInfo.title = 'new title'
        newInfo.version = 'new version'
        newInfo.contact = new ContactExtension()
        newInfo.license = new LicenseExtension()

        when:
        infoExtension.mergeWith(newInfo)

        then:
        assert infoExtension.description == 'new info description'
        assert infoExtension.termsOfService == 'new ToS'
        assert infoExtension.title == 'new title'
        assert infoExtension.version == 'new version'
        assert infoExtension.contact
        assert infoExtension.license
    }
}

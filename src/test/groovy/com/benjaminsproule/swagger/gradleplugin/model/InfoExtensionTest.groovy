package com.benjaminsproule.swagger.gradleplugin.model

import org.junit.Before
import org.junit.Test

class InfoExtensionTest {
    InfoExtension infoExtension

    @Before
    void setup() {
        infoExtension = new InfoExtension(null)
    }

    @Test
    void 'Valid info returns no errors'() {
        infoExtension.title = 'title'
        infoExtension.version = 'v1.0'
        infoExtension.license = [isValid: { new ArrayList<>() }] as LicenseExtension

        def result = infoExtension.isValid()

        assert !result
    }

    @Test
    void 'Info with missing licence should provide missing licence error'() {
        def result = infoExtension.isValid()

        assert result
        assert result.contains('info.licence is required by the swagger spec')
    }

    @Test
    void 'Info with no title should provide missing title error'() {
        infoExtension.license = [isValid: { new ArrayList<>() }] as LicenseExtension
        infoExtension.version = 'v1.0'
        def result = infoExtension.isValid()

        assert result
        assert result.contains('info.title is required by the swagger spec')
    }

    @Test
    void 'Info with no version should provide missing version error'() {
        infoExtension.license = [isValid: { new ArrayList<>() }] as LicenseExtension
        infoExtension.title = 'title'
        def result = infoExtension.isValid()

        assert result
        assert result.contains('info.version is required by the swagger spec')
    }

    @Test
    void 'Info with nested errors should return them'() {
        infoExtension.title = 'title'
        infoExtension.version = 'v1.0'
        infoExtension.license = [isValid: { ['nested error'] }] as LicenseExtension

        def result = infoExtension.isValid()

        assert result
        assert result.contains('nested error')
    }

    @Test
    void 'Merge Info objects'() {
        def newInfo = new InfoExtension()
        newInfo.description = 'new info description'
        newInfo.termsOfService = 'new ToS'
        newInfo.title = 'new title'
        newInfo.version = 'new version'
        newInfo.contact = new ContactExtension()
        newInfo.license = new LicenseExtension()

        infoExtension.mergeWith(newInfo)

        assert infoExtension.description == 'new info description'
        assert infoExtension.termsOfService == 'new ToS'
        assert infoExtension.title == 'new title'
        assert infoExtension.version == 'new version'
        assert infoExtension.contact
        assert infoExtension.license
    }
}

package com.benjaminsproule.swagger.gradleplugin.model

import spock.lang.Specification

class LicenseExtensionTest extends Specification {
    LicenseExtension licenseExtension

    def setup() {
        licenseExtension = new LicenseExtension()
    }

    def 'Valid licence validation returns no errors'() {
        given:
        licenseExtension.name = 'name'

        when:
        def result = licenseExtension.isValid()

        then:
        assert !result
    }

    def 'Swagger extension with missing name should provide missing name error'() {
        when:
        def result = licenseExtension.isValid()

        then:
        assert result
        assert result.contains('info.licence.name is required by the swagger spec')
    }
}

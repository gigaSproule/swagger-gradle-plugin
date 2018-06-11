package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.LicenseExtension
import spock.lang.Specification

class LicenseValidatorTest extends Specification {
    def 'isValid returns error message if name not set'() {
        when:
        def errors = new LicenseValidator().isValid(new LicenseExtension())

        then:
        errors.size() == 1
        errors[0] == 'info.licence.name is required by the swagger spec'
    }

    def 'isValid returns empty list if name set'() {
        given:
        def licenseExtension = new LicenseExtension()
        licenseExtension.name = 'name'

        when:
        def errors = new LicenseValidator().isValid(licenseExtension)

        then:
        errors.size() == 0
    }
}

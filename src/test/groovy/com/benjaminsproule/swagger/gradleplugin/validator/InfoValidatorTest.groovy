package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.InfoExtension
import com.benjaminsproule.swagger.gradleplugin.model.LicenseExtension
import spock.lang.Specification

class InfoValidatorTest extends Specification {

    private LicenseValidator mockLicenseValidator
    private InfoValidator infoValidator

    def setup() {
        mockLicenseValidator = Mock(LicenseValidator)
        infoValidator = new InfoValidator(mockLicenseValidator)
    }

    def 'isValid returns error message if title not set'() {
        given:
        def infoExtension = new InfoExtension()
        infoExtension.version = '1.0'

        1 * mockLicenseValidator.isValid(_) >> []

        when:
        def errors = infoValidator.isValid(infoExtension)

        then:
        errors.size() == 1
        errors[0] == 'info.title is required by the swagger spec'
    }

    def 'isValid returns error message if version not set'() {
        given:
        def infoExtension = new InfoExtension()
        infoExtension.title = 'title'

        1 * mockLicenseValidator.isValid(_) >> []

        when:
        def errors = infoValidator.isValid(infoExtension)

        then:
        errors.size() == 1
        errors[0] == 'info.version is required by the swagger spec'
    }

    def 'isValid returns error messages if title and version not set'() {
        given:
        1 * mockLicenseValidator.isValid(_) >> []

        when:
        def errors = infoValidator.isValid(new InfoExtension())

        then:
        errors.size() == 2
        errors[0] == 'info.title is required by the swagger spec'
        errors[1] == 'info.version is required by the swagger spec'
    }

    def 'isValid returns empty list if title and version set'() {
        given:
        def infoExtension = new InfoExtension()
        infoExtension.title = 'title'
        infoExtension.version = '1.0'

        1 * mockLicenseValidator.isValid(_) >> []

        when:
        def errors = infoValidator.isValid(infoExtension)

        then:
        errors.size() == 0
    }

    def 'Returns errors from other validators'() {
        given:
        def infoExtension = new InfoExtension()
        infoExtension.title = 'title'
        infoExtension.version = '1.0'
        infoExtension.license = new LicenseExtension()

        1 * mockLicenseValidator.isValid(_) >> ['license validator error 1', 'license validator error 2']

        when:
        def errors = infoValidator.isValid(infoExtension)

        then:
        errors.size() == 2
        errors[0] == 'license validator error 1'
        errors[1] == 'license validator error 2'
    }
}

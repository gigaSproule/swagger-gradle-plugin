package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.InfoExtension
import spock.lang.Specification

class InfoValidatorTest extends Specification {
    def 'isValid returns error message if title not set'() {
        given:
        def infoExtension = new InfoExtension()
        infoExtension.version = '1.0'

        when:
        def errors = new InfoValidator().isValid(infoExtension)

        then:
        errors.size() == 1
        errors[0] == 'info.title is required by the swagger spec'
    }

    def 'isValid returns error message if version not set'() {
        given:
        def infoExtension = new InfoExtension()
        infoExtension.title = 'title'

        when:
        def errors = new InfoValidator().isValid(infoExtension)

        then:
        errors.size() == 1
        errors[0] == 'info.version is required by the swagger spec'
    }

    def 'isValid returns error messages if title and version not set'() {
        when:
        def errors = new InfoValidator().isValid(new InfoExtension())

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

        when:
        def errors = new InfoValidator().isValid(infoExtension)

        then:
        errors.size() == 0
    }
}

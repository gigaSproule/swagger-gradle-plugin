package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import spock.lang.Specification

class ApiSourceValidatorTest extends Specification {

    def 'isValid returns error message if locations not set'() {
        when:
        def errors = new ApiSourceValidator().isValid(new ApiSourceExtension())

        then:
        errors.size() == 1
        errors[0] == 'locations required, specify classes or packages where swagger annotated classes are located'
    }

    def 'isValid returns error message if locations is empty'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = []

        when:
        def errors = new ApiSourceValidator().isValid(apiSourceExtension)

        then:
        errors.size() == 1
        errors[0] == 'locations required, specify classes or packages where swagger annotated classes are located'
    }

    def 'isValid returns error message if schemes is set and not http, https, ws or wss'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['locations']
        apiSourceExtension.schemes = ['schemes']

        when:
        def errors = new ApiSourceValidator().isValid(apiSourceExtension)

        then:
        errors.size() == 1
        errors[0] == 'schemes must be either "http", "https", "ws" or "wss"'
    }

    def 'isValid returns error message if schemes has a valid and invalid entry'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['locations']
        apiSourceExtension.schemes = ['http', 'schemes']

        when:
        def errors = new ApiSourceValidator().isValid(apiSourceExtension)

        then:
        errors.size() == 1
        errors[0] == 'schemes must be either "http", "https", "ws" or "wss"'
    }

    def 'isValid returns error messages if locations not set and schemes invalid'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.schemes = ['invalid']

        when:
        def errors = new ApiSourceValidator().isValid(apiSourceExtension)

        then:
        errors.size() == 2
        errors[0] == 'locations required, specify classes or packages where swagger annotated classes are located'
        errors[1] == 'schemes must be either "http", "https", "ws" or "wss"'
    }

    def 'isValid returns empty list if locations set'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['location']

        when:
        def errors = new ApiSourceValidator().isValid(apiSourceExtension)

        then:
        errors.size() == 0
    }
}

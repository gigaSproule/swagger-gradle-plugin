package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import spock.lang.Specification

class SwaggerValidatorTest extends Specification {
    def 'isValid returns error message if apiSourceExtensions not set'() {
        when:
        def errors = new SwaggerValidator().isValid(new SwaggerExtension())

        then:
        errors.size() == 1
        errors[0] == 'You must specify at least one apiSource element'
    }

    def 'isValid returns error message if apiSourceExtensions is empty'() {
        given:
        def swaggerExtension = new SwaggerExtension()
        swaggerExtension.apiSourceExtensions = []

        when:
        def errors = new SwaggerValidator().isValid(swaggerExtension)

        then:
        errors.size() == 1
        errors[0] == 'You must specify at least one apiSource element'
    }

    def 'isValid returns empty list if apiSourceExtensions set'() {
        given:
        def swaggerExtension = new SwaggerExtension()
        swaggerExtension.apiSourceExtensions = [new ApiSourceExtension()]

        when:
        def errors = new SwaggerValidator().isValid(swaggerExtension)

        then:
        errors.size() == 0
    }
}

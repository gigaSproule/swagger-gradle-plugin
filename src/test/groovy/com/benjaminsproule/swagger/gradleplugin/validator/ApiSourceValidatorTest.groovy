package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.model.InfoExtension
import com.benjaminsproule.swagger.gradleplugin.model.SecurityDefinitionExtension
import com.benjaminsproule.swagger.gradleplugin.model.TagExtension
import spock.lang.Specification

class ApiSourceValidatorTest extends Specification {

    private InfoValidator mockInfoValidator
    private SecurityDefinitionValidator mockSecurityDefinitionValidator
    private TagValidator mockTagValidator
    private ApiSourceValidator apiSourceValidator

    def setup() {
        mockInfoValidator = Mock(InfoValidator)
        mockSecurityDefinitionValidator = Mock(SecurityDefinitionValidator)
        mockTagValidator = Mock(TagValidator)
        apiSourceValidator = new ApiSourceValidator(mockInfoValidator, mockSecurityDefinitionValidator, mockTagValidator)
    }

    def 'isValid returns error message if locations not set'() {
        given:
        1 * mockInfoValidator.isValid(_) >> []
        0 * mockSecurityDefinitionValidator.isValid(_) >> []
        0 * mockTagValidator.isValid(_) >> []

        when:
        def errors = apiSourceValidator.isValid(new ApiSourceExtension())

        then:
        errors.size() == 1
        errors[0] == 'locations required, specify classes or packages where swagger annotated classes are located'
    }

    def 'isValid returns error message if locations is empty'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = []

        1 * mockInfoValidator.isValid(_) >> []
        0 * mockSecurityDefinitionValidator.isValid(_) >> []
        0 * mockTagValidator.isValid(_) >> []

        when:
        def errors = apiSourceValidator.isValid(apiSourceExtension)

        then:
        errors.size() == 1
        errors[0] == 'locations required, specify classes or packages where swagger annotated classes are located'
    }

    def 'isValid returns error message if schemes is set and not http, https, ws or wss'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['locations']
        apiSourceExtension.schemes = ['schemes']

        1 * mockInfoValidator.isValid(_) >> []
        0 * mockSecurityDefinitionValidator.isValid(_) >> []
        0 * mockTagValidator.isValid(_) >> []

        when:
        def errors = apiSourceValidator.isValid(apiSourceExtension)

        then:
        errors.size() == 1
        errors[0] == 'schemes must be either "http", "https", "ws" or "wss"'
    }

    def 'isValid returns error message if schemes has a valid and invalid entry'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['locations']
        apiSourceExtension.schemes = ['http', 'schemes']

        1 * mockInfoValidator.isValid(_) >> []
        0 * mockSecurityDefinitionValidator.isValid(_) >> []
        0 * mockTagValidator.isValid(_) >> []

        when:
        def errors = apiSourceValidator.isValid(apiSourceExtension)

        then:
        errors.size() == 1
        errors[0] == 'schemes must be either "http", "https", "ws" or "wss"'
    }

    def 'isValid returns error messages if locations not set and schemes invalid'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.schemes = ['invalid']

        1 * mockInfoValidator.isValid(_) >> []
        0 * mockSecurityDefinitionValidator.isValid(_) >> []
        0 * mockTagValidator.isValid(_) >> []

        when:
        def errors = apiSourceValidator.isValid(apiSourceExtension)

        then:
        errors.size() == 2
        errors[0] == 'locations required, specify classes or packages where swagger annotated classes are located'
        errors[1] == 'schemes must be either "http", "https", "ws" or "wss"'
    }

    def 'isValid returns empty list if locations set'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['location']

        1 * mockInfoValidator.isValid(_) >> []
        0 * mockSecurityDefinitionValidator.isValid(_) >> []
        0 * mockTagValidator.isValid(_) >> []

        when:
        def errors = apiSourceValidator.isValid(apiSourceExtension)

        then:
        errors.size() == 0
    }

    def 'Returns errors from other validators'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = ['location']
        apiSourceExtension.info = new InfoExtension()
        apiSourceExtension.tags = [new TagExtension()]
        apiSourceExtension.securityDefinition = [new SecurityDefinitionExtension()]

        1 * mockInfoValidator.isValid(_) >> ['info validator error 1', 'info validator error 2']
        1 * mockSecurityDefinitionValidator.isValid(_) >> ['security definition validator error 1', 'security definition validator error 2']
        1 * mockTagValidator.isValid(_) >> ['tag validator error 1', 'tag validator error 2']

        when:
        def errors = apiSourceValidator.isValid(apiSourceExtension)

        then:
        errors.size() == 6
        errors[0] == 'info validator error 1'
        errors[1] == 'info validator error 2'
        errors[2] == 'security definition validator error 1'
        errors[3] == 'security definition validator error 2'
        errors[4] == 'tag validator error 1'
        errors[5] == 'tag validator error 2'
    }
}

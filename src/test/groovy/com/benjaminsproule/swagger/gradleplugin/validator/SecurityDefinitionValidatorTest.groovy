package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.SecurityDefinitionExtension
import spock.lang.Specification

class SecurityDefinitionValidatorTest extends Specification {

    def 'isValid returns empty list security defintion not provided'() {
        when:
        def errors = new SecurityDefinitionValidator().isValid(null)

        then:
        errors.size() == 0
    }

    def 'isValid returns error message if name, type, json and jsonPath not set'() {
        when:
        def errors = new SecurityDefinitionValidator().isValid(new SecurityDefinitionExtension())

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.name and securityDefinition.type OR securityDefinition.json OR securityDefinition.jsonPath is required by the swagger spec'
    }

    def 'isValid returns error message if name, json and jsonPath not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.type = 'type'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.name is required by the swagger spec'
    }

    def 'isValid returns error message if type, json and jsonPath not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.type is required by the swagger spec'
    }

    def 'isValid returns error message if type is apiKey and keyLocation is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.keyName = 'keyName'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.keyLocation is required by the swagger spec and must be either "query" or "header"'
    }

    def 'isValid returns error message if type is apiKey and keyLocation is not query or header'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.keyLocation = 'keyLocation'
        securityDefinitionExtension.keyName = 'keyName'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.keyLocation is required by the swagger spec and must be either "query" or "header"'
    }

    def 'isValid returns error message if type is apiKey and keyName is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.keyLocation = 'query'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.keyName is required by the swagger spec'
    }

    def 'isValid returns empty list if type and name are set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.type = 'type'
        securityDefinitionExtension.name = 'name'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }

    def 'isValid returns empty list if json set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.json = 'json'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }

    def 'isValid returns empty list if jsonPath set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.jsonPath = 'jsonPath'

        when:
        def errors = new SecurityDefinitionValidator().isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }
}

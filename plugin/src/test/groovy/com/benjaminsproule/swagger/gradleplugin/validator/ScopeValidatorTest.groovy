package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ScopeExtension
import spock.lang.Specification

class ScopeValidatorTest extends Specification {

    def 'isValid returns error messages if name is not set'() {
        given:
        def scopeExtension = new ScopeExtension()
        scopeExtension.description = 'description'

        when:
        def errors = new ScopeValidator().isValid(scopeExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.scopes.name is required by the swagger spec for OAuth 2.0'
    }

    def 'isValid returns error messages if description is not set'() {
        given:
        def scopeExtension = new ScopeExtension()
        scopeExtension.name = 'name'

        when:
        def errors = new ScopeValidator().isValid(scopeExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.scopes.description is required by the swagger spec for OAuth 2.0'
    }

    def 'isValid returns error messages if name and description is not set'() {
        when:
        def errors = new ScopeValidator().isValid(new ScopeExtension())

        then:
        errors.size() == 2
        errors[0] == 'securityDefinition.scopes.name is required by the swagger spec for OAuth 2.0'
        errors[1] == 'securityDefinition.scopes.description is required by the swagger spec for OAuth 2.0'
    }

    def 'isValid returns empty list if name and description is set'() {
        given:
        def scopeExtension = new ScopeExtension()
        scopeExtension.name = 'name'
        scopeExtension.description = 'description'

        when:
        def errors = new ScopeValidator().isValid(scopeExtension)

        then:
        errors.size() == 0
    }

}

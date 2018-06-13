package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ScopeExtension
import com.benjaminsproule.swagger.gradleplugin.model.SecurityDefinitionExtension
import spock.lang.Specification

class SecurityDefinitionValidatorTest extends Specification {

    private ScopeValidator mockScopeValidator
    private SecurityDefinitionValidator securityDefinitionValidator

    def setup() {
        mockScopeValidator = Mock(ScopeValidator)
        securityDefinitionValidator = new SecurityDefinitionValidator(mockScopeValidator)
    }

    def 'isValid returns empty list security definition not provided'() {
        when:
        def errors = securityDefinitionValidator.isValid(null)

        then:
        errors.size() == 0
    }

    def 'isValid returns error message if name, type and json not set'() {
        when:
        def errors = securityDefinitionValidator.isValid(new SecurityDefinitionExtension())

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.name and securityDefinition.type OR securityDefinition.json is required by the swagger spec'
    }

    def 'isValid returns error message if name is not provided'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.type = 'basic'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.name is required by the swagger spec'
    }

    def 'isValid returns error message if type is not basic, apiKey or oauth2'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'type'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.type is required by the swagger spec and must be either "basic", "apiKey" or "oauth2"'
    }

    def 'isValid returns empty list if type is set to basic'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'basic'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }

    def 'isValid returns error message if type is apiKey and keyLocation is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.keyName = 'keyName'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.keyLocation is required by the swagger spec and must be either "query" or "header" for API key'
    }

    def 'isValid returns error message if type is apiKey and keyLocation is not query or header'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.keyLocation = 'keyLocation'
        securityDefinitionExtension.keyName = 'keyName'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.keyLocation is required by the swagger spec and must be either "query" or "header" for API key'
    }

    def 'isValid returns error message if type is apiKey and keyName is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.keyLocation = 'query'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.keyName is required by the swagger spec for API key'
    }

    def 'isValid returns error messages if type is oauth2 and flow is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.tokenUrl = 'tokenUrl'
        securityDefinitionExtension.authorizationUrl = 'tokenUrl'
        securityDefinitionExtension.scopes = [new ScopeExtension()]

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.flow is required by the swagger spec for OAuth 2.0'
    }

    def 'isValid returns error messages if type is oauth2, flow is accessCode and authorizationUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.flow = 'accessCode'
        securityDefinitionExtension.tokenUrl = 'tokenUrl'
        securityDefinitionExtension.scopes = [new ScopeExtension()]

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.authorizationUrl is required by the swagger spec for OAuth 2.0'
    }

    def 'isValid returns error messages if type is oauth2, flow is implicit and authorizationUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.flow = 'implicit'
        securityDefinitionExtension.scopes = [new ScopeExtension()]

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.authorizationUrl is required by the swagger spec for OAuth 2.0'
    }

    def 'isValid returns empty list if type is oauth2, flow is application and authorizationUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.flow = 'application'
        securityDefinitionExtension.tokenUrl = 'tokenUrl'
        securityDefinitionExtension.scopes = [new ScopeExtension()]

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }

    def 'isValid returns empty list if type is oauth2, flow is password and authorizationUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.flow = 'password'
        securityDefinitionExtension.tokenUrl = 'tokenUrl'
        securityDefinitionExtension.scopes = [new ScopeExtension()]

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }

    def 'isValid returns error messages if type is oauth2 and scopes is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.authorizationUrl = 'authorizationUrl'
        securityDefinitionExtension.flow = 'implicit'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.scopes is required by the swagger spec for OAuth 2.0'
    }

    def 'isValid returns error messages if type is oauth2, flow is accessCode and tokenUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.scopes = [new ScopeExtension()]
        securityDefinitionExtension.authorizationUrl = 'authorizationUrl'
        securityDefinitionExtension.flow = 'accessCode'

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.tokenUrl is required by the swagger spec for access code OAuth 2.0 when the flow is either accessCode, application or password'
    }

    def 'isValid returns error messages if type is oauth2, flow is application and tokenUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.scopes = [new ScopeExtension()]
        securityDefinitionExtension.authorizationUrl = 'authorizationUrl'
        securityDefinitionExtension.flow = 'application'

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.tokenUrl is required by the swagger spec for access code OAuth 2.0 when the flow is either accessCode, application or password'
    }

    def 'isValid returns error messages if type is oauth2, flow is password and tokenUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.scopes = [new ScopeExtension()]
        securityDefinitionExtension.authorizationUrl = 'authorizationUrl'
        securityDefinitionExtension.flow = 'password'

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 1
        errors[0] == 'securityDefinition.tokenUrl is required by the swagger spec for access code OAuth 2.0 when the flow is either accessCode, application or password'
    }

    def 'isValid returns empty list if type is oauth2, flow is implicit and tokenUrl is not set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.scopes = [new ScopeExtension()]
        securityDefinitionExtension.authorizationUrl = 'authorizationUrl'
        securityDefinitionExtension.flow = 'implicit'

        1 * mockScopeValidator.isValid(_) >> []

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }

    def 'Returns errors from other validators'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.name = 'name'
        securityDefinitionExtension.type = 'oauth2'
        securityDefinitionExtension.scopes = [new ScopeExtension()]
        securityDefinitionExtension.authorizationUrl = 'authorizationUrl'
        securityDefinitionExtension.tokenUrl = 'tokenUrl'
        securityDefinitionExtension.flow = 'accessCode'

        1 * mockScopeValidator.isValid(_) >> ['scope validator error 1', 'scope validator error 2']

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 2
        errors[0] == 'scope validator error 1'
        errors[1] == 'scope validator error 2'
    }

    def 'isValid returns empty list if json set'() {
        given:
        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.json = 'json'

        when:
        def errors = securityDefinitionValidator.isValid(securityDefinitionExtension)

        then:
        errors.size() == 0
    }
}

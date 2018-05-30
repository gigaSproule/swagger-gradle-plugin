package com.benjaminsproule.swagger.gradleplugin.model

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import io.swagger.models.auth.In
import io.swagger.models.auth.SecuritySchemeDefinition
import spock.lang.Specification

class SecurityDefinitionExtensionTest extends Specification {
    SecurityDefinitionExtension securityDefinitionExtension
    private ClassFinder classFinder

    def setup() {
        classFinder = Mock(ClassFinder)
        securityDefinitionExtension = new SecurityDefinitionExtension(classFinder)
    }

    def 'Valid security definition of type basic returns no errors'() {
        given:
        securityDefinitionExtension.name = 'secure'
        securityDefinitionExtension.type = 'basic'

        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert !result
    }

    def 'Valid security definition of type apiKey returns no errors'() {
        given:
        securityDefinitionExtension.name = 'ApiKeyAuth'
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.keyName = 'X-API-Key'
        securityDefinitionExtension.keyLocation = 'header'

        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert !result
    }


    def 'Security definition with missing name source should provide missing name error'() {
        given:
        securityDefinitionExtension.type = 'basic'

        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert result
        assert result.contains('You must specify name and type')
    }

    def 'Security definition with missing type source should provide missing type error'() {
        given:
        securityDefinitionExtension.name = 'secure'

        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert result
        assert result.contains('You must specify name and type')
    }

    def 'Security definition with no content should provide missing data error'() {
        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert result
        assert result.contains('Security definition must specify json or jsonPath or (name and type)')
    }

    def 'Security definition of type apiKey without keyLocation should provide missing data error'() {
        given:
        securityDefinitionExtension.name = 'ApiKeyAuth'
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.keyName = 'X-API-Key'

        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert result
        assert result.contains('When type is "apiKey" - you must specify keyLocation and keyName')
    }

    def 'Security definition of type apiKey without keyName should provide missing data error'() {
        given:
        securityDefinitionExtension.name = 'ApiKeyAuth'
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.keyLocation = 'header'

        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert result
        assert result.contains('When type is "apiKey" - you must specify keyLocation and keyName')
    }

    def 'Security definition of type apiKey with bad keyLocation should provide missing data error'() {
        given:
        securityDefinitionExtension.name = 'ApiKeyAuth'
        securityDefinitionExtension.type = 'apiKey'
        securityDefinitionExtension.keyName = 'X-API-Key'
        securityDefinitionExtension.keyLocation = 'unknownproperty'

        when:
        def result = securityDefinitionExtension.isValid()

        then:
        assert result
        assert result.contains('When type is "apiKey" - keyLocation must be "query" or "header"')
    }

    def 'Load Security Definitions from file'() {
        given:
        securityDefinitionExtension.json = 'security-definition/securityDefinitionExtensionTest.json'
        def classLoader = Mock(ClassLoader)
        classFinder.getClassLoader() >> classLoader
        classLoader.getResourceAsStream(securityDefinitionExtension.json) >> getClass().getClassLoader().getResourceAsStream(securityDefinitionExtension.json)

        when:
        def result = securityDefinitionExtension.asSwaggerType()

        then:
        assert result
        assert result.size() == 3
        assertBasic(result.get('basic'))
        assertApiKey(result.get('api_key'))
        assertOauth(result.get('petstore_auth'))
    }

    private static void assertBasic(SecuritySchemeDefinition definition) {
        assert definition
        assert definition.type == 'basic'
    }

    private static void assertApiKey(SecuritySchemeDefinition definition) {
        assert definition
        assert definition.type == 'apiKey'
        assert definition.in == In.HEADER
        assert definition.name == 'X-API-Key'
    }

    private static void assertOauth(SecuritySchemeDefinition definition) {
        assert definition
        assert definition.type == 'oauth2'
        assert definition.flow == 'implicit'
        assert definition.authorizationUrl == 'http://swagger.io/api/oauth/dialog'
        assert definition.scopes
        assert definition.scopes['write:pets'] == 'modify pets in your account'
        assert definition.scopes['read:pets'] == 'read your pets'
    }
}

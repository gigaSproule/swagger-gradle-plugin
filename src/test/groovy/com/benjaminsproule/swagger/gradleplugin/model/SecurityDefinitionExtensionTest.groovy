package com.benjaminsproule.swagger.gradleplugin.model

import io.swagger.models.auth.SecuritySchemeDefinition
import org.junit.Before
import org.junit.Test

class SecurityDefinitionExtensionTest {
    SecurityDefinitionExtension securityDefinitionExtension

    @Before
    void setup() {
        securityDefinitionExtension = new SecurityDefinitionExtension()
    }

    @Test
    void 'Valid security definition returns no errors'() {
        securityDefinitionExtension.name = 'secure'
        securityDefinitionExtension.type = 'basic'

        def result = securityDefinitionExtension.isValid()

        assert !result
    }

    @Test
    void 'Security definition with missing name source should provide missing name error'() {
        securityDefinitionExtension.type = 'basic'
        def result = securityDefinitionExtension.isValid()

        assert result
        assert result.contains('You must specify name and type')
    }

    @Test
    void 'Security definition with missing type source should provide missing type error'() {
        securityDefinitionExtension.name = 'secure'
        def result = securityDefinitionExtension.isValid()

        assert result
        assert result.contains('You must specify name and type')
    }

    @Test
    void 'Security definition with no content should provide missing data error'() {
        def result = securityDefinitionExtension.isValid()

        assert result
        assert result.contains('Security definition must specify json or jsonPath or (name and type)')
    }

    @Test
    void 'Load Security Definitions from file'() {
        securityDefinitionExtension.json = '/security-definition/securityDefinitionExtensionTest.json'

        def result = securityDefinitionExtension.asSwaggerType()

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
        assert definition.in
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

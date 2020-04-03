package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.SecurityDefinitionExtension

class SecurityDefinitionValidator implements ModelValidator<SecurityDefinitionExtension> {

    private ScopeValidator scopeValidator

    SecurityDefinitionValidator(ScopeValidator scopeValidator) {
        this.scopeValidator = scopeValidator
    }

    @Override
    List<String> isValid(SecurityDefinitionExtension securityDefinitionExtension) {
        if (!securityDefinitionExtension || securityDefinitionExtension.json) {
            return []
        }

        if (!securityDefinitionExtension.name && !securityDefinitionExtension.type && !securityDefinitionExtension.json) {
            return ['securityDefinition.name and securityDefinition.type OR securityDefinition.json is required by the swagger spec']
        }

        def errors = []

        if (!securityDefinitionExtension.name) {
            errors += 'securityDefinition.name is required by the swagger spec'
        }

        if (securityDefinitionExtension.type != 'basic' && securityDefinitionExtension.type != 'apiKey' && securityDefinitionExtension.type != 'oauth2') {
            errors += ['securityDefinition.type is required by the swagger spec and must be either "basic", "apiKey" or "oauth2"']
        }

        if (securityDefinitionExtension.type == 'apiKey') {
            errors += isValidApiKey(securityDefinitionExtension)
        } else if (securityDefinitionExtension.type == 'oauth2') {
            errors += isValidOauth2(securityDefinitionExtension)
        }

        return errors
    }

    private static def isValidApiKey(SecurityDefinitionExtension securityDefinitionExtension) {
        def errors = []

        if (securityDefinitionExtension.keyLocation != 'query' && securityDefinitionExtension.keyLocation != 'header') {
            errors += 'securityDefinition.keyLocation is required by the swagger spec and must be either "query" or "header" for API key'
        }

        if (!securityDefinitionExtension.keyName) {
            errors += 'securityDefinition.keyName is required by the swagger spec for API key'
        }

        errors
    }

    private def isValidOauth2(SecurityDefinitionExtension securityDefinitionExtension) {
        def errors = []

        if (!securityDefinitionExtension.flow) {
            errors += 'securityDefinition.flow is required by the swagger spec for OAuth 2.0'
        }

        if ((securityDefinitionExtension.flow == 'accessCode' || securityDefinitionExtension.flow == 'implicit')
            && !securityDefinitionExtension.authorizationUrl) {
            errors += 'securityDefinition.authorizationUrl is required by the swagger spec for OAuth 2.0'
        }

        if (!securityDefinitionExtension.getScopeExtensions()) {
            errors += 'securityDefinition.scopes is required by the swagger spec for OAuth 2.0'
        } else {
            securityDefinitionExtension.getScopeExtensions().each { scope ->
                errors += scopeValidator.isValid(scope)
            }
        }

        if ((securityDefinitionExtension.flow == 'accessCode' || securityDefinitionExtension.flow == 'password' || securityDefinitionExtension.flow == 'application')
            && !securityDefinitionExtension.tokenUrl) {
            errors += 'securityDefinition.tokenUrl is required by the swagger spec for access code OAuth 2.0 when the flow is either accessCode, application or password'
        }

        errors
    }
}

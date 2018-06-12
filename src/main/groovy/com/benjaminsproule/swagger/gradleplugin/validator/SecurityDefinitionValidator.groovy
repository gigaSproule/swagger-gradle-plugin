package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.SecurityDefinitionExtension

class SecurityDefinitionValidator implements ModelValidator<SecurityDefinitionExtension> {

    @Override
    List<String> isValid(SecurityDefinitionExtension securityDefinitionExtension) {
        if (!securityDefinitionExtension || securityDefinitionExtension.json || securityDefinitionExtension.jsonPath) {
            return []
        }

        def errors = []
        if (!securityDefinitionExtension.name && !securityDefinitionExtension.type && !securityDefinitionExtension.json && !securityDefinitionExtension.jsonPath) {
            return ['securityDefinition.name and securityDefinition.type OR securityDefinition.json OR securityDefinition.jsonPath is required by the swagger spec']
        }

        if (!securityDefinitionExtension.name) {
            errors += 'securityDefinition.name is required by the swagger spec'
        }

        if (!securityDefinitionExtension.type) {
            errors += 'securityDefinition.type is required by the swagger spec'
        }

        if (securityDefinitionExtension.type == 'apiKey') {
            errors += isValidApiKey(securityDefinitionExtension)
        }

        return errors
    }

    private static def isValidApiKey(SecurityDefinitionExtension securityDefinitionExtension) {
        def errors = []
        if (securityDefinitionExtension.keyLocation != 'query' && securityDefinitionExtension.keyLocation != 'header') {
            errors += 'securityDefinition.keyLocation is required by the swagger spec and must be either "query" or "header"'
        }
        if (!securityDefinitionExtension.keyName) {
            errors += 'securityDefinition.keyName is required by the swagger spec'
        }
        errors
    }
}

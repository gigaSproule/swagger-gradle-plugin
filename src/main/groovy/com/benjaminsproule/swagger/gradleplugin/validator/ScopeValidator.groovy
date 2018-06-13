package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ScopeExtension

class ScopeValidator implements ModelValidator<ScopeExtension> {
    @Override
    List<String> isValid(ScopeExtension scopeExtension) {
        def errors = []

        if (!scopeExtension.name) {
            errors += 'securityDefinition.scopes.name is required by the swagger spec for OAuth 2.0'
        }

        if (!scopeExtension.description) {
            errors += 'securityDefinition.scopes.description is required by the swagger spec for OAuth 2.0'
        }

        errors
    }
}

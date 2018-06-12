package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ExternalDocsExtension

class ExternalDocsValidator implements ModelValidator<ExternalDocsExtension> {
    List<String> isValid(ExternalDocsExtension externalDocsExtension) {
        if (!externalDocsExtension) {
            return []
        }

        def errors = []

        if (!externalDocsExtension.url) {
            errors += 'tag.externalDocs.url is required by the swagger spec'
        }

        errors
    }
}

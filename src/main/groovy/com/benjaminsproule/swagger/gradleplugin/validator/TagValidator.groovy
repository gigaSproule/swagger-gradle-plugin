package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.TagExtension

class TagValidator implements ModelValidator<TagExtension> {

    private ExternalDocsValidator externalDocsValidator

    TagValidator(ExternalDocsValidator externalDocsValidator) {
        this.externalDocsValidator = externalDocsValidator
    }

    List<String> isValid(TagExtension tagExtension) {
        if (!tagExtension) {
            return []
        }

        def errors = []

        if (!tagExtension.name) {
            errors += 'tag.name is required by the swagger spec'
        }

        errors += externalDocsValidator.isValid(tagExtension.externalDocs)

        errors
    }
}

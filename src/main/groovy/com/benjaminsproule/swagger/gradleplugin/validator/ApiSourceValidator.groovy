package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension

class ApiSourceValidator implements ModelValidator<ApiSourceExtension> {

    private InfoValidator infoValidator
    private SecurityDefinitionValidator securityDefinitionValidator
    private TagValidator tagValidator

    ApiSourceValidator(InfoValidator infoValidator, SecurityDefinitionValidator securityDefinitionValidator, TagValidator tagValidator) {
        this.infoValidator = infoValidator
        this.securityDefinitionValidator = securityDefinitionValidator
        this.tagValidator = tagValidator
    }

    @Override
    List<String> isValid(ApiSourceExtension apiSourceExtension) {
        def errors = []
        if (!apiSourceExtension.locations) {
            errors += 'locations required, specify classes or packages where swagger annotated classes are located'
        }

        for (def scheme : apiSourceExtension.schemes) {
            if (scheme != 'http' && scheme != 'https' && scheme != 'ws' && scheme != 'wss') {
                errors += 'schemes must be either "http", "https", "ws" or "wss"'
                break
            }
        }

        errors += infoValidator.isValid(apiSourceExtension.info)
        errors += securityDefinitionValidator.isValid(apiSourceExtension.securityDefinition)
        apiSourceExtension.tags.each { tag ->
            errors += tagValidator.isValid(tag)
        }

        return errors
    }
}

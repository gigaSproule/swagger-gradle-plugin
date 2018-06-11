package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension

class ApiSourceValidator implements ModelValidator<ApiSourceExtension> {
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

        return errors
    }
}

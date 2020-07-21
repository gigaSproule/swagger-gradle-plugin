package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension

class SwaggerValidator implements ModelValidator<SwaggerExtension> {
    @Override
    List<String> isValid(SwaggerExtension swaggerExtension) {
        if (!swaggerExtension.apiSourceExtensions) {
            return ['You must specify at least one apiSource element']
        }

        return []
    }
}

package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.LicenseExtension

class LicenseValidator implements ModelValidator<LicenseExtension> {
    @Override
    List<String> isValid(LicenseExtension licenseExtension) {
        if (!licenseExtension) {
            return []
        }

        if (!licenseExtension.name) {
            return ['info.licence.name is required by the swagger spec']
        }

        return []
    }
}

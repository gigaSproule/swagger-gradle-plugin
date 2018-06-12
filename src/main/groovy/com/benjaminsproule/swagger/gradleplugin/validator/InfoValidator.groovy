package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.InfoExtension

class InfoValidator implements ModelValidator<InfoExtension> {

    private LicenseValidator licenseValidator

    InfoValidator(LicenseValidator licenseValidator) {
        this.licenseValidator = licenseValidator
    }

    @Override
    List<String> isValid(InfoExtension infoExtension) {
        def errors = []
        if (!infoExtension.title) {
            errors += 'info.title is required by the swagger spec'
        }

        if (!infoExtension.version) {
            errors += 'info.version is required by the swagger spec'
        }

        errors += licenseValidator.isValid(infoExtension.license)

        return errors
    }
}

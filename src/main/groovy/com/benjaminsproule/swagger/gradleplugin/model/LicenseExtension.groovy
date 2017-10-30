package com.benjaminsproule.swagger.gradleplugin.model

import groovy.transform.ToString
import io.swagger.models.License

@ToString(includeNames = true)
class LicenseExtension implements ModelValidator {
    String name
    String url

    License getSwaggerLicence() {
        License license = new License()
        license.setName(name)
        license.setUrl(url)

        return license
    }

    @Override
    List<String> isValid() {
        if (!name) {
            return ['info.licence.name is required by the swagger spec']
        }

        return []
    }
}

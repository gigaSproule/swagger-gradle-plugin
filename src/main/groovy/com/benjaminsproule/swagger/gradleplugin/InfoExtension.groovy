package com.benjaminsproule.swagger.gradleplugin

import groovy.transform.ToString
import io.swagger.models.License

@ToString(includeNames = true)
class InfoExtension {
    String title
    String version
    License license

    void license(Closure closure) {
        License license = new License()
        license.setName(closure.name)
        this.license = license
    }
}

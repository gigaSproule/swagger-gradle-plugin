package com.github.swagger.docgen.gradleplugin

import groovy.transform.ToString

/**
 * SwaggerPluginExtension
 */
@ToString(includeNames = true)
class SwaggerPluginExtension {
    // TODO: Figure out how to allow for embedded closures
    String title
    String version
    String license
    String locations
    String swaggerDirectory
}

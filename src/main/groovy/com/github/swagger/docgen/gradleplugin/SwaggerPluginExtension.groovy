package com.github.swagger.docgen.gradleplugin

import groovy.transform.ToString

/**
 * SwaggerPluginExtension 
 */
@ToString(includeNames = true)
class SwaggerPluginExtension {
    String apiVersion = 'v1'
    String mustacheFileRoot
    String outputTemplate
    String outputPath
    String swaggerDirectory
    String[] endPoints
    String basePath
    String swaggerUIDocBasePath;
    boolean useOutputFlatStructure = true;
}

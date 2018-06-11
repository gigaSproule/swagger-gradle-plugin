package com.benjaminsproule.swagger.gradleplugin.model

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class ApiSourceExtension {
    Project project
    InfoExtension info
    SecurityDefinitionExtension securityDefinition
    List<String> locations
    List<String> schemes // Values MUST be from the list: "http", "https", "ws", "wss"
    List<String> outputFormats
    String basePath
    String host
    String templatePath
    String outputPath
    String swaggerDirectory
    String swaggerFileName = "swagger"
    String modelSubstitute
    String swaggerInternalFilter
    String swaggerApiReader
    boolean springmvc
    boolean useJAXBAnnotationProcessor = false
    boolean useJAXBAnnotationProcessorAsPrimary = true
    boolean jsonExampleValues = false
    boolean attachSwaggerArtifact
    File descriptionFile
    List<String> swaggerExtensions
    List<String> typesToSkipList = []
    List<String> apiModelPropertyAccessExclusionsList = []
    List<String> modelConverters

    ApiSourceExtension(Project project) {
        this.project = project
    }

    /**
     * Used for taking in configuration for info object.
     * @param closure {@link InfoExtension} closure
     */
    void info(Closure closure) {
        info = project.configure(new InfoExtension(project), closure) as InfoExtension
    }

    /**
     * Used for taking in configuration for security defintion object.
     * @param closure {@link SecurityDefinitionExtension} closure
     */
    void securityDefinition(Closure closure) {
        securityDefinition = project.configure(new SecurityDefinitionExtension(), closure) as SecurityDefinitionExtension
    }
}

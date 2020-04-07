package com.benjaminsproule.swagger.gradleplugin.model

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class ApiSourceExtension {
    InfoExtension info
    List<Map<String, List<String>>> security  // see https://swagger.io/docs/specification/2-0/authentication/
    List<SecurityDefinitionExtension> securityDefinition = []
    List<TagExtension> tags = []
    List<String> locations
    List<String> schemes // Values MUST be from the list: "http", "https", "ws", "wss"
    List<String> outputFormats = ['json']
    String basePath
    String host
    String templatePath
    String outputPath
    String swaggerDirectory
    String swaggerFileName = "swagger"
    Map<String, String> modelSubstitutions = [:]
    String swaggerInternalFilter
    String swaggerApiReader
    boolean springmvc
    boolean useJAXBAnnotationProcessor = false
    boolean useJAXBAnnotationProcessorAsPrimary = true
    boolean jsonExampleValues = false
    boolean attachSwaggerArtifact
    boolean expandSuperTypes = true
    String tagStrategy   // null | "class"
    File descriptionFile
    List<String> swaggerExtensions
    List<String> typesToSkipList = []
    List<String> apiModelPropertyAccessExclusionsList = []
    List<String> modelConverters

    private Project project

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
     * Used for taking in configuration for security definition object.
     * @param closure {@link SecurityDefinitionExtension} closure
     */
    void securityDefinition(Closure closure) {
        securityDefinition += project.configure(new SecurityDefinitionExtension(project), closure) as SecurityDefinitionExtension
    }

    /**
     * Used for taking in configuration for tag object.
     * @param closure {@link TagExtension} closure
     */
    void tag(Closure closure) {
        tags += project.configure(new TagExtension(project), closure) as TagExtension
    }
}

package com.benjaminsproule.swagger.gradleplugin.extension

import com.github.kongchen.swagger.docgen.mavenplugin.ApiSource
import com.github.kongchen.swagger.docgen.mavenplugin.SecurityDefinition
import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class ApiSourceExtension extends ApiSource {
    private Project project
    List<String> apiModelPropertyAccessExclusionsList
    List<String> typesToSkipList

    ApiSourceExtension(Project project) {
        this.project = project

        if (this.apiModelPropertyAccessExclusionsList != null) {
            this.apiModelPropertyAccessExclusions.addAll(this.apiModelPropertyAccessExclusionsList)
        }

        if (this.typesToSkipList != null) {
            this.typesToSkip.addAll(this.typesToSkipList)
        }
    }

    void info(Closure closure) {
        this.info = project.configure(new InfoExtension(project), closure) as InfoExtension
    }

    void securityDefinition(Closure closure) {
        if (this.securityDefinitions == null) {
            this.securityDefinitions = new ArrayList<>()
        }

        SecurityDefinition securityDefinitionExtension = project.configure(new SecurityDefinition(), closure) as SecurityDefinition
        this.securityDefinitions.add(securityDefinitionExtension)
    }
}

package com.benjaminsproule.swagger.gradleplugin.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class SecurityDefinitionExtension {
    List<ScopeExtension> scopes = []
    @JsonIgnore
    String name
    String type
    @JsonProperty("in")
    String keyLocation
    @JsonProperty("name")
    String keyName
    String description
    String authorizationUrl
    String tokenUrl
    String flow
    String json

    private Project project

    SecurityDefinitionExtension(Project project) {
        this.project = project
    }

    /**
     * Used for taking in configuration for scope object.
     * @param closure {@link ScopeExtension} closure
     */
    void scope(Closure closure) {
        scopes += project.configure(new ScopeExtension(), closure) as ScopeExtension
    }
    
    List<ScopeExtension> getScopeExtensions() {
      return scopes;
    }
    
    Map<String,String> getScopes() {
      return scopes.collectEntries { [ (it.name) : it.description ] }
    }
}

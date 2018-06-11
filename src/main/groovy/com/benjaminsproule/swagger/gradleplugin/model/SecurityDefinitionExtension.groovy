package com.benjaminsproule.swagger.gradleplugin.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString(includeNames = true)
class SecurityDefinitionExtension {
    @JsonIgnore
    String name
    String type
    @JsonProperty("in")
    String keyLocation
    @JsonProperty("name")
    String keyName
    String description
    String json
    String jsonPath
}

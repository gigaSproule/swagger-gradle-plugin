package com.benjaminsproule.swagger.gradleplugin.model

import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.ToString
import io.swagger.models.auth.ApiKeyAuthDefinition
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.auth.OAuth2Definition
import io.swagger.models.auth.SecuritySchemeDefinition

@ToString(includeNames = true)
class SecurityDefinitionExtension implements ModelValidator, Swagerable<Map<String, SecuritySchemeDefinition>> {
    @JsonIgnore
    String name
    String type
    @JsonProperty("in")
    String keyLocation
    String description
    String json
    String jsonPath

    @Override
    List<String> isValid() {
        if ((name && !type) || (!name && type)) {
            return ['You must specify name and type']
        }

        if (!name && !type && !json && !jsonPath) {
            return ['Security definition must specify json or jsonPath or (name and type)']
        }

        return []
    }

    @Override
    Map<String, SecuritySchemeDefinition> asSwaggerType() {
        return generateSecuritySchemeDefinitions()
    }

    private Map<String, SecuritySchemeDefinition> generateSecuritySchemeDefinitions() throws GenerateException {
        //Tree map to ensure consistent output
        def map = new TreeMap<String, SecuritySchemeDefinition>()

        def securityDefinitions = new HashMap<String, JsonNode>()
        if (json || jsonPath) {
            securityDefinitions = loadSecurityDefinitionsFromJsonFile()
        } else {
            securityDefinitions.put(this.name, new ObjectMapper().valueToTree(this))
        }

        securityDefinitions.each { key, value ->
            SecuritySchemeDefinition ssd = getSecuritySchemeDefinitionByType(value.get("type").asText(), value)
            if (ssd != null) {
                map.put(key, ssd)
            }
        }

        return map
    }

    private Map<String, JsonNode> loadSecurityDefinitionsFromJsonFile() throws GenerateException {
        def securityDefinitions = new HashMap()

        try {
            InputStream jsonStream = json != null ? this.getClass().getResourceAsStream(json) : new FileInputStream(jsonPath)
            JsonNode tree = new ObjectMapper().readTree(jsonStream)

            tree.fieldNames().each {
                securityDefinitions.put(it, tree.get(it))
            }
        } catch (IOException e) {
            throw new GenerateException(e)
        }

        return securityDefinitions
    }

    private static SecuritySchemeDefinition getSecuritySchemeDefinitionByType(String type, JsonNode node) throws GenerateException {
        try {
            ObjectMapper mapper = new ObjectMapper()
            SecuritySchemeDefinition securityDef = null

            if (type == new OAuth2Definition().getType()) {
                if (node != null) {
                    securityDef = mapper.readValue(node.traverse(), OAuth2Definition.class)
                } else {
                    securityDef = new OAuth2Definition()
                }
            } else if (type == new BasicAuthDefinition().getType()) {
                if (node != null) {
                    securityDef = mapper.readValue(node.traverse(), BasicAuthDefinition.class)
                } else {
                    securityDef = new BasicAuthDefinition()
                }
            } else if (type == new ApiKeyAuthDefinition().getType()) {
                if (node != null) {
                    securityDef = mapper.readValue(node.traverse(), ApiKeyAuthDefinition.class)
                } else {
                    securityDef = new ApiKeyAuthDefinition()
                }
            }
            return securityDef
        } catch (IOException e) {
            throw new GenerateException(e)
        }
    }
}

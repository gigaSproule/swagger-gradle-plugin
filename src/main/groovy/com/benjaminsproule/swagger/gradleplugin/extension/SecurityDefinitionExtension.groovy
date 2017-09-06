package com.benjaminsproule.swagger.gradleplugin.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kongchen.swagger.docgen.GenerateException
import com.github.kongchen.swagger.docgen.mavenplugin.SecurityDefinition
import io.swagger.models.auth.SecuritySchemeDefinition
import org.gradle.api.Project

class SecurityDefinitionExtension extends SecurityDefinition {

    private Project project

    SecurityDefinitionExtension(Project project) {
        this.project = project
    }

    @Override
    Map<String, SecuritySchemeDefinition> getDefinitions() throws GenerateException {
        Map<String, SecuritySchemeDefinition> map = new HashMap<String, SecuritySchemeDefinition>()
        if (name != null && type != null) {
            map.put(name, super.getSecuritySchemeDefinitionByNameAndType())
        } else if (json != null) {
            try {
                if (!json.startsWith('/') && !json.startsWith('\\')) {
                    json = "${File.separator}${json}"
                }
                if (File.separator.equals("/") && json.contains('/')) {
                    json = json.replaceAll('/', File.separator)
                } else if (json.contains('\\')) {
                    json = json.replaceAll('\\\\', File.separator)
                }
                JsonNode tree = new ObjectMapper().readTree(project.file("${project.sourceSets.main.output.resourcesDir}${json}"))
                Iterator<String> fit = tree.fieldNames()
                while (fit.hasNext()) {
                    String field = fit.next()
                    JsonNode node = tree.get(field)
                    String type = node.get("type").asText()
                    SecuritySchemeDefinition ssd = super.getSecuritySchemeDefinitionByType(type, node)
                    if (ssd != null) {
                        map.put(field, ssd)
                    }
                }
            } catch (IOException e) {
                throw new GenerateException(e)
            }
        }
        return map
    }
}

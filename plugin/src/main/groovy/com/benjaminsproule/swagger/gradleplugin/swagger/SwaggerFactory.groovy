package com.benjaminsproule.swagger.gradleplugin.swagger

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import io.swagger.models.*
import io.swagger.models.auth.ApiKeyAuthDefinition
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.auth.OAuth2Definition
import io.swagger.models.auth.SecuritySchemeDefinition

import java.util.List
import java.util.Map

import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException

class SwaggerFactory {

    private ClassFinder classFinder

    SwaggerFactory(ClassFinder classFinder) {
        this.classFinder = classFinder
    }

    Swagger swagger(ApiSourceExtension apiSourceExtension) {
        def swagger = new Swagger()
        swagger.setHost(apiSourceExtension.host)
        swagger.setBasePath(apiSourceExtension.basePath)
        swagger.setInfo(generateInfo(apiSourceExtension.info))
        swagger.setTags(generateTags(apiSourceExtension.tags))

        if (apiSourceExtension.schemes) {
            for (String scheme : apiSourceExtension.schemes) {
                swagger.scheme(Scheme.forValue(scheme))
            }
        }

        // read description from file
        if (apiSourceExtension.descriptionFile) {
            try {
                swagger.getInfo().setDescription(apiSourceExtension.descriptionFile.getText().trim())
            } catch (IOException e) {
                throw new GradleException(e.getMessage(), e)
            }
        }

        if (apiSourceExtension.securityDefinition) {
            swagger.setSecurityDefinitions(generateSecuritySchemeDefinitions(apiSourceExtension.securityDefinition))
        }

        if (apiSourceExtension.security) {
            swagger.setSecurity(generateSecurity(apiSourceExtension.security, swagger.getSecurityDefinitions().keySet()))
        }

        return swagger
    }

    private static Info generateInfo(InfoExtension infoExtension) {
        Info info = new Info()
        info.setDescription(infoExtension.description)
        info.setTermsOfService(infoExtension.termsOfService)
        info.setVersion(infoExtension.version)
        info.setTitle(infoExtension.title)

        if (infoExtension.contact != null) {
            info.setContact(generateSwaggerContact(infoExtension.contact))
        }

        if (infoExtension.license != null) {
            info.setLicense(generateSwaggerLicence(infoExtension.license))
        }

        infoExtension.vendorExtensions.each{ key, value ->
            info.setVendorExtension(key, value)
        }

        return info
    }

    private static List<Tag> generateTags(List<TagExtension> tagExtensions) {
        def tags = []
        tagExtensions.each {
            def tag = new Tag()
            tag.name = it.name
            tag.description = it.description
            tag.externalDocs = generateExternalDocs(it.externalDocs)
            tags += tag
        }
        tags
    }

    private static ExternalDocs generateExternalDocs(ExternalDocsExtension externalDocsExtension) {
        if (!externalDocsExtension) {
            return null
        }
        def externalDocs = new ExternalDocs()
        externalDocs.description = externalDocsExtension.description
        externalDocs.url = externalDocsExtension.url
        externalDocs
    }

    private static Contact generateSwaggerContact(ContactExtension contactExtension) {
        Contact contact = new Contact()
        contact.setName(contactExtension.name)
        contact.setUrl(contactExtension.url)
        contact.setEmail(contactExtension.email)

        return contact
    }

    private static License generateSwaggerLicence(LicenseExtension licenseExtension) {
        License license = new License()
        license.setName(licenseExtension.name)
        license.setUrl(licenseExtension.url)

        return license
    }

    private Map<String, SecuritySchemeDefinition> generateSecuritySchemeDefinitions(List<SecurityDefinitionExtension> securityDefinitionExtensions) throws GenerateException {
        //Tree map to ensure consistent output
        def map = new TreeMap<String, SecuritySchemeDefinition>()

        def securityDefinitions = new HashMap<String, JsonNode>()
        securityDefinitionExtensions.each { securityDefinitionExtension ->
            if (securityDefinitionExtension.json) {
                loadSecurityDefinitionsFromJsonFile(securityDefinitionExtension).each {
                    securityDefinitions.put(it.key, it.value)
                }
            } else {
                securityDefinitions.put(securityDefinitionExtension.name, new ObjectMapper().valueToTree(securityDefinitionExtension))
            }
        }

        securityDefinitions.each { key, value ->
            SecuritySchemeDefinition ssd = getSecuritySchemeDefinitionByType(value.get("type").asText(), value)
            if (ssd != null) {
                map.put(key, ssd)
            }
        }

        return map
    }

    private Map<String, JsonNode> loadSecurityDefinitionsFromJsonFile(SecurityDefinitionExtension securityDefinitionExtension) throws GenerateException {
        def securityDefinitions = [:]

        try {
            InputStream jsonStream = classFinder.getClassLoader().getResourceAsStream(securityDefinitionExtension.json)
            if (jsonStream == null) {
                jsonStream = new FileInputStream(securityDefinitionExtension.json)
            }

            JsonNode tree = new ObjectMapper().readTree(jsonStream)

            tree.fieldNames().each {
                securityDefinitions.put(it, tree.get(it))
            }
        } catch (IOException e) {
            throw new GenerateException(e)
        }

        return securityDefinitions
    }

    private
    static SecuritySchemeDefinition getSecuritySchemeDefinitionByType(String type, JsonNode node) throws GenerateException {
        try {
            ObjectMapper mapper = new ObjectMapper()
            SecuritySchemeDefinition securityDef = null

            if (type == new OAuth2Definition().getType()) {
                if (node != null) {
                    securityDef = mapper.readValue(node.traverse(), OAuth2Definition)
                } else {
                    securityDef = new OAuth2Definition()
                }
            } else if (type == new BasicAuthDefinition().getType()) {
                if (node != null) {
                    securityDef = mapper.readValue(node.traverse(), BasicAuthDefinition)
                } else {
                    securityDef = new BasicAuthDefinition()
                }
            } else if (type == new ApiKeyAuthDefinition().getType()) {
                if (node != null) {
                    securityDef = mapper.readValue(node.traverse(), ApiKeyAuthDefinition)
                } else {
                    securityDef = new ApiKeyAuthDefinition()
                }
            }
            return securityDef
        } catch (IOException e) {
            throw new GenerateException(e)
        }
    }

    private List<SecurityRequirement> generateSecurity(List<Map<String, List<String>>> securityList, Set<String> securityDefinitionNames) {
        def requirements = []
        securityList.each { securityMap ->
            def sr = new SecurityRequirement()
            securityMap.each { key, value ->
                // Valdiation is done here and not in the ApiSourceValidator, otherwise
                // we have to load twice the securityDefinitions
                if(!securityDefinitionNames.contains(key)) {
                  throw new InvalidUserDataException("security '${key}' does not exists in a securityDefinition")
                }
                sr.requirement(key, value)
            }
            requirements.add(sr)
        }

        return requirements
    }

}

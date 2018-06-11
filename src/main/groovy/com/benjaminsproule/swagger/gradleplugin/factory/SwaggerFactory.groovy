package com.benjaminsproule.swagger.gradleplugin.factory

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.SwaggerDefinition
import io.swagger.models.*
import io.swagger.models.auth.ApiKeyAuthDefinition
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.auth.OAuth2Definition
import io.swagger.models.auth.SecuritySchemeDefinition
import org.gradle.api.GradleException
import org.springframework.core.annotation.AnnotationUtils

class SwaggerFactory {

    private ClassFinder classFinder

    SwaggerFactory(ClassFinder classFinder) {
        this.classFinder = classFinder
    }

    Swagger swagger(ApiSourceExtension apiSourceExtension) {
        def swagger = new Swagger()
        swagger.setHost(apiSourceExtension.host ?: getHostFromAnnotation(apiSourceExtension))
        swagger.setBasePath(apiSourceExtension.basePath ?: getBasePathFromAnnotation(apiSourceExtension))
        swagger.setInfo(generateInfo(apiSourceExtension.info))
        swagger.setTags(getTagsFromAnnotation(apiSourceExtension))

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

        return swagger
    }

    private String getHostFromAnnotation(ApiSourceExtension apiSourceExtension) {
        for (Class<?> aClass : classFinder.getValidClasses(SwaggerDefinition, apiSourceExtension.locations)) {
            SwaggerDefinition swaggerDefinition = AnnotationUtils.findAnnotation(aClass, SwaggerDefinition)
            return swaggerDefinition.host()
        }

        return null
    }

    private String getBasePathFromAnnotation(ApiSourceExtension apiSourceExtension) {
        for (Class<?> aClass : classFinder.getValidClasses(SwaggerDefinition, apiSourceExtension.locations)) {
            SwaggerDefinition swaggerDefinition = AnnotationUtils.findAnnotation(aClass, SwaggerDefinition)
            return swaggerDefinition.basePath()
        }

        return null
    }

    private List<Tag> getTagsFromAnnotation(ApiSourceExtension apiSourceExtension) {
        def tags = []
        for (Class<?> aClass : classFinder.getValidClasses(SwaggerDefinition, apiSourceExtension.locations)) {
            SwaggerDefinition swaggerDefinition = AnnotationUtils.findAnnotation(aClass, SwaggerDefinition)
            def tagAnnotations = swaggerDefinition.tags()
            tags.addAll(tagAnnotations.collect {
                Tag tag = new Tag()
                    .name(it.name())
                    .description(it.description())
                tag.externalDocs(new ExternalDocs()
                    .description(it.externalDocs().value())
                    .url(it.externalDocs().url()))
            })
        }
        if (!tags) {
            return null
        }
        return tags as List<Tag>
    }

    private static Info generateInfo(InfoExtension infoExtension) {
        Info info = new Info()
        info.setDescription(infoExtension.description)
        info.setTermsOfService(infoExtension.termsOfService)
        info.setVersion(infoExtension.version)
        info.setTitle(infoExtension.title)

        if (infoExtension.contact != null) {
            info.setContact(getSwaggerContact(infoExtension.contact))
        }

        if (infoExtension.license != null) {
            info.setLicense(getSwaggerLicence(infoExtension.license))
        }

        return info
    }

    private static Contact getSwaggerContact(ContactExtension contactExtension) {
        Contact contact = new Contact()
        contact.setName(contactExtension.name)
        contact.setUrl(contactExtension.url)
        contact.setEmail(contactExtension.email)

        return contact
    }

    private static License getSwaggerLicence(LicenseExtension licenseExtension) {
        License license = new License()
        license.setName(licenseExtension.name)
        license.setUrl(licenseExtension.url)

        return license
    }

    private Map<String, SecuritySchemeDefinition> generateSecuritySchemeDefinitions(SecurityDefinitionExtension securityDefinitionExtension) throws GenerateException {
        //Tree map to ensure consistent output
        def map = new TreeMap<String, SecuritySchemeDefinition>()

        def securityDefinitions = new HashMap<String, JsonNode>()
        if (securityDefinitionExtension.json || securityDefinitionExtension.jsonPath) {
            securityDefinitions = loadSecurityDefinitionsFromJsonFile(securityDefinitionExtension)
        } else {
            securityDefinitions.put(securityDefinitionExtension.name, new ObjectMapper().valueToTree(securityDefinitionExtension))
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
            InputStream jsonStream = securityDefinitionExtension.json != null ?
                classFinder.getClassLoader().getResourceAsStream(securityDefinitionExtension.json)
                : new FileInputStream(securityDefinitionExtension.jsonPath)

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
}

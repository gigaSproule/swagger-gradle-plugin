package com.benjaminsproule.swagger.gradleplugin.swagger

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.model.*
import io.swagger.models.Scheme
import io.swagger.models.auth.In
import org.reflections.Reflections
import spock.lang.Specification

import java.lang.annotation.Annotation

class SwaggerFactoryTest extends Specification {
    private SwaggerFactory swaggerFactory
    private List<String> locations = ['com.benjaminsproule']

    def setup() {
        def mockClassFinder = Mock(ClassFinder)
        mockClassFinder.getClassLoader() >> getClass().getClassLoader()
        mockClassFinder.getValidClasses(_, _) >> { args ->
            new Reflections(getClass().getClassLoader(), args[1]).getTypesAnnotatedWith(args[0] as Class<? extends Annotation>)
        }

        swaggerFactory = new SwaggerFactory(mockClassFinder)
    }

    def 'Should generate swagger object from contents'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.host = "http://localhost"
        apiSourceExtension.basePath = '/'
        apiSourceExtension.locations = locations
        apiSourceExtension.schemes = ['http']

        def infoExtension = new InfoExtension()
        infoExtension.description = 'description'
        infoExtension.termsOfService = 'terms of service'
        infoExtension.title = 'title'
        infoExtension.version = '1.0'
        infoExtension.vendorExtensions = Map.of("x-tags", "API")

        def contactExtension = new ContactExtension()
        contactExtension.name = 'contact name'
        contactExtension.url = 'contact url'
        contactExtension.email = 'contact email'
        infoExtension.contact = contactExtension

        def licenseExtension = new LicenseExtension()
        licenseExtension.name = 'license name'
        licenseExtension.url = 'license url'
        infoExtension.license = licenseExtension

        apiSourceExtension.info = infoExtension

        def tagExtension1 = new TagExtension()
        tagExtension1.name = 'tag 1'
        tagExtension1.description = 'description'

        def externalDocsExtension = new ExternalDocsExtension()
        externalDocsExtension.description = 'description'
        externalDocsExtension.url = 'url'

        tagExtension1.externalDocs = externalDocsExtension

        def tagExtension2 = new TagExtension()
        tagExtension2.name = 'tag 2'
        tagExtension2.description = 'description'

        apiSourceExtension.tags = [tagExtension1, tagExtension2]

        def securityDefinitionExtension1 = new SecurityDefinitionExtension()
        securityDefinitionExtension1.type = 'basic'
        securityDefinitionExtension1.name = 'security definition 1'

        def securityDefinitionExtension2 = new SecurityDefinitionExtension()
        securityDefinitionExtension2.type = 'basic'
        securityDefinitionExtension2.name = 'security definition 2'

        apiSourceExtension.securityDefinition = [securityDefinitionExtension1, securityDefinitionExtension2]

        when:
        def swagger = swaggerFactory.swagger(apiSourceExtension)

        then:
        swagger
        swagger.host == "http://localhost"
        swagger.basePath == '/'
        swagger.schemes == [Scheme.HTTP]
        swagger.info
        swagger.info.description == 'description'
        swagger.info.termsOfService == 'terms of service'
        swagger.info.title == 'title'
        swagger.info.version == '1.0'
        swagger.info.contact
        swagger.info.contact.name == 'contact name'
        swagger.info.contact.url == 'contact url'
        swagger.info.contact.email == 'contact email'
        swagger.info.license
        swagger.info.license.name == 'license name'
        swagger.info.license.url == 'license url'
        swagger.info.vendorExtensions.get("x-tags") == "API"
        swagger.tags
        swagger.tags[0].name == 'tag 1'
        swagger.tags[0].description == 'description'
        swagger.tags[0].externalDocs.description == 'description'
        swagger.tags[0].externalDocs.url == 'url'
        swagger.tags[1].name == 'tag 2'
        swagger.tags[1].description == 'description'
        swagger.securityDefinitions
        swagger.securityDefinitions.'security definition 1'.type == 'basic'
        swagger.securityDefinitions.'security definition 2'.type == 'basic'
    }

    def 'Should generate security definitions from json with classpath path'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = locations

        def infoExtension = new InfoExtension()
        infoExtension.title = 'title'
        apiSourceExtension.info = infoExtension

        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.json = 'security-definition/securityDefinitionExtensionTest.json'
        apiSourceExtension.securityDefinition = [securityDefinitionExtension]

        when:
        def swagger = swaggerFactory.swagger(apiSourceExtension)

        then:
        swagger.securityDefinitions.basic.type == 'basic'
        swagger.securityDefinitions.api_key.type == 'apiKey'
        swagger.securityDefinitions.api_key.name == 'X-API-Key'
        swagger.securityDefinitions.api_key.in == In.HEADER
        swagger.securityDefinitions.petstore_auth.type == 'oauth2'
        swagger.securityDefinitions.petstore_auth.flow == 'implicit'
        swagger.securityDefinitions.petstore_auth.authorizationUrl == 'http://swagger.io/api/oauth/dialog'
        swagger.securityDefinitions.petstore_auth.scopes
        swagger.securityDefinitions.petstore_auth.scopes['write:pets'] == 'modify pets in your account'
        swagger.securityDefinitions.petstore_auth.scopes['read:pets'] == 'read your pets'
    }

    def 'Should generate security definitions from json with full path'() {
        given:
        def apiSourceExtension = new ApiSourceExtension()
        apiSourceExtension.locations = locations

        def infoExtension = new InfoExtension()
        infoExtension.title = 'title'
        apiSourceExtension.info = infoExtension

        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.json = new File(getClass().getClassLoader().getResource('security-definition/securityDefinitionExtensionTest.json').toURI()).absolutePath
        apiSourceExtension.securityDefinition = [securityDefinitionExtension]

        when:
        def swagger = swaggerFactory.swagger(apiSourceExtension)

        then:
        swagger.securityDefinitions.basic.type == 'basic'
        swagger.securityDefinitions.api_key.type == 'apiKey'
        swagger.securityDefinitions.api_key.name == 'X-API-Key'
        swagger.securityDefinitions.api_key.in == In.HEADER
        swagger.securityDefinitions.petstore_auth.type == 'oauth2'
        swagger.securityDefinitions.petstore_auth.flow == 'implicit'
        swagger.securityDefinitions.petstore_auth.authorizationUrl == 'http://swagger.io/api/oauth/dialog'
        swagger.securityDefinitions.petstore_auth.scopes
        swagger.securityDefinitions.petstore_auth.scopes['write:pets'] == 'modify pets in your account'
        swagger.securityDefinitions.petstore_auth.scopes['read:pets'] == 'read your pets'
    }
}

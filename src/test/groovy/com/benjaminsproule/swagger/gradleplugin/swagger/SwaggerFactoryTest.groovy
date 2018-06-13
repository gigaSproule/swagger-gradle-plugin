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

        def securityDefinitionExtension = new SecurityDefinitionExtension()
        securityDefinitionExtension.type = 'basic'
        securityDefinitionExtension.name = 'name'
        apiSourceExtension.securityDefinition = securityDefinitionExtension

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
        swagger.securityDefinitions
        swagger.securityDefinitions.name.type == 'basic'
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
        apiSourceExtension.securityDefinition = securityDefinitionExtension

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
        apiSourceExtension.securityDefinition = securityDefinitionExtension

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

package com.benjaminsproule.swagger.gradleplugin.model

import io.swagger.models.Info
import io.swagger.models.Scheme
import io.swagger.models.auth.BasicAuthDefinition
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
import spock.lang.Specification

class ApiSourceExtensionTest extends Specification {
    ApiSourceExtension apiSourceExtension

    def setup() {
        def project = ProjectBuilder.builder().build()
        apiSourceExtension = new ApiSourceExtension(project)
    }

    def 'Valid api source validation returns no errors'() {
        setup:
        apiSourceExtension.locations = ['com.github']
        apiSourceExtension.info = Mock(InfoExtension)
        apiSourceExtension.info.isValid() >> []

        when:
        def result = apiSourceExtension.isValid()

        then:
        assert !result
    }

    @Ignore("Need to sort out the classfinder before this is going to work")
    def 'Api Source with missing info should provide missing info error'() {
        setup:
        apiSourceExtension.locations = ['com.github.junk'] //make sure we don't discover any annotations

        when:
        def result = apiSourceExtension.isValid()

        then:
        assert result
        assert result.contains('Info is required by the swagger spec.')
    }

    def 'Api Source with no locations should provide missing locations error'() {
        setup:
        apiSourceExtension.info = new InfoExtension(null)

        when:
        def result = apiSourceExtension.isValid()

        then:
        assert result
        assert result.contains('locations required, specify classes or packages where swagger annotated classes are located')
    }

    def 'Errors from nested objects should be returned'() {
        setup:
        apiSourceExtension.locations = ['com.github']
        apiSourceExtension.info = Mock(InfoExtension)
        apiSourceExtension.info.isValid() >> ['nested error']

        when:
        def result = apiSourceExtension.isValid()

        then:
        assert result
        assert result.contains('nested error')
    }

    def 'Should generate swagger type from contents'() {
        setup:
        apiSourceExtension.host = "http://localhost"
        apiSourceExtension.basePath ='/'
        apiSourceExtension.locations = ['com.benjaminsproule']
        apiSourceExtension.schemes = ['http']
        apiSourceExtension.descriptionFile = new File("src/test/resources/api-description/description.txt")
        apiSourceExtension.info = Mock(InfoExtension)
        apiSourceExtension.info.asSwaggerType() >> new Info()
        apiSourceExtension.securityDefinition = Mock(SecurityDefinitionExtension)
        apiSourceExtension.securityDefinition.asSwaggerType() >> ['basic': new BasicAuthDefinition()]

        when:
        def result = apiSourceExtension.asSwaggerType()

        then:
        assert result
        assert result.host == "http://localhost"
        assert result.basePath == '/'
        assert result.schemes == [Scheme.HTTP]
        assert result.info
        assert result.info.description == '''A file description
with multiple lines'''
        assert result.securityDefinitions
    }
}

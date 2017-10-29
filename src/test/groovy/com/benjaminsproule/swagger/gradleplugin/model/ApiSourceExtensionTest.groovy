package com.benjaminsproule.swagger.gradleplugin.model

import io.swagger.models.Info
import io.swagger.models.Scheme
import io.swagger.models.auth.BasicAuthDefinition
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class ApiSourceExtensionTest {
    ApiSourceExtension apiSourceExtension
    def mock

    @Before
    void setup() {
        def project = ProjectBuilder.builder().build()
        apiSourceExtension = new ApiSourceExtension(project)
    }

    @Test
    void 'Valid api source validation returns no errors'() {
        apiSourceExtension.locations = ['com.github']
        apiSourceExtension.info = [isValid: { new ArrayList<>()} ] as InfoExtension

        def result = apiSourceExtension.isValid()

        println apiSourceExtension

        assert !result
    }

    @Test
    @Ignore("Need to sort out the classfinder before this is going to work")
    void 'Api Source with missing info should provide missing info error'() {
        apiSourceExtension.locations = ['com.github.junk'] //make sure we don't discover any annotations

        def result
        mock.use {
            result = apiSourceExtension.isValid()
        }


        assert result
        assert result.contains('Info is required by the swagger spec.')
    }

    @Test
    void 'Api Source with no locations should provide missing locations error'() {
        apiSourceExtension.info = new InfoExtension(null)
        def result = apiSourceExtension.isValid()

        assert result
        assert result.contains('locations required, specify classes or packages where swagger annotated classes are located')
    }

    @Test
    void 'Errors from nested objects should be returned'() {
        apiSourceExtension.locations = ['com.github']
        apiSourceExtension.info = [isValid: {['nested error']}] as InfoExtension

        def result = apiSourceExtension.isValid()

        assert result
        assert result.contains('nested error')
    }

    @Test
    void 'Should generate swagger type from contents'() {
        apiSourceExtension.host = "http://localhost"
        apiSourceExtension.basePath ='/'
        apiSourceExtension.locations = ['com.benjaminsproule']
        apiSourceExtension.schemes = ['http']
        apiSourceExtension.descriptionFile = new File("src/test/resources/api-description/description.txt")
        apiSourceExtension.info = [asSwaggerType: {new Info()}] as InfoExtension
        apiSourceExtension.securityDefinition = [asSwaggerType: {['basic': new BasicAuthDefinition()]}] as SecurityDefinitionExtension

        def result = apiSourceExtension.asSwaggerType()

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

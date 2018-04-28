package com.benjaminsproule.swagger.gradleplugin.model

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import io.swagger.models.Info
import io.swagger.models.Scheme
import io.swagger.models.auth.BasicAuthDefinition
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ApiSourceExtensionTest extends Specification {
    Project project
    ApiSourceExtension apiSourceExtension

    def setup() {
        project = ProjectBuilder.builder().build()
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

    def 'Api Source with missing info should provide missing info error'() {
        setup:
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin
        ClassFinder.createInstance(project)
        apiSourceExtension.locations = ['com.github.junk'] //make sure we don't discover any annotations

        when:
        def result = apiSourceExtension.isValid()

        then:
        assert result
        assert result.contains('info.title is required by the swagger spec')
        assert result.contains('info.version is required by the swagger spec')
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
        apiSourceExtension.basePath = '/'
        apiSourceExtension.locations = ['com.benjaminsproule']
        apiSourceExtension.schemes = ['http']
        apiSourceExtension.descriptionFile = loadFileFromClasspath("/api-description/description.txt")
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

    private static File loadFileFromClasspath(String path) {
        URL url = ApiSourceExtensionTest.class.getResource(path)
        return new File(url.toURI())
    }

}

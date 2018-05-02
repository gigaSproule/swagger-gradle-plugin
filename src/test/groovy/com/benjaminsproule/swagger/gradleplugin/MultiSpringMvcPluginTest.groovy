package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class MultiSpringMvcPluginTest {
    Project project

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.benjaminsproule.swagger'
    }

    @Test
    void producesSwaggerDocumentation() {
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin

        def expectedSwaggerDirectory = temporaryFolder.newFolder()
        def expectedSwaggerFile1 = "swagger-one"
        def expectedSwaggerFile2 = "swagger-two"
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc.TestResourceForMultiApiSource_One']
                    springmvc = true
                    schemes = ['http']
                    swaggerDirectory = expectedSwaggerDirectory
                    swaggerFileName = expectedSwaggerFile1
                    basePath = 'One'
                }
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc.TestResourceForMultiApiSource_Two']
                    springmvc = true
                    schemes = ['http']
                    swaggerDirectory = expectedSwaggerDirectory
                    swaggerFileName = expectedSwaggerFile2
                    basePath = 'Two'
                }
            }
        ))

        project.tasks.generateSwaggerDocumentation.execute()

        def swaggerFile1 = new File(expectedSwaggerDirectory, "${expectedSwaggerFile1}.json").text
        assertSwaggerJson(swaggerFile1, 'One')
        def swaggerFile2 = new File(expectedSwaggerDirectory, "${expectedSwaggerFile2}.json").text
        assertSwaggerJson(swaggerFile2, 'Two')
    }

    private static void assertSwaggerJson(String swaggerJsonFile, String prefix) {

        def producedSwaggerDocument = new JsonSlurper().parseText(swaggerJsonFile)

        assert producedSwaggerDocument.swagger == '2.0'
        assert producedSwaggerDocument.basePath == "${prefix}"

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == prefix
        assert info.title == "${prefix}ApiTitle"

        def paths = producedSwaggerDocument.get('paths')
        assert paths
        assert paths.size() == 1

        assert paths."${prefix}Api".get.responses.'200'.schema.'$ref' == "#/definitions/MultiApiSourceParent${prefix}ResponseModel"

    }
}

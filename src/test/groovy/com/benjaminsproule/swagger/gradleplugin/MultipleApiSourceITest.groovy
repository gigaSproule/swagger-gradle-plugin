package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class MultipleApiSourceITest extends AbstractPluginITest {

    def 'Produces Swagger Documentation'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/swaggerui-" + UUID.randomUUID()
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    schemes = ['http']
                    basePath = 'One'
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    swaggerFileName = 'swagger-one'
                    ${testSpecificConfigOne}
                }
                apiSource {
                    schemes = ['http']
                    basePath = 'Two'
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    swaggerFileName = 'swagger-two'
                    ${testSpecificConfigTwo}
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger-one.json", 'One')
        assertSwaggerJson("${expectedSwaggerDirectory}/swagger-two.json", 'Two')

        where:
        testSpecificConfigOne << [
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.springmvc.TestResourceForMultiApiSource_One']
                springmvc = true
            """,
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.jaxrs.TestResourceForMultiApiSource_One']
            """
        ]
        testSpecificConfigTwo << [
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.springmvc.TestResourceForMultiApiSource_Two']
                springmvc = true
            """,
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.jaxrs.TestResourceForMultiApiSource_Two']
            """
        ]
    }

    private static void assertSwaggerJson(String swaggerJsonFile, String prefix) {
        def producedSwaggerDocument = new JsonSlurper().parse(new File(swaggerJsonFile), 'UTF-8')

        assert producedSwaggerDocument.swagger == '2.0'
        assert producedSwaggerDocument.basePath == "${prefix}"

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == prefix
        assert info.title == "${prefix}ApiTitle"

        def paths = producedSwaggerDocument.get('paths')
        assert paths
        assert paths.size() == 1

        assert paths."/${prefix}Api".get.responses.'200'.schema.'$ref' == "#/definitions/MultiApiSourceParent${prefix}ResponseModel"
    }
}

package com.benjaminsproule.swagger.gradleplugin


import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class MissingImplicitParamsITest extends AbstractPluginITest {

    def 'Produces Swagger Documentation'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/swaggerui-" + UUID.randomUUID()
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.github.wakingrufus.swagger'
            }
            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.missingimplicitparams']
                    schemes = ['http']
                    host = 'localhost:8080'
                    basePath = '/'
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    ${testSpecificConfig}
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        result.output.contains("java.lang.ClassNotFoundException: com.benjaminsproule.swagger.gradleplugin.test.model.MissingRequestModel")

        where:
        testSpecificConfig << [
            """
                springmvc = true
            """,
            """
            """
        ]
    }
}

package com.benjaminsproule.swagger.gradleplugin

import static org.gradle.testkit.runner.TaskOutcome.FAILED

class MissingImplicitParamsITest extends AbstractPluginITest {

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
        def result = runPluginTask(false)

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
        result.output.contains("Caused by: java.lang.ClassNotFoundException: com.benjaminsproule.swagger.gradleplugin.test.model.MissingRequestModel")

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

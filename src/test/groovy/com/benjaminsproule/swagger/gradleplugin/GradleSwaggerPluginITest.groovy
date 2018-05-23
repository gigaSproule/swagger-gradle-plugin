package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper

import java.nio.file.Files

import static org.gradle.testkit.runner.TaskOutcome.SKIPPED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GradleSwaggerPluginITest extends AbstractPluginITest {

    def 'Should skip swagger generation when swagger.skip property set'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    locations = ['com.benjaminsproule']
                    info {
                        title = project.name
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def result = pluginTaskRunnerBuilder()
            .withArguments('generateSwaggerDocumentation', '-Pswagger.skip=true')
            .build()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SKIPPED

        assert !new File(testProjectOutputDir, '/swagger.json').exists()
    }

    def 'Should read missing config from annotations'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDir}/swaggerui-" + UUID.randomUUID()
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.Definitions']
                    schemes = ['http']
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
            """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        def swaggerFile = new File("${expectedSwaggerDirectory}/swagger.json")
        assert Files.exists(swaggerFile.toPath())

        def producedSwaggerDocument = new JsonSlurper().parse(swaggerFile)

        assert producedSwaggerDocument.host == 'http://annotated'
        assert producedSwaggerDocument.basePath == '/annotated'

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == 'annotated'
        assert info.title == 'annotated'

        def tags = producedSwaggerDocument.tags
        assert tags
        assert tags.size() == 1
        assert tags.get(0).name == 'Test'
        assert tags.get(0).description == 'Test tag description'
    }

    def 'Generate Swagger artifact when flag is set'() {
        given:
        def swaggerRelativeDirectory = "swaggerui-" + UUID.randomUUID()
        def expectedSwaggerDirectory = "${testProjectOutputDir}/${swaggerRelativeDirectory}"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule']
                    schemes = ['http']
                    info {
                        title = project.name
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        def swaggerFile = new File("${buildFile.getParentFile()}/build/libs/${buildFile.getParentFile().getName()}-${swaggerRelativeDirectory}.jar")
        assert swaggerFile.exists()
    }
}

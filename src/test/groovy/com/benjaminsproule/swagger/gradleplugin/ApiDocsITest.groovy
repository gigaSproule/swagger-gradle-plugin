package com.benjaminsproule.swagger.gradleplugin

import org.apache.commons.text.RandomStringGenerator

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ApiDocsITest extends AbstractPluginITest {

    def 'Use specified templates to generate Swagger docs'() {
        given:
        def randomizer = new RandomStringGenerator.Builder().build().generate(5)
        def expectedSwaggerDocsDirectory = "${testProjectDir}/swaggerdocs-${randomizer}"
        def expectedSwaggerApiDocsFile = "${expectedSwaggerDocsDirectory}/api.html"
        //Template path is not actually a template path but a template file and it probably has to be the root
        def templatePathValue = "classpath:/api-doc-template/strapdown.html.hbs"

        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    locations = ['com.benjaminsproule']
                    schemes = ['http']
                    info {
                        title = 'Project Name'
                        version = '1'
                        termsOfService = 'http://localhost/tos'
                        description = 'The Api Description'
                        license {
                            name = 'Apache 2.0'
                            url = 'http://localhost/license'
                        }
                        contact {
                            name = 'Joe Blogs'
                            email = 'joe.blogs@fake.com'
                        }
                    }
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                    templatePath = '${templatePathValue}'
                    outputPath = '${expectedSwaggerApiDocsFile}'
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        def swaggerDir = new File(expectedSwaggerApiDocsFile)
        assert swaggerDir
    }
}

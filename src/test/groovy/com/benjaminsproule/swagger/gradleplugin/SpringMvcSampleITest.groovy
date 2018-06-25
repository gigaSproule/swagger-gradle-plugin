package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SpringMvcSampleITest extends AbstractPluginITest {

    def 'Configure global security'() {
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
                    springmvc = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc.SampleController']
                    host = 'localhost:8080'
                    basePath = '/'
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    ${testSpecificConfig}
                }
            }
        """

      when:
      def result = runPluginTask()

      then:
      result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

      assertSwaggerJsonGlobalSecurity("${expectedSwaggerDirectory}/swagger.json")

      where:
      testSpecificConfig << [
          """
             securityDefinition {
                  name = 'MyBasicAuth'
                  type = 'basic'
             }
             security = [ [ MyBasicAuth : [] ] ]
          """
      ]
  }
  
  private static void assertSwaggerJsonGlobalSecurity(String swaggerJsonFile) {
    def producedSwaggerDocument = new JsonSlurper().parse(new File(swaggerJsonFile), 'UTF-8')

    assert producedSwaggerDocument.swagger == '2.0'
    assert producedSwaggerDocument.basePath == '/'

    def info = producedSwaggerDocument.info
    assert info
    assert info.version == '1'
    assert info.title == 'test'

    def security = producedSwaggerDocument.security;
    assert security
    assert security.size() == 1
    assert security[0].containsKey('MyBasicAuth')
 }
}

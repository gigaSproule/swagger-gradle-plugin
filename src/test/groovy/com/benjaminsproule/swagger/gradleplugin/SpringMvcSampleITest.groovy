package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SpringMvcSampleITest extends AbstractPluginITest {

   def 'Produces Swagger Documentation'() {
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

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json")

        where:
        testSpecificConfig << [
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc.SampleController']
            """
        ]
    }

    private static void assertSwaggerJson(String swaggerJsonFile) {
        def producedSwaggerDocument = new JsonSlurper().parse(new File(swaggerJsonFile), 'UTF-8')

        assert producedSwaggerDocument.swagger == '2.0'
        assert producedSwaggerDocument.basePath == '/'

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == '1'
        assert info.title == 'test'

        def paths = producedSwaggerDocument.get('paths')
        assert paths
        assert paths.size() == 1

        assert paths."/api/sample".get
        assert paths."/api/sample".get.operationId == "getSample"
        assert paths."/api/sample".post
        assert paths."/api/sample".post.operationId == "postSample"
    }
    
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
      System.out.println(result.output)

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
    
    System.out.println(new File(swaggerJsonFile).text)

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

package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

import java.util.List

class SecurityITest extends AbstractPluginITest {
  
    static final List<String> springMvcAndJaxRs = [
       """
          springmvc = true
          locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc.SampleController']
       """,
       """
          locations = ['com.benjaminsproule.swagger.gradleplugin.test.jaxrs.SampleResource']
       """
      ]
    
    def 'Configure global security'() {
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
                    host = 'localhost:8080'
                    basePath = '/'
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                    security = [ [ MyBasicAuth : [] ] ]
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
      testSpecificConfig << springMvcAndJaxRs
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
 
 def 'Configure global security with multiple auths'() {
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
                    host = 'localhost:8080'
                    basePath = '/'
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                    securityDefinition {
                        name = 'MyApiKey'
                        type = 'apiKey'
                        keyLocation = 'header'
                        keyName = 'X-API-Key'
                    }
                    /* Add example when  issue https://github.com/gigaSproule/swagger-gradle-plugin/issues/104 is fixed 
                    securityDefinition {
                        name = 'MyOAuth2'
                        type = 'oauth2'
                        authorizationUrl = 'authorizationUrl'
                        flow = 'implicit'
                        scope {
                            name = 'scope1'
                            description = 'description'
                        }
                    }
                    */
                    security = [ [ MyBasicAuth : [], MyApiKey : [] ] ]
                    ${testSpecificConfig}
                }
            }
        """

   when:
   def result = runPluginTask()

   then:
   result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

   assertSwaggerJsonGlobalSecurityMultiple("${expectedSwaggerDirectory}/swagger.json")

   where:
   testSpecificConfig << springMvcAndJaxRs
 }
 
 private static void assertSwaggerJsonGlobalSecurityMultiple(String swaggerJsonFile) {
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
   assert security[0].containsKey('MyApiKey')
 }
 
}

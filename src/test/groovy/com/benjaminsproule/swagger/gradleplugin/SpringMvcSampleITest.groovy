package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SpringMvcSampleITest extends AbstractPluginITest {

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
   def 'Produces separate model for controller methods annotated with JsonView annotation'() {
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

        def producedSwaggerDocument = new JsonSlurper().parse(new File("${expectedSwaggerDirectory}/swagger.json"), 'UTF-8')

        assert producedSwaggerDocument.swagger == '2.0'
        assert producedSwaggerDocument.basePath == '/'

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == '1'
        assert info.title == 'test'

        def paths = producedSwaggerDocument.get('paths')
        assert paths
        assert paths.size() == 3

        def withViewOne = paths."/api/jsonview/with/1".get
        assert withViewOne
        assert withViewOne.operationId == "withJsonViewOne"
        assert withViewOne.responses."200".responseSchema.originalRef == "#/definitions/TestJsonViewEntity_TestJsonViewOne"

        def withViewTwo = paths."/api/jsonview/with/2".get
        assert withViewTwo
        assert withViewTwo.operationId == "withJsonViewTwo"
        assert withViewTwo.responses."200".responseSchema.originalRef == "#/definitions/TestJsonViewEntity_TestJsonViewTwo"

        def withoutAny = paths."/api/jsonview/without".post
        assert withoutAny.operationId == "withoutJsonView"
        assert withoutAny.responses."200".responseSchema.originalRef == "#/definitions/TestJsonViewEntity"

        // assert definitions
        def definitions = producedSwaggerDocument.definitions
        assert definitions
        assert definitions.size() == 3

        def viewOneDef = definitions."TestJsonViewEntity_TestJsonViewOne"
        assert viewOneDef
        assert viewOneDef.required == ['requiredValue', 'viewValue']

        def viewTwoDef = definitions."TestJsonViewEntity_TestJsonViewTwo"
        assert viewTwoDef
        assert viewTwoDef.required == ['requiredValue']


        def regularEntityDef = definitions."TestJsonViewEntity"
        assert regularEntityDef
        assert regularEntityDef.required == ['requiredValue', 'viewValue']


        where:
        testSpecificConfig << [
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc.JsonViewController']
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
    
    def 'Produces tag for controller when no Api'() {
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
                    springmvc = true
                    host = 'localhost:8080'
                    basePath = '/'
                    info {
                        title = 'test'
                        version = '1'
                    }
                    tagStrategy = "class"
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    ${testSpecificConfig}
                }
            }
        """
       when:
      def result = runPluginTask()
       then:
      result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
       assertSwaggerJsonTags("${expectedSwaggerDirectory}/swagger.json")
       where:
      testSpecificConfig << [
          """
              locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc']
          """
      ]
  }
  
  private static void assertSwaggerJsonTags(String swaggerJsonFile) {
    def producedSwaggerDocument = new JsonSlurper().parse(new File(swaggerJsonFile), 'UTF-8')
    
    assert producedSwaggerDocument.swagger == '2.0'
    assert producedSwaggerDocument.basePath == '/'
    
    def info = producedSwaggerDocument.info
    assert info
    assert info.version == '1'
    assert info.title == 'test'
    
    def paths = producedSwaggerDocument.get('paths')
    assert paths
    assert paths.size() == 5

    assert paths."/api/sample".get
    assert paths."/api/sample".get.operationId == "getSample"
    assert paths."/api/sample".get.tags == ['SampleController']
    assert paths."/api/sample".post
    assert paths."/api/sample".post.operationId == "postSample"
    assert paths."/api/sample".post.tags == ['SampleController']
    
    assert paths."/api/other".get
    assert paths."/api/other".get.operationId == "getOther"
    assert paths."/api/other".get.tags == ['OtherController']
    assert paths."/api/other".post
    assert paths."/api/other".post.operationId == "createOther"
    assert paths."/api/other".post.tags == ['OtherController']
    
    assert paths."/api/pets".get
    assert paths."/api/pets".get.operationId == "getPets"
    assert paths."/api/pets".get.tags == ['pets']
    assert paths."/api/pets/eagles".get
    assert paths."/api/pets/eagles".get.operationId == "getEagles"
    assert paths."/api/pets/eagles".get.tags == ['eagles']

    assert paths."/api/type-param".post
    assert paths."/api/type-param".post.operationId == "post"
    assert paths."/api/type-param".post.tags == ['TypeParamController']
    
    def tags = producedSwaggerDocument.get('tags')
    assert tags
    assert tags.size() == 5
    assert tags.sort() == [[name:'eagles'], [name:'OtherController'], [name:'pets'], [name:'SampleController'],
                           [name:'TypeParamController']]
  }

}

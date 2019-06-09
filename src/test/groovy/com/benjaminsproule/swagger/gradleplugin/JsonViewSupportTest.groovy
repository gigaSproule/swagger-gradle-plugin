package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewEntity
import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewOne
import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewTwo
import groovy.json.JsonSlurper

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class JsonViewSupportTest extends AbstractPluginITest {

    def '@JsonView support for JAX-RS resources'() {
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
        assert withoutAny
        assert withoutAny.operationId == "withoutJsonView"
        assert withoutAny.responses."200".responseSchema.originalRef == "#/definitions/TestJsonViewEntity"

        // assert definitions
        def definitions = producedSwaggerDocument.definitions
        assert definitions
        assert definitions.size() == 3

        assertDefinition(definitions, TestJsonViewEntity, TestJsonViewOne, ['requiredValue', 'viewValue'])
        assertDefinition(definitions, TestJsonViewEntity, TestJsonViewTwo, ['requiredValue'])
        assertDefinition(definitions, TestJsonViewEntity, null, ['requiredValue', 'viewValue'])

        where:
        testSpecificConfig << [
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.jaxrs.JsonViewTestResource']
            """
        ]
    }
    def '@JsonView support for Spring MVC controllers'() {
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
        assert withoutAny
        assert withoutAny.operationId == "withoutJsonView"
        assert withoutAny.responses."200".responseSchema.originalRef == "#/definitions/TestJsonViewEntity"

        // assert definitions
        def definitions = producedSwaggerDocument.definitions
        assert definitions
        assert definitions.size() == 3

        assertDefinition(definitions, TestJsonViewEntity, TestJsonViewOne, ['requiredValue', 'viewValue'])
        assertDefinition(definitions, TestJsonViewEntity, TestJsonViewTwo, ['requiredValue'])
        assertDefinition(definitions, TestJsonViewEntity, null, ['requiredValue', 'viewValue'])

        where:
        testSpecificConfig << [
            """
                locations = ['com.benjaminsproule.swagger.gradleplugin.test.springmvc.JsonViewController']
            """
        ]
    }


    private static void assertDefinition(def definitions, Class<?> definitionEntity, Class<?> view, Collection<String> expectedFields) {
        assert definitions

        def defName = definitionEntity.simpleName + view? "_${view.simpleName}" : ""
        def definition = definitions[defName]
        assert definition
        assert definition.required == expectedFields
    }
}

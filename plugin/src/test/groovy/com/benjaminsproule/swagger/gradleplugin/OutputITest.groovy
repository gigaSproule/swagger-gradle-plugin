package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class OutputITest extends AbstractPluginITest {

    public static final List<String> locations = [
        """
            locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
        """,
        """
            locations = ['com.benjaminsproule.swagger.gradleplugin.test.java']
        """,
//        """
//            locations = ['com.benjaminsproule.swagger.gradleplugin.test.kotlin']
//        """,
        """
            locations = ['com.benjaminsproule.swagger.gradleplugin.test.scala']
        """
    ]

    def 'Produces Swagger documentation with JAX-RS'() {
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
                    ${basicApiSourceClosure()}
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
        testSpecificConfig << locations
    }

    void 'Produces Swagger documentation with Spring MVC'() {
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
                    ${basicApiSourceClosure()}
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    springmvc = true
                    ${testSpecificConfig}
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json", 'string', 'query')

        where:
        testSpecificConfig << locations
    }

    def 'Produces Swagger documentation with model substitution'() {
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
                    ${basicApiSourceClosure()}
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    modelSubstitute = 'model-substitution'
                    ${testSpecificConfig}
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json", 'integer')

        where:
        testSpecificConfig << locations
    }

    def 'Produces Swagger documentation with model substitution on an apiSource level'() {
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
                    ${basicApiSourceClosure()}
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    swaggerFileName = 'swagger-model-substitution'
                    modelSubstitute = 'model-substitution'
                    ${testSpecificConfig}
                }
                apiSource {
                    ${basicApiSourceClosure()}
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    swaggerFileName = 'swagger'
                    ${testSpecificConfig}
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger-model-substitution.json", 'integer')
        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json")

        where:
        testSpecificConfig << locations
    }

    def 'Produce Swagger documentation in multiple formats'() {
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
                    ${basicApiSourceClosure()}
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    outputFormats = ['json', 'yaml']
                    ${testSpecificConfig}
                }
            }
        """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json")
        assertSwaggerYaml("${expectedSwaggerDirectory}/swagger.yaml")

        where:
        testSpecificConfig << locations
    }

    private static String basicApiSourceClosure() {
        """
        schemes = ['http']
        info {
            title = 'test'
            version = '1'
            license {
                name = 'Apache 2.0'
            }
            contact {
                name = 'Joe Blogs'
            }
        }
        host = 'localhost:8080'
        basePath = '/'
        securityDefinition {
            name = 'MyBasicAuth'
            type = 'basic'
        }
    """
    }

    private static void assertSwaggerJson(String swaggerJsonFilePath, String type = 'string', String pathParam = 'path') {
        def swaggerJsonFile = new File(swaggerJsonFilePath)
        assert Files.exists(swaggerJsonFile.toPath())
        assertSwaggerDocument(new JsonSlurper().parse(swaggerJsonFile, 'UTF-8'), 'json', type, pathParam)
    }

    private static void assertSwaggerYaml(String swaggerYamlFilePath, String type = 'string', String pathParam = 'path') {
        def swaggerYamlFile = new File(swaggerYamlFilePath)
        assert Files.exists(swaggerYamlFile.toPath())
        assertSwaggerDocument(new Yaml().load(swaggerYamlFile.getText('UTF-8')), 'yaml', type, pathParam)
    }

    private static void assertSwaggerDocument(def producedSwaggerDocument, String format, String type, String pathParam) {
        assert producedSwaggerDocument.swagger == '2.0'
        assert producedSwaggerDocument.host == 'localhost:8080'
        assert producedSwaggerDocument.basePath == '/'

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == '1'
        assert info.title == 'test'
        assert info.contact.name == 'Joe Blogs'
        assert info.license.name == 'Apache 2.0'

        def tags = producedSwaggerDocument.tags
        assert tags
        assert tags.size() == 1
        assert tags.get(0).name == 'Test'

        def schemes = producedSwaggerDocument.schemes
        assert schemes
        assert schemes.size() == 1
        assert schemes.get(0) == 'http'

        def paths = producedSwaggerDocument.paths
        assert paths
        assertPaths(paths, format, type, 'withannotation', pathParam)
        assertPaths(paths, format, type, 'withoutannotation', pathParam)
        // After path assertion for better test output i.e. this won't tell us what is missing, but tells us we are checking everything
        assert paths.size() == 38

        def securityDefinitions = producedSwaggerDocument.securityDefinitions
        assert securityDefinitions
        assert securityDefinitions.size() == 1
        assert securityDefinitions.MyBasicAuth.type == 'basic'

        def definitions = producedSwaggerDocument.definitions
        assert definitions
        assert definitions.size() == 7
        assert definitions.RequestModel.type == 'object'
        assert definitions.RequestModel.properties.size() == 2
        assert definitions.RequestModel.properties.name.type == type
        assert definitions.RequestModel.properties.value.type == type
        assert definitions.ResponseModel.type == 'object'
        assert definitions.ResponseModel.properties.size() == 1
        assert definitions.ResponseModel.properties.name.type == type
        assert definitions.SubResponseModel.type == 'object'
        assert definitions.SubResponseModel.properties.size() == 2
        assert definitions.SubResponseModel.properties.name.type == type
        assert definitions.SubResponseModel.properties.value.type == type
        assert definitions.OuterJsonSubType.type == 'object'
        assert definitions.OuterJsonSubType.discriminator == '__outerType'
        assert definitions.OuterJsonSubType.properties.size() == 1
        assert definitions.OuterJsonSubType.properties.innerJsonSubType.'$ref' == '#/definitions/InnerJsonSubType'
        assert definitions.SubOuterJsonSubType.allOf[0].'$ref' == '#/definitions/OuterJsonSubType'
        assert definitions.SubOuterJsonSubType.allOf[1].type == 'object'
        assert definitions.SubOuterJsonSubType.allOf[1].properties.size() == 1
        assert definitions.SubOuterJsonSubType.allOf[1].properties.subValue.type == type
        assert definitions.InnerJsonSubType.type == 'object'
        assert definitions.InnerJsonSubType.discriminator == '__innerType'
        assert definitions.InnerJsonSubType.properties.size() == 1
        assert definitions.InnerJsonSubType.properties.value.type == type
        assert definitions.SubInnerJsonSubType.allOf[0].'$ref' == '#/definitions/InnerJsonSubType'
        assert definitions.SubInnerJsonSubType.allOf[1].type == 'object'
        assert definitions.SubInnerJsonSubType.allOf[1].properties.size() == 1
        assert definitions.SubInnerJsonSubType.allOf[1].properties.subValue.type == type
    }

    private static void assertPaths(paths, String format, String type, String path, String pathParam) {
        def ok = format == 'json' ? '200' : 200
        def created = format == 'json' ? '201' : 201

        assert paths."/root/${path}/basic".get.tags == ['Test']
        assert paths."/root/${path}/basic".get.summary == 'A basic operation'
        assert paths."/root/${path}/basic".get.description == 'Test resource'
        assert paths."/root/${path}/basic".get.operationId == 'basic'
        assert paths."/root/${path}/basic".get.produces == null
        assert paths."/root/${path}/basic".get.consumes == null
        assert paths."/root/${path}/basic".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/basic".get.responses.get(ok).schema.type == type
        assert paths."/root/${path}/basic".get.security.basic

        assert paths."/root/${path}/default".get.tags == ['Test']
        assert paths."/root/${path}/default".get.summary == 'A default operation'
        assert paths."/root/${path}/default".get.description == 'Test resource'
        assert paths."/root/${path}/default".get.operationId == 'defaultResponse'
        assert paths."/root/${path}/default".get.produces == null
        assert paths."/root/${path}/default".get.consumes == null
        if (paths."/root/${path}/default".get.responses.default) {
            assert paths."/root/${path}/default".get.responses.default.description == 'successful operation'
            assert paths."/root/${path}/default".get.responses.default.schema == null
        } else if (paths."/root/${path}/default".get.responses.get(ok)) {
            assert paths."/root/${path}/default".get.responses.get(ok).description == 'successful operation'
            // TODO: Spring produces `object`, whereas JAX-RS produces null
//            assert paths."/root/${path}/default".get.responses.get(ok).schema == null
        } else {
            assert false: "No response found for /root/${path}/default"
        }
        assert paths."/root/${path}/default".get.security.basic

        assert paths."/root/${path}/generics".post.tags == ['Test']
        assert paths."/root/${path}/generics".post.summary == 'A generics operation'
        assert paths."/root/${path}/generics".post.description == 'Test resource'
        assert paths."/root/${path}/generics".post.operationId == 'generics'
        assert paths."/root/${path}/generics".post.produces == null
        assert paths."/root/${path}/generics".post.consumes == null
        assert paths."/root/${path}/generics".post.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/generics".post.responses.get(ok).schema.type == 'array'
        assert paths."/root/${path}/generics".post.responses.get(ok).schema.items.type == type
        assert paths."/root/${path}/generics".post.security.basic
        assert paths."/root/${path}/generics".post.parameters[0].in == 'body'
        assert paths."/root/${path}/generics".post.parameters[0].name == 'body'
        assert paths."/root/${path}/generics".post.parameters[0].required == false
        assert paths."/root/${path}/generics".post.parameters[0].schema.type == 'array'
        assert paths."/root/${path}/generics".post.parameters[0].schema.items.'$ref' == '#/definitions/RequestModel'

        assert paths."/root/${path}/datatype".post.tags == ['Test']
        assert paths."/root/${path}/datatype".post.summary == 'Consumes and Produces operation'
        assert paths."/root/${path}/datatype".post.description == 'Test resource'
        assert paths."/root/${path}/datatype".post.operationId == 'dataType'
        assert paths."/root/${path}/datatype".post.produces == ['application/json']
        assert paths."/root/${path}/datatype".post.consumes == ['application/json']
        if (paths."/root/${path}/datatype".post.responses.default) {
            assert paths."/root/${path}/datatype".post.responses.default.description == 'successful operation'
            assert paths."/root/${path}/datatype".post.responses.default.schema == null
        } else if (paths."/root/${path}/datatype".post.responses.get(ok)) {
            assert paths."/root/${path}/datatype".post.responses.get(ok).description == 'successful operation'
            // TODO: Spring produces `object`, whereas JAX-RS produces null
//            assert paths."/root/${path}/datatype".post.responses.get(ok).schema == null
        } else {
            assert false: "No response found for /root/${path}/datatype"
        }
        assert paths."/root/${path}/datatype".post.security.basic
        assert paths."/root/${path}/datatype".post.parameters[0].in == 'body'
        assert paths."/root/${path}/datatype".post.parameters[0].name == 'body'
        assert paths."/root/${path}/datatype".post.parameters[0].required == false
        assert paths."/root/${path}/datatype".post.parameters[0].schema.'$ref' == '#/definitions/RequestModel'

        assert paths."/root/${path}/response".post.tags == ['Test']
        assert paths."/root/${path}/response".post.summary == 'A response operation'
        assert paths."/root/${path}/response".post.description == 'Test resource'
        assert paths."/root/${path}/response".post.operationId == 'response'
        assert paths."/root/${path}/response".post.produces == null
        assert paths."/root/${path}/response".post.consumes == null
        assert paths."/root/${path}/response".post.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/response".post.responses.get(ok).schema.type == null
        assert paths."/root/${path}/response".post.responses.get(ok).schema.'$ref' == '#/definitions/ResponseModel'
        assert paths."/root/${path}/response".post.security.basic

        assert paths."/root/${path}/responseContainer".post.tags == ['Test']
        assert paths."/root/${path}/responseContainer".post.summary == 'A response container operation'
        assert paths."/root/${path}/responseContainer".post.description == 'Test resource'
        assert paths."/root/${path}/responseContainer".post.operationId == 'responseContainer'
        assert paths."/root/${path}/responseContainer".post.produces == null
        assert paths."/root/${path}/responseContainer".post.consumes == null
        assert paths."/root/${path}/responseContainer".post.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/responseContainer".post.responses.get(ok).schema.type == 'array'
        assert paths."/root/${path}/responseContainer".post.responses.get(ok).schema.items.'$ref' == '#/definitions/ResponseModel'
        assert paths."/root/${path}/responseContainer".post.security.basic

        assert paths."/root/${path}/extended".get.tags == ['Test']
        assert paths."/root/${path}/extended".get.summary == 'An extended operation'
        assert paths."/root/${path}/extended".get.description == 'Test resource'
        assert paths."/root/${path}/extended".get.operationId == 'extended'
        assert paths."/root/${path}/extended".get.produces == null
        assert paths."/root/${path}/extended".get.consumes == null
        assert paths."/root/${path}/extended".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/extended".get.responses.get(ok).schema.type == null
        assert paths."/root/${path}/extended".get.responses.get(ok).schema.'$ref' == '#/definitions/SubResponseModel'
        assert paths."/root/${path}/extended".get.security.basic

        assert paths."/root/${path}/deprecated".get.tags == ['Test']
        assert paths."/root/${path}/deprecated".get.summary == 'A deprecated operation'
        assert paths."/root/${path}/deprecated".get.description == 'Test resource'
        assert paths."/root/${path}/deprecated".get.operationId == 'deprecated'
        assert paths."/root/${path}/deprecated".get.produces == null
        assert paths."/root/${path}/deprecated".get.consumes == null
        assert paths."/root/${path}/deprecated".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/deprecated".get.responses.get(ok).schema.type == type
        assert paths."/root/${path}/deprecated".get.security.basic

        assert paths."/root/${path}/auth".get.tags == ['Test']
        assert paths."/root/${path}/auth".get.summary == 'An auth operation'
        assert paths."/root/${path}/auth".get.description == 'Test resource'
        assert paths."/root/${path}/auth".get.operationId == 'withAuth'
        assert paths."/root/${path}/auth".get.produces == null
        assert paths."/root/${path}/auth".get.consumes == null
        assert paths."/root/${path}/auth".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/auth".get.responses.get(ok).schema.type == type
        assert paths."/root/${path}/auth".get.security.basic

        assert paths."/root/${path}/model".get.tags == ['Test']
        assert paths."/root/${path}/model".get.summary == 'A model operation'
        assert paths."/root/${path}/model".get.description == 'Test resource'
        assert paths."/root/${path}/model".get.operationId == 'model'
        assert paths."/root/${path}/model".get.produces == null
        assert paths."/root/${path}/model".get.consumes == null
        assert paths."/root/${path}/model".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/model".get.responses.get(ok).schema.type == type
        assert paths."/root/${path}/model".get.security.basic

        assert paths."/root/${path}/overriden".get.tags == ['Test']
        assert paths."/root/${path}/overriden".get.summary == 'An overriden operation description'
        assert paths."/root/${path}/overriden".get.description == 'Test resource'
        assert paths."/root/${path}/overriden".get.operationId == 'overriden'
        assert paths."/root/${path}/overriden".get.produces == null
        assert paths."/root/${path}/overriden".get.consumes == null
        assert paths."/root/${path}/overriden".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/overriden".get.responses.get(ok).schema.type == type
        assert paths."/root/${path}/overriden".get.security.basic

        assert paths."/root/${path}/overridenWithoutDescription".get.tags == ['Test']
        assert paths."/root/${path}/overridenWithoutDescription".get.summary == 'An overriden operation'
        assert paths."/root/${path}/overridenWithoutDescription".get.description == 'Test resource'
        assert paths."/root/${path}/overridenWithoutDescription".get.operationId == 'overridenWithoutDescription'
        assert paths."/root/${path}/overridenWithoutDescription".get.produces == null
        assert paths."/root/${path}/overridenWithoutDescription".get.consumes == null
        assert paths."/root/${path}/overridenWithoutDescription".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/overridenWithoutDescription".get.responses.get(ok).schema.type == type
        assert paths."/root/${path}/overridenWithoutDescription".get.security.basic

        assert paths."/root/${path}/hidden" == null

        assert paths."/root/${path}/multipleParameters/{parameter1}".get.tags == ['Test']
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.summary == 'A multiple parameters operation'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.description == 'Test resource'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.operationId == 'multipleParameters'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.produces == null
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.consumes == null
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.responses.get(ok).schema.type == type
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.security.basic
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[0].in == pathParam
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[0].name == 'parameter1'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[0].required == true
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[0].type == 'number'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[1].in == 'query'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[1].name == 'parameter2'
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[1].required == false
        assert paths."/root/${path}/multipleParameters/{parameter1}".get.parameters[1].type == 'boolean'

        assert paths."/root/${path}/patch".patch.tags == ['Test']
        assert paths."/root/${path}/patch".patch.summary == 'A PATCH operation'
        assert paths."/root/${path}/patch".patch.description == 'Test resource'
        assert paths."/root/${path}/patch".patch.operationId == 'patch'
        assert paths."/root/${path}/patch".patch.produces == null
        assert paths."/root/${path}/patch".patch.consumes == null
        assert paths."/root/${path}/patch".patch.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/patch".patch.responses.get(ok).schema.type == type
        assert paths."/root/${path}/patch".patch.security.basic

        assert paths."/root/${path}/options".options.tags == ['Test']
        assert paths."/root/${path}/options".options.summary == 'An OPTIONS operation'
        assert paths."/root/${path}/options".options.description == 'Test resource'
        assert paths."/root/${path}/options".options.operationId == 'options'
        assert paths."/root/${path}/options".options.produces == null
        assert paths."/root/${path}/options".options.consumes == null
        if (paths."/root/${path}/options".options.responses.default) {
            assert paths."/root/${path}/options".options.responses.default.description == 'successful operation'
            assert paths."/root/${path}/options".options.responses.default.schema == null
        } else if (paths."/root/${path}/options".options.responses.get(ok)) {
            assert paths."/root/${path}/options".options.responses.get(ok).description == 'successful operation'
            // TODO: Spring produces `object`, whereas JAX-RS produces null
//            assert paths."/root/${path}/options".options.responses.get(ok).schema == null
        } else {
            assert false: "No response found for /root/${path}/options"
        }
        assert paths."/root/${path}/options".options.security.basic

        assert paths."/root/${path}/head".head.tags == ['Test']
        assert paths."/root/${path}/head".head.summary == 'An HEAD operation'
        assert paths."/root/${path}/head".head.description == 'Test resource'
        assert paths."/root/${path}/head".head.operationId == 'head'
        assert paths."/root/${path}/head".head.produces == null
        assert paths."/root/${path}/head".head.consumes == null
        assert paths."/root/${path}/head".head.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/head".head.responses.get(ok).schema.type == type
        assert paths."/root/${path}/head".head.security.basic

        assert paths."/root/${path}/implicitparams".post.tags == ['Test']
        assert paths."/root/${path}/implicitparams".post.summary == 'An implicit params operation'
        assert paths."/root/${path}/implicitparams".post.description == 'Test resource'
        assert paths."/root/${path}/implicitparams".post.operationId == 'implicitParams'
        assert paths."/root/${path}/implicitparams".post.produces == null
        assert paths."/root/${path}/implicitparams".post.consumes == null
        assert paths."/root/${path}/implicitparams".post.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/implicitparams".post.responses.get(ok).schema.type == type
        assert paths."/root/${path}/implicitparams".post.security.basic
        assert paths."/root/${path}/implicitparams".post.parameters[0].in == 'body'
        assert paths."/root/${path}/implicitparams".post.parameters[0].name == 'body'
        assert paths."/root/${path}/implicitparams".post.parameters[0].required == true
        assert paths."/root/${path}/implicitparams".post.parameters[0].schema.'$ref' == '#/definitions/RequestModel'
        assert paths."/root/${path}/implicitparams".post.parameters[1].in == 'header'
        assert paths."/root/${path}/implicitparams".post.parameters[1].name == 'id'
        assert paths."/root/${path}/implicitparams".post.parameters[1].required == false
        assert paths."/root/${path}/implicitparams".post.parameters[1].type == 'string'

        assert paths."/root/${path}/createdrequest".post.tags == ['Test']
        assert paths."/root/${path}/createdrequest".post.summary == 'A created request operation'
        assert paths."/root/${path}/createdrequest".post.description == 'Test resource'
        assert paths."/root/${path}/createdrequest".post.operationId == 'createdRequest'
        assert paths."/root/${path}/createdrequest".post.produces == null
        assert paths."/root/${path}/createdrequest".post.consumes == null
        assert paths."/root/${path}/createdrequest".post.responses.get(created).description == 'successful operation'
        assert paths."/root/${path}/createdrequest".post.responses.get(created).schema.type == type
        assert paths."/root/${path}/createdrequest".post.security.basic

        assert paths."/root/${path}/innerjsonsubtype".get.tags == ['Test']
        assert paths."/root/${path}/innerjsonsubtype".get.summary == 'A inner JSON sub type operation'
        assert paths."/root/${path}/innerjsonsubtype".get.description == 'Test resource'
        assert paths."/root/${path}/innerjsonsubtype".get.operationId == 'innerJsonSubType'
        assert paths."/root/${path}/innerjsonsubtype".get.produces == null
        assert paths."/root/${path}/innerjsonsubtype".get.consumes == null
        assert paths."/root/${path}/innerjsonsubtype".get.responses.get(ok).description == 'successful operation'
        assert paths."/root/${path}/innerjsonsubtype".get.responses.get(ok).schema.'$ref' == '#/definitions/OuterJsonSubType'
        assert paths."/root/${path}/innerjsonsubtype".get.security.basic
    }
}

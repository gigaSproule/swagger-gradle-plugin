package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import groovy.json.JsonSlurper
import org.gradle.api.internal.ClosureBackedAction
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files

import static org.junit.Assert.fail

@RunWith(Parameterized)
class OutputITest extends AbstractPluginITest {

    @Parameterized.Parameters
    static Collection<ClosureBackedAction<SwaggerExtension>> data() {
        [
            getGroovySwaggerExtensionClosure(),
            getJavaSwaggerExtensionClosure(),
            getKotlinSwaggerExtensionClosure()
        ]
    }

    private ClosureBackedAction<SwaggerExtension> swaggerExtensionClosure

    OutputITest(ClosureBackedAction<SwaggerExtension> swaggerExtensionClosure) {
        this.swaggerExtensionClosure = swaggerExtensionClosure
    }

    @Test
    void producesSwaggerDocumentationWithJaxRs() {
        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + UUID.randomUUID()
        project.extensions.configure(SwaggerExtension, swaggerExtensionClosure)
        project.extensions.getByType(SwaggerExtension).apiSourceExtensions.each {
            it.swaggerDirectory = expectedSwaggerDirectory
        }

        project.tasks.generateSwaggerDocumentation.execute()

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json", 'string')
    }

    @Test
    void producesSwaggerDocumentationWithSpringMvc() {
        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + UUID.randomUUID()
        project.extensions.configure(SwaggerExtension, swaggerExtensionClosure)
        project.extensions.getByType(SwaggerExtension).apiSourceExtensions.each {
            it.swaggerDirectory = expectedSwaggerDirectory
            it.springmvc = true
        }

        project.tasks.generateSwaggerDocumentation.execute()

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json", 'string')
    }

    @Test
    void producesSwaggerDocumentationWithModelSubstitution() {
        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + UUID.randomUUID()
        project.extensions.configure(SwaggerExtension, swaggerExtensionClosure)
        project.extensions.getByType(SwaggerExtension).apiSourceExtensions.each {
            it.swaggerDirectory = expectedSwaggerDirectory
            it.modelSubstitute = 'model-substitution'
        }

        project.tasks.generateSwaggerDocumentation.execute()

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json", 'integer')
    }

    @Test
    void produceSwaggerDocumentationInMultipleFormats() {
        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + UUID.randomUUID()
        project.extensions.configure(SwaggerExtension, swaggerExtensionClosure)
        project.extensions.getByType(SwaggerExtension).apiSourceExtensions.each {
            it.swaggerDirectory = expectedSwaggerDirectory
            it.outputFormats = ['json', 'yaml']
        }

        project.tasks.generateSwaggerDocumentation.execute()

        assertSwaggerJson("${expectedSwaggerDirectory}/swagger.json")
        assertSwaggerYaml("${expectedSwaggerDirectory}/swagger.yaml")
    }

    static ClosureBackedAction<SwaggerExtension> getGroovySwaggerExtensionClosure() {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    springmvc = true
                    schemes = ['http']
                    modelSubstitute = ''
                    info {
                        title = project.name
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
                }
            }
        )
    }

    static ClosureBackedAction<SwaggerExtension> getJavaSwaggerExtensionClosure() {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.java']
                    springmvc = true
                    schemes = ['http']
                    modelSubstitute = ''
                    info {
                        title = project.name
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
                }
            }
        )
    }

    static ClosureBackedAction<SwaggerExtension> getKotlinSwaggerExtensionClosure() {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.kotlin']
                    springmvc = true
                    schemes = ['http']
                    modelSubstitute = ''
                    info {
                        title = project.name
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
                }
            }
        )
    }

    private static Object assertSwaggerJson(String swaggerJsonFilePath, String type = 'string') {
        def swaggerJsonFile = new File(swaggerJsonFilePath)
        assert Files.exists(swaggerJsonFile.toPath())
        assertSwaggerDocument(new JsonSlurper().parse(swaggerJsonFile, 'UTF-8'), 'json', type)
    }

    private static Object assertSwaggerYaml(String swaggerYamlFilePath, String type = 'string') {
        def swaggerYamlFile = new File(swaggerYamlFilePath)
        assert Files.exists(swaggerYamlFile.toPath())
        assertSwaggerDocument(new Yaml().load(swaggerYamlFile.getText('UTF-8')), 'yaml', type)
    }

    private static void assertSwaggerDocument(def producedSwaggerDocument, String format, String type) {
        def ok = format == 'json' ? '200' : 200

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
        assert paths.size() == 26
        assert paths.'/root/withannotation/basic'.get.tags == ['Test']
        assert paths.'/root/withannotation/basic'.get.summary == 'A basic operation'
        assert paths.'/root/withannotation/basic'.get.description == 'Test resource'
        assert paths.'/root/withannotation/basic'.get.operationId == 'basic'
        assert paths.'/root/withannotation/basic'.get.produces == null
        assert paths.'/root/withannotation/basic'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/basic'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withannotation/basic'.get.security.basic

        assert paths.'/root/withannotation/default'.get.tags == ['Test']
        assert paths.'/root/withannotation/default'.get.summary == 'A default operation'
        assert paths.'/root/withannotation/default'.get.description == 'Test resource'
        assert paths.'/root/withannotation/default'.get.operationId == 'defaultResponse'
        assert paths.'/root/withannotation/default'.get.produces == null
        if (paths.'/root/withannotation/default'.get.responses.default) {
            assert paths.'/root/withannotation/default'.get.responses.default.description == 'successful operation'
        } else if (paths.'/root/withannotation/default'.get.responses.get(ok)) {
            assert paths.'/root/withannotation/default'.get.responses.get(ok).description == 'successful operation'
        } else {
            fail('No response found for /root/withannotation/default')
        }
        assert paths.'/root/withannotation/default'.get.security.basic

        assert paths.'/root/withannotation/generics'.post.tags == ['Test']
        assert paths.'/root/withannotation/generics'.post.summary == 'A generics operation'
        assert paths.'/root/withannotation/generics'.post.description == 'Test resource'
        assert paths.'/root/withannotation/generics'.post.operationId == 'generics'
        assert paths.'/root/withannotation/generics'.post.produces == null
        assert paths.'/root/withannotation/generics'.post.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/generics'.post.responses.get(ok).schema.type == 'array'
        assert paths.'/root/withannotation/generics'.post.responses.get(ok).schema.items.type == type
        assert paths.'/root/withannotation/generics'.post.security.basic

        assert paths.'/root/withannotation/datatype'.post.tags == ['Test']
        assert paths.'/root/withannotation/datatype'.post.summary == 'Consumes and Produces operation'
        assert paths.'/root/withannotation/datatype'.post.description == 'Test resource'
        assert paths.'/root/withannotation/datatype'.post.operationId == 'dataType'
        assert paths.'/root/withannotation/datatype'.post.produces == ['application/json']
        if (paths.'/root/withannotation/datatype'.post.responses.default) {
            assert paths.'/root/withannotation/datatype'.post.responses.default.description == 'successful operation'
        } else if (paths.'/root/withannotation/datatype'.post.responses.get(ok)) {
            assert paths.'/root/withannotation/datatype'.post.responses.get(ok).description == 'successful operation'
        } else {
            fail('No response found for /root/withannotation/datatype')
        }
        assert paths.'/root/withannotation/datatype'.post.security.basic

        assert paths.'/root/withannotation/response'.post.tags == ['Test']
        assert paths.'/root/withannotation/response'.post.summary == 'A response operation'
        assert paths.'/root/withannotation/response'.post.description == 'Test resource'
        assert paths.'/root/withannotation/response'.post.operationId == 'response'
        assert paths.'/root/withannotation/response'.post.produces == null
        assert paths.'/root/withannotation/response'.post.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/response'.post.responses.get(ok).schema.type == null
        assert paths.'/root/withannotation/response'.post.responses.get(ok).schema.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withannotation/response'.post.security.basic

        assert paths.'/root/withannotation/responseContainer'.post.tags == ['Test']
        assert paths.'/root/withannotation/responseContainer'.post.summary == 'A response container operation'
        assert paths.'/root/withannotation/responseContainer'.post.description == 'Test resource'
        assert paths.'/root/withannotation/responseContainer'.post.operationId == 'responseContainer'
        assert paths.'/root/withannotation/responseContainer'.post.produces == null
        assert paths.'/root/withannotation/responseContainer'.post.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/responseContainer'.post.responses.get(ok).schema.type == 'array'
        assert paths.'/root/withannotation/responseContainer'.post.responses.get(ok).schema.items.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withannotation/responseContainer'.post.security.basic

        assert paths.'/root/withannotation/extended'.get.tags == ['Test']
        assert paths.'/root/withannotation/extended'.get.summary == 'An extended operation'
        assert paths.'/root/withannotation/extended'.get.description == 'Test resource'
        assert paths.'/root/withannotation/extended'.get.operationId == 'extended'
        assert paths.'/root/withannotation/extended'.get.produces == null
        assert paths.'/root/withannotation/extended'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/extended'.get.responses.get(ok).schema.type == null
        assert paths.'/root/withannotation/extended'.get.responses.get(ok).schema.'$ref' == '#/definitions/SubResponseModel'
        assert paths.'/root/withannotation/extended'.get.security.basic

        assert paths.'/root/withannotation/deprecated'.get.tags == ['Test']
        assert paths.'/root/withannotation/deprecated'.get.summary == 'A deprecated operation'
        assert paths.'/root/withannotation/deprecated'.get.description == 'Test resource'
        assert paths.'/root/withannotation/deprecated'.get.operationId == 'deprecated'
        assert paths.'/root/withannotation/deprecated'.get.produces == null
        assert paths.'/root/withannotation/deprecated'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/deprecated'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withannotation/deprecated'.get.security.basic

        assert paths.'/root/withannotation/auth'.get.tags == ['Test']
        assert paths.'/root/withannotation/auth'.get.summary == 'An auth operation'
        assert paths.'/root/withannotation/auth'.get.description == 'Test resource'
        assert paths.'/root/withannotation/auth'.get.operationId == 'withAuth'
        assert paths.'/root/withannotation/auth'.get.produces == null
        assert paths.'/root/withannotation/auth'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/auth'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withannotation/auth'.get.security.basic

        assert paths.'/root/withannotation/model'.get.tags == ['Test']
        assert paths.'/root/withannotation/model'.get.summary == 'A model operation'
        assert paths.'/root/withannotation/model'.get.description == 'Test resource'
        assert paths.'/root/withannotation/model'.get.operationId == 'model'
        assert paths.'/root/withannotation/model'.get.produces == null
        assert paths.'/root/withannotation/model'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/model'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withannotation/model'.get.security.basic

        assert paths.'/root/withannotation/overriden'.get.tags == ['Test']
        assert paths.'/root/withannotation/overriden'.get.summary == 'An overriden operation description'
        assert paths.'/root/withannotation/overriden'.get.description == 'Test resource'
        assert paths.'/root/withannotation/overriden'.get.operationId == 'overriden'
        assert paths.'/root/withannotation/overriden'.get.produces == null
        assert paths.'/root/withannotation/overriden'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/overriden'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withannotation/overriden'.get.security.basic

        assert paths.'/root/withannotation/overridenWithoutDescription'.get.tags == ['Test']
        assert paths.'/root/withannotation/overridenWithoutDescription'.get.summary == 'An overriden operation'
        assert paths.'/root/withannotation/overridenWithoutDescription'.get.description == 'Test resource'
        assert paths.'/root/withannotation/overridenWithoutDescription'.get.operationId == 'overridenWithoutDescription'
        assert paths.'/root/withannotation/overridenWithoutDescription'.get.produces == null
        assert paths.'/root/withannotation/overridenWithoutDescription'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/overridenWithoutDescription'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withannotation/overridenWithoutDescription'.get.security.basic

        assert paths.'/root/withannotation/hidden' == null

        assert paths.'/root/withannotation/ignoredModel'.get.tags == ['Test']
        assert paths.'/root/withannotation/ignoredModel'.get.summary == 'An ignored model'
        assert paths.'/root/withannotation/ignoredModel'.get.description == 'Test resource'
        assert paths.'/root/withannotation/ignoredModel'.get.operationId == 'ignoredModel'
        assert paths.'/root/withannotation/ignoredModel'.get.produces == null
        assert paths.'/root/withannotation/ignoredModel'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withannotation/ignoredModel'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withannotation/ignoredModel'.get.security.basic

        assert paths.'/root/withoutannotation/basic'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/basic'.get.summary == 'A basic operation'
        assert paths.'/root/withoutannotation/basic'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/basic'.get.operationId == 'basic'
        assert paths.'/root/withoutannotation/basic'.get.produces == null
        assert paths.'/root/withoutannotation/basic'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/basic'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withoutannotation/basic'.get.security.basic

        assert paths.'/root/withoutannotation/default'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/default'.get.summary == 'A default operation'
        assert paths.'/root/withoutannotation/default'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/default'.get.operationId == 'defaultResponse'
        assert paths.'/root/withoutannotation/default'.get.produces == null
        if (paths.'/root/withoutannotation/default'.get.responses.default) {
            assert paths.'/root/withoutannotation/default'.get.responses.default.description == 'successful operation'
        } else if (paths.'/root/withoutannotation/default'.get.responses.get(ok)) {
            assert paths.'/root/withoutannotation/default'.get.responses.get(ok).description == 'successful operation'
        } else {
            fail('No response found for /root/withoutannotation/default')
        }
        assert paths.'/root/withoutannotation/default'.get.security.basic

        assert paths.'/root/withoutannotation/generics'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/generics'.post.summary == 'A generics operation'
        assert paths.'/root/withoutannotation/generics'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/generics'.post.operationId == 'generics'
        assert paths.'/root/withoutannotation/generics'.post.produces == null
        assert paths.'/root/withoutannotation/generics'.post.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/generics'.post.responses.get(ok).schema.type == 'array'
        assert paths.'/root/withoutannotation/generics'.post.responses.get(ok).schema.items.type == type
        assert paths.'/root/withoutannotation/generics'.post.security.basic

        assert paths.'/root/withoutannotation/datatype'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/datatype'.post.summary == 'Consumes and Produces operation'
        assert paths.'/root/withoutannotation/datatype'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/datatype'.post.operationId == 'dataType'
        assert paths.'/root/withoutannotation/datatype'.post.produces == ['application/json']
        if (paths.'/root/withoutannotation/datatype'.post.responses.default) {
            assert paths.'/root/withoutannotation/datatype'.post.responses.default.description == 'successful operation'
        } else if (paths.'/root/withoutannotation/datatype'.post.responses.get(ok)) {
            assert paths.'/root/withoutannotation/datatype'.post.responses.get(ok).description == 'successful operation'
        } else {
            fail('No response found for /root/withoutannotation/datatype')
        }
        assert paths.'/root/withoutannotation/datatype'.post.security.basic

        assert paths.'/root/withoutannotation/response'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/response'.post.summary == 'A response operation'
        assert paths.'/root/withoutannotation/response'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/response'.post.operationId == 'response'
        assert paths.'/root/withoutannotation/response'.post.produces == null
        assert paths.'/root/withoutannotation/response'.post.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/response'.post.responses.get(ok).schema.type == null
        assert paths.'/root/withoutannotation/response'.post.responses.get(ok).schema.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withoutannotation/response'.post.security.basic

        assert paths.'/root/withoutannotation/responseContainer'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/responseContainer'.post.summary == 'A response container operation'
        assert paths.'/root/withoutannotation/responseContainer'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/responseContainer'.post.operationId == 'responseContainer'
        assert paths.'/root/withoutannotation/responseContainer'.post.produces == null
        assert paths.'/root/withoutannotation/responseContainer'.post.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/responseContainer'.post.responses.get(ok).schema.type == 'array'
        assert paths.'/root/withoutannotation/responseContainer'.post.responses.get(ok).schema.items.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withoutannotation/responseContainer'.post.security.basic

        assert paths.'/root/withoutannotation/extended'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/extended'.get.summary == 'An extended operation'
        assert paths.'/root/withoutannotation/extended'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/extended'.get.operationId == 'extended'
        assert paths.'/root/withoutannotation/extended'.get.produces == null
        assert paths.'/root/withoutannotation/extended'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/extended'.get.responses.get(ok).schema.type == null
        assert paths.'/root/withoutannotation/extended'.get.responses.get(ok).schema.'$ref' == '#/definitions/SubResponseModel'
        assert paths.'/root/withoutannotation/extended'.get.security.basic

        assert paths.'/root/withoutannotation/deprecated'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/deprecated'.get.summary == 'A deprecated operation'
        assert paths.'/root/withoutannotation/deprecated'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/deprecated'.get.operationId == 'deprecated'
        assert paths.'/root/withoutannotation/deprecated'.get.produces == null
        assert paths.'/root/withoutannotation/deprecated'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/deprecated'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withoutannotation/deprecated'.get.security.basic

        assert paths.'/root/withoutannotation/auth'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/auth'.get.summary == 'An auth operation'
        assert paths.'/root/withoutannotation/auth'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/auth'.get.operationId == 'withAuth'
        assert paths.'/root/withoutannotation/auth'.get.produces == null
        assert paths.'/root/withoutannotation/auth'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/auth'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withoutannotation/auth'.get.security.basic

        assert paths.'/root/withoutannotation/model'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/model'.get.summary == 'A model operation'
        assert paths.'/root/withoutannotation/model'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/model'.get.operationId == 'model'
        assert paths.'/root/withoutannotation/model'.get.produces == null
        assert paths.'/root/withoutannotation/model'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/model'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withoutannotation/model'.get.security.basic

        assert paths.'/root/withoutannotation/overriden'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/overriden'.get.summary == 'An overriden operation description'
        assert paths.'/root/withoutannotation/overriden'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/overriden'.get.operationId == 'overriden'
        assert paths.'/root/withoutannotation/overriden'.get.produces == null
        assert paths.'/root/withoutannotation/overriden'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/overriden'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withoutannotation/overriden'.get.security.basic

        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.summary == 'An overriden operation'
        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.operationId == 'overridenWithoutDescription'
        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.produces == null
        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withoutannotation/overridenWithoutDescription'.get.security.basic

        assert paths.'/root/withoutannotation/hidden' == null

        assert paths.'/root/withoutannotation/ignoredModel'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/ignoredModel'.get.summary == 'An ignored model'
        assert paths.'/root/withoutannotation/ignoredModel'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/ignoredModel'.get.operationId == 'ignoredModel'
        assert paths.'/root/withoutannotation/ignoredModel'.get.produces == null
        assert paths.'/root/withoutannotation/ignoredModel'.get.responses.get(ok).description == 'successful operation'
        assert paths.'/root/withoutannotation/ignoredModel'.get.responses.get(ok).schema.type == type
        assert paths.'/root/withoutannotation/ignoredModel'.get.security.basic

        def securityDefinitions = producedSwaggerDocument.securityDefinitions
        assert securityDefinitions
        assert securityDefinitions.size() == 1
        assert securityDefinitions.MyBasicAuth.type == 'basic'

        def definitions = producedSwaggerDocument.definitions
        assert definitions
        assert definitions.size() == 3
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
    }
}

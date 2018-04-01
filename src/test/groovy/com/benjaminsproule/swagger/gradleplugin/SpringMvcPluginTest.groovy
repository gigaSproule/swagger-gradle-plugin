package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.nio.file.Files

class SpringMvcPluginTest {
    Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.benjaminsproule.swagger'
    }

    @After
    void tearDown() {
        project = null
    }

    @Test
    void producesSwaggerDocumentation() {
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin


        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + UUID.randomUUID()
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule']
                    springmvc = true
                    schemes = ['http']
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
                    swaggerDirectory = expectedSwaggerDirectory
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        ))

        project.tasks.generateSwaggerDocumentation.execute()

        def swaggerFile = new File("${expectedSwaggerDirectory}/swagger.json")
        assertSwaggerJson(swaggerFile)
    }

    private static void assertSwaggerJson(File swaggerJsonFile) {
        assert Files.exists(swaggerJsonFile.toPath())

        JsonSlurper jsonSlurper = new JsonSlurper()

        def producedSwaggerDocument = jsonSlurper.parse(swaggerJsonFile)

        assert producedSwaggerDocument.swagger == '2.0'
        assert producedSwaggerDocument.host == 'localhost:8080'
        assert producedSwaggerDocument.basePath == '/'

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == '1'
        assert info.title == 'test'

        def tags = producedSwaggerDocument.tags
        assert tags
        assert tags.size() == 1
        assert tags.get(0).get('name') == 'Test'

        def paths = producedSwaggerDocument.get('paths')
        assert paths
        assert paths.size() == 22
        assert paths.'/root/withannotation/basic'.get.tags == ['Test']
        assert paths.'/root/withannotation/basic'.get.summary == 'A basic operation'
        assert paths.'/root/withannotation/basic'.get.description == 'Test resource'
        assert paths.'/root/withannotation/basic'.get.operationId == 'basic'
        assert paths.'/root/withannotation/basic'.get.produces == null
        assert paths.'/root/withannotation/basic'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/basic'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withannotation/basic'.get.security.basic
        assert paths.'/root/withannotation/default'.get.tags == ['Test']
        assert paths.'/root/withannotation/default'.get.summary == 'A default operation'
        assert paths.'/root/withannotation/default'.get.description == 'Test resource'
        assert paths.'/root/withannotation/default'.get.operationId == 'defaultResponse'
        assert paths.'/root/withannotation/default'.get.produces == null
        assert paths.'/root/withannotation/default'.get.responses.default.description == 'successful operation'
        assert paths.'/root/withannotation/default'.get.security.basic
        assert paths.'/root/withannotation/generics'.post.tags == ['Test']
        assert paths.'/root/withannotation/generics'.post.summary == 'A generics operation'
        assert paths.'/root/withannotation/generics'.post.description == 'Test resource'
        assert paths.'/root/withannotation/generics'.post.operationId == 'generics'
        assert paths.'/root/withannotation/generics'.post.produces == null
        assert paths.'/root/withannotation/generics'.post.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/generics'.post.responses.'200'.schema.type == 'array'
        assert paths.'/root/withannotation/generics'.post.responses.'200'.schema.items.type == 'string'
        assert paths.'/root/withannotation/generics'.post.security.basic
        assert paths.'/root/withannotation/datatype'.post.tags == ['Test']
        assert paths.'/root/withannotation/datatype'.post.summary == 'Consumes and Produces operation'
        assert paths.'/root/withannotation/datatype'.post.description == 'Test resource'
        assert paths.'/root/withannotation/datatype'.post.operationId == 'dataType'
        assert paths.'/root/withannotation/datatype'.post.produces == ['application/json']
        assert paths.'/root/withannotation/datatype'.post.responses.default.description == 'successful operation'
        assert paths.'/root/withannotation/datatype'.post.security.basic
        assert paths.'/root/withannotation/response'.post.tags == ['Test']
        assert paths.'/root/withannotation/response'.post.summary == 'A response operation'
        assert paths.'/root/withannotation/response'.post.description == 'Test resource'
        assert paths.'/root/withannotation/response'.post.operationId == 'response'
        assert paths.'/root/withannotation/response'.post.produces == null
        assert paths.'/root/withannotation/response'.post.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/response'.post.responses.'200'.schema.type == null
        assert paths.'/root/withannotation/response'.post.responses.'200'.schema.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withannotation/response'.post.security.basic
        assert paths.'/root/withannotation/responseContainer'.post.tags == ['Test']
        assert paths.'/root/withannotation/responseContainer'.post.summary == 'A response container operation'
        assert paths.'/root/withannotation/responseContainer'.post.description == 'Test resource'
        assert paths.'/root/withannotation/responseContainer'.post.operationId == 'responseContainer'
        assert paths.'/root/withannotation/responseContainer'.post.produces == null
        assert paths.'/root/withannotation/responseContainer'.post.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/responseContainer'.post.responses.'200'.schema.type == 'array'
        assert paths.'/root/withannotation/responseContainer'.post.responses.'200'.schema.items.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withannotation/responseContainer'.post.security.basic
        assert paths.'/root/withannotation/extended'.get.tags == ['Test']
        assert paths.'/root/withannotation/extended'.get.summary == 'An extended operation'
        assert paths.'/root/withannotation/extended'.get.description == 'Test resource'
        assert paths.'/root/withannotation/extended'.get.operationId == 'extended'
        assert paths.'/root/withannotation/extended'.get.produces == null
        assert paths.'/root/withannotation/extended'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/extended'.get.responses.'200'.schema.type == null
        assert paths.'/root/withannotation/extended'.get.responses.'200'.schema.'$ref' == '#/definitions/SubResponseModel'
        assert paths.'/root/withannotation/extended'.get.security.basic
        assert paths.'/root/withannotation/deprecated'.get.tags == ['Test']
        assert paths.'/root/withannotation/deprecated'.get.summary == 'A deprecated operation'
        assert paths.'/root/withannotation/deprecated'.get.description == 'Test resource'
        assert paths.'/root/withannotation/deprecated'.get.operationId == 'deprecated'
        assert paths.'/root/withannotation/deprecated'.get.produces == null
        assert paths.'/root/withannotation/deprecated'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/deprecated'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withannotation/deprecated'.get.security.basic
        assert paths.'/root/withannotation/auth'.get.tags == ['Test']
        assert paths.'/root/withannotation/auth'.get.summary == 'An auth operation'
        assert paths.'/root/withannotation/auth'.get.description == 'Test resource'
        assert paths.'/root/withannotation/auth'.get.operationId == 'withAuth'
        assert paths.'/root/withannotation/auth'.get.produces == null
        assert paths.'/root/withannotation/auth'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/auth'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withannotation/auth'.get.security.basic
        assert paths.'/root/withannotation/model'.get.tags == ['Test']
        assert paths.'/root/withannotation/model'.get.summary == 'A model operation'
        assert paths.'/root/withannotation/model'.get.description == 'Test resource'
        assert paths.'/root/withannotation/model'.get.operationId == 'model'
        assert paths.'/root/withannotation/model'.get.produces == null
        assert paths.'/root/withannotation/model'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/model'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withannotation/model'.get.security.basic
        assert paths.'/root/withannotation/overriden'.get.tags == ['Test']
        assert paths.'/root/withannotation/overriden'.get.summary == 'An overriden operation description'
        assert paths.'/root/withannotation/overriden'.get.description == 'Test resource'
        assert paths.'/root/withannotation/overriden'.get.operationId == 'overriden'
        assert paths.'/root/withannotation/overriden'.get.produces == null
        assert paths.'/root/withannotation/overriden'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withannotation/overriden'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withannotation/overriden'.get.security.basic
        assert paths.'/root/withannotation/hidden' == null
        assert paths.'/root/withoutannotation/basic'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/basic'.get.summary == 'A basic operation'
        assert paths.'/root/withoutannotation/basic'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/basic'.get.operationId == 'basic'
        assert paths.'/root/withoutannotation/basic'.get.produces == null
        assert paths.'/root/withoutannotation/basic'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/basic'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withoutannotation/basic'.get.security.basic
        assert paths.'/root/withoutannotation/default'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/default'.get.summary == 'A default operation'
        assert paths.'/root/withoutannotation/default'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/default'.get.operationId == 'defaultResponse'
        assert paths.'/root/withoutannotation/default'.get.produces == null
        assert paths.'/root/withoutannotation/default'.get.responses.default.description == 'successful operation'
        assert paths.'/root/withoutannotation/default'.get.security.basic
        assert paths.'/root/withoutannotation/generics'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/generics'.post.summary == 'A generics operation'
        assert paths.'/root/withoutannotation/generics'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/generics'.post.operationId == 'generics'
        assert paths.'/root/withoutannotation/generics'.post.produces == null
        assert paths.'/root/withoutannotation/generics'.post.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/generics'.post.responses.'200'.schema.type == 'array'
        assert paths.'/root/withoutannotation/generics'.post.responses.'200'.schema.items.type == 'string'
        assert paths.'/root/withoutannotation/generics'.post.security.basic
        assert paths.'/root/withoutannotation/datatype'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/datatype'.post.summary == 'Consumes and Produces operation'
        assert paths.'/root/withoutannotation/datatype'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/datatype'.post.operationId == 'dataType'
        assert paths.'/root/withoutannotation/datatype'.post.produces == ['application/json']
        assert paths.'/root/withoutannotation/datatype'.post.responses.default.description == 'successful operation'
        assert paths.'/root/withoutannotation/datatype'.post.security.basic
        assert paths.'/root/withoutannotation/response'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/response'.post.summary == 'A response operation'
        assert paths.'/root/withoutannotation/response'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/response'.post.operationId == 'response'
        assert paths.'/root/withoutannotation/response'.post.produces == null
        assert paths.'/root/withoutannotation/response'.post.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/response'.post.responses.'200'.schema.type == null
        assert paths.'/root/withoutannotation/response'.post.responses.'200'.schema.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withoutannotation/response'.post.security.basic
        assert paths.'/root/withoutannotation/responseContainer'.post.tags == ['Test']
        assert paths.'/root/withoutannotation/responseContainer'.post.summary == 'A response container operation'
        assert paths.'/root/withoutannotation/responseContainer'.post.description == 'Test resource'
        assert paths.'/root/withoutannotation/responseContainer'.post.operationId == 'responseContainer'
        assert paths.'/root/withoutannotation/responseContainer'.post.produces == null
        assert paths.'/root/withoutannotation/responseContainer'.post.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/responseContainer'.post.responses.'200'.schema.type == 'array'
        assert paths.'/root/withoutannotation/responseContainer'.post.responses.'200'.schema.items.'$ref' == '#/definitions/ResponseModel'
        assert paths.'/root/withoutannotation/responseContainer'.post.security.basic
        assert paths.'/root/withoutannotation/extended'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/extended'.get.summary == 'An extended operation'
        assert paths.'/root/withoutannotation/extended'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/extended'.get.operationId == 'extended'
        assert paths.'/root/withoutannotation/extended'.get.produces == null
        assert paths.'/root/withoutannotation/extended'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/extended'.get.responses.'200'.schema.type == null
        assert paths.'/root/withoutannotation/extended'.get.responses.'200'.schema.'$ref' == '#/definitions/SubResponseModel'
        assert paths.'/root/withoutannotation/extended'.get.security.basic
        assert paths.'/root/withoutannotation/deprecated'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/deprecated'.get.summary == 'A deprecated operation'
        assert paths.'/root/withoutannotation/deprecated'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/deprecated'.get.operationId == 'deprecated'
        assert paths.'/root/withoutannotation/deprecated'.get.produces == null
        assert paths.'/root/withoutannotation/deprecated'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/deprecated'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withoutannotation/deprecated'.get.security.basic
        assert paths.'/root/withoutannotation/auth'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/auth'.get.summary == 'An auth operation'
        assert paths.'/root/withoutannotation/auth'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/auth'.get.operationId == 'withAuth'
        assert paths.'/root/withoutannotation/auth'.get.produces == null
        assert paths.'/root/withoutannotation/auth'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/auth'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withoutannotation/auth'.get.security.basic
        assert paths.'/root/withoutannotation/model'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/model'.get.summary == 'A model operation'
        assert paths.'/root/withoutannotation/model'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/model'.get.operationId == 'model'
        assert paths.'/root/withoutannotation/model'.get.produces == null
        assert paths.'/root/withoutannotation/model'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/model'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withoutannotation/model'.get.security.basic
        assert paths.'/root/withoutannotation/overriden'.get.tags == ['Test']
        assert paths.'/root/withoutannotation/overriden'.get.summary == 'An overriden operation description'
        assert paths.'/root/withoutannotation/overriden'.get.description == 'Test resource'
        assert paths.'/root/withoutannotation/overriden'.get.operationId == 'overriden'
        assert paths.'/root/withoutannotation/overriden'.get.produces == null
        assert paths.'/root/withoutannotation/overriden'.get.responses.'200'.description == 'successful operation'
        assert paths.'/root/withoutannotation/overriden'.get.responses.'200'.schema.type == 'string'
        assert paths.'/root/withoutannotation/overriden'.get.security.basic
        assert paths.'/root/withoutannotation/hidden' == null
    }
}

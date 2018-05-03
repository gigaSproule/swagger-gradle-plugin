package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import groovy.json.JsonSlurper
import org.gradle.api.internal.ClosureBackedAction
import org.junit.Test
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class GradleSwaggerPluginTest extends AbstractPluginTest {

    @Test
    void pluginAddsGenerateSwaggerTask() {
        assertTrue(project.tasks.generateSwaggerDocumentation instanceof GenerateSwaggerDocsTask)
    }

    @Test
    void shouldSkipSwaggerGenerationWhenSkipSwaggerPropertySet() {
        project.extensions.extraProperties.set('swagger.skip', true)

        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + UUID.randomUUID()
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>({
            apiSource {
                locations = ['com.benjaminsproule']
                info {
                    title = project.name
                    version = '1'
                }
                swaggerDirectory = expectedSwaggerDirectory
                host = 'localhost:8080'
                basePath = '/'
            }
        }
        ))

        project.tasks.generateSwaggerDocumentation.execute()

        def swaggerFile = new File("${expectedSwaggerDirectory}/swagger.json")
        assertFalse(Files.exists(swaggerFile.toPath()))
    }

    @Test
    void shouldReadMissingConfigFromAnnotations() {
        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + UUID.randomUUID()
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>({
            apiSource {
                locations = ['com.benjaminsproule']
                schemes = ['http']
                swaggerDirectory = expectedSwaggerDirectory
                securityDefinition {
                    name = 'MyBasicAuth'
                    type = 'basic'
                }
            }
        }
        ))

        project.tasks.generateSwaggerDocumentation.execute()

        def swaggerFile = new File("${expectedSwaggerDirectory}/swagger.json")
        assert Files.exists(swaggerFile.toPath())

        JsonSlurper jsonSlurper = new JsonSlurper()

        def producedSwaggerDocument = jsonSlurper.parse(swaggerFile)

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

    @Test
    void generateSwaggerArtifactWhenFlagIsSet() {
        def swaggerRelativeDirectory = "swaggerui-" + UUID.randomUUID()
        def expectedSwaggerDirectory = "${project.buildDir}/${swaggerRelativeDirectory}"
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>({
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

        def swaggerFile = new File("${project.buildDir}/libs/${project.archivesBaseName}-${swaggerRelativeDirectory}.jar")
        assert swaggerFile.exists()
    }
}

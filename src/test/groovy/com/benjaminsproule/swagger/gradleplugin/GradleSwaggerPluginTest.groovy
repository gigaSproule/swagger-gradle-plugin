package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import groovy.json.JsonSlurper
import org.apache.commons.lang3.RandomStringUtils
import org.gradle.api.Project
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class GradleSwaggerPluginTest {
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
    void pluginAddsGenerateSwaggerTask() {
        assertTrue(project.tasks.generateSwaggerDocumentation instanceof GenerateSwaggerDocsTask)
    }

    @Test
    void shouldSkipSwaggerGeneartionWhenSkipSwaggerPropertySet() {
        project.extensions.extraProperties.set('swagger.skip', true)
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin

        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + RandomStringUtils.randomAlphabetic(5)
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>(
            {
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
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin


        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + RandomStringUtils.randomAlphabetic(5)
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>(
            {
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

        assert producedSwaggerDocument.get('host') == 'http://annotated'
        assert producedSwaggerDocument.get('basePath') == '/annotated'

        def info = producedSwaggerDocument.get('info')
        assert info
        assert info.get('version') == 'annotated'
        assert info.get('title') == 'annotated'
    }

    @Test
    void generateSwaggerArtifactWhenFlagIsSet() {
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin

        def swaggerRelativeDirectory = "swaggerui-" + RandomStringUtils.randomAlphabetic(5)
        def expectedSwaggerDirectory = "${project.buildDir}/${swaggerRelativeDirectory}"
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule']
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

        def swaggerFile = new File("${project.buildDir}/libs/${project.archivesBaseName}-${swaggerRelativeDirectory}.jar")
        assert swaggerFile.exists()
    }

    @Test
    void swaggerDocumentationGeneratedInMultipleFormats() {
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin

        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui-" + RandomStringUtils.randomAlphabetic(5)
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule']
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
                    outputFormats = ['json', 'yaml']
                }
            }
        ))

        project.tasks.generateSwaggerDocumentation.execute()

        def swaggerJsonFile = new File("${expectedSwaggerDirectory}/swagger.json")
        assertSwaggerJson(swaggerJsonFile)

        def swaggerYamlFile = new File("${expectedSwaggerDirectory}/swagger.yaml")
        assertSwaggerYaml(swaggerYamlFile)
    }

    private static void assertSwaggerYaml(File swaggerYamlFile) {
        assertTrue(Files.exists(swaggerYamlFile.toPath()))

        Yaml parser = new Yaml()
        def yaml = parser.load(swaggerYamlFile.text)

        assert yaml['swagger'] == '2.0'
        assert yaml.info['version'] == '1'
        assert yaml.info['title'] == 'test'
        assert yaml.info.contact['name'] == 'Joe Blogs'
        assert yaml.info.license['name'] == 'Apache 2.0'
        assert yaml['host'] == 'localhost:8080'
        assert yaml['basePath'] == '/'
    }

    private static void assertSwaggerJson(File swaggerJsonFile) {
        assert Files.exists(swaggerJsonFile.toPath())

        JsonSlurper jsonSlurper = new JsonSlurper()

        def producedSwaggerDocument = jsonSlurper.parse(swaggerJsonFile)

        assert producedSwaggerDocument.get('swagger') == '2.0'
        assert producedSwaggerDocument.get('host') == 'localhost:8080'
        assert producedSwaggerDocument.get('basePath') == '/'

        def info = producedSwaggerDocument.get('info')
        assert info
        assert info.get('version') == '1'
        assert info.get('title') == 'test'

        def tags = producedSwaggerDocument.get('tags')
        assert tags
        assert tags.size() == 1
        assert tags.get(0).get('name') == 'Test'
    }
}

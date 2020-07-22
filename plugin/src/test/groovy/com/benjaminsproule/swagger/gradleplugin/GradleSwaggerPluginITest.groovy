package com.benjaminsproule.swagger.gradleplugin

import groovy.json.JsonSlurper

import java.nio.file.Files
import java.nio.file.Paths

import static org.gradle.testkit.runner.TaskOutcome.*

class GradleSwaggerPluginITest extends AbstractPluginITest {

    def 'Should skip swagger generation when swagger.skip property set'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    locations = ['com.benjaminsproule']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDirAsString}'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def result = pluginTaskRunnerBuilder()
            .withArguments('generateSwaggerDocumentation', '-Pswagger.skip=true')
            .build()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SKIPPED

        assert !new File(testProjectOutputDir, '/swagger.json').exists()
    }

    def 'Should read missing config from annotations'() {
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
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.Definitions']
                    schemes = ['http']
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
            """

        when:
        def result = runPluginTask()

        then:
        result.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        def swaggerFile = new File("${expectedSwaggerDirectory}/swagger.json")
        assert Files.exists(swaggerFile.toPath())

        def producedSwaggerDocument = new JsonSlurper().parse(swaggerFile)

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

    def 'Generate Swagger artifact when flag is set'() {
        given:
        def localRepo = "${testProjectOutputDirAsString}/repo"
        def swaggerRelativeDirectory = "swaggerui-" + UUID.randomUUID()
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/${swaggerRelativeDirectory}"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'maven-publish'
                id 'com.benjaminsproule.swagger'
            }
            group = 'com.benjaminsproule.swagger'
            version = '0.0.1'
            publishing {
                publications {
                    maven(MavenPublication) {
                        artifact "${expectedSwaggerDirectory}/swagger.json"
                    }
                }
                repositories {
                    maven {
                        url "${localRepo}"
                    }
                }
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def runResult = runPluginTask()
        new File("${localRepo}").mkdirs()
        def publishResult = pluginTaskRunnerBuilder()
            .withArguments('publishMavenPublicationToMavenRepository')
            .build()

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        publishResult.task(":publishMavenPublicationToMavenRepository").outcome == SUCCESS

        def swaggerFile = new File("${localRepo}/com/benjaminsproule/swagger/${buildFile.getParentFile().getName()}/0.0.1/${buildFile.getParentFile().getName()}-0.0.1.json")
        assert swaggerFile.exists()
    }

    def 'Generate Swagger artifact when flag is set for multiple formats'() {
        given:
        def localRepo = "${testProjectOutputDirAsString}/repo"
        def swaggerRelativeDirectory = "swaggerui-" + UUID.randomUUID()
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/${swaggerRelativeDirectory}"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'maven-publish'
                id 'com.benjaminsproule.swagger'
            }
            group = 'com.benjaminsproule.swagger'
            version = '0.0.1'
            publishing {
                publications {
                    maven(MavenPublication) {
                        artifact "${expectedSwaggerDirectory}/swagger.json"
                        artifact "${expectedSwaggerDirectory}/swagger.yaml"
                    }
                }
                repositories {
                    maven {
                        url "${localRepo}"
                    }
                }
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    outputFormats = ['json', 'yaml']
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def runResult = runPluginTask()
        new File("${localRepo}").mkdirs()
        def publishResult = pluginTaskRunnerBuilder()
            .withArguments('publishMavenPublicationToMavenRepository')
            .build()

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        publishResult.task(":publishMavenPublicationToMavenRepository").outcome == SUCCESS

        def jsonSwaggerFile = new File("${localRepo}/com/benjaminsproule/swagger/${buildFile.getParentFile().getName()}/0.0.1/${buildFile.getParentFile().getName()}-0.0.1.json")
        assert jsonSwaggerFile.exists()
        def yamlSwaggerFile = new File("${localRepo}/com/benjaminsproule/swagger/${buildFile.getParentFile().getName()}/0.0.1/${buildFile.getParentFile().getName()}-0.0.1.yaml")
        assert yamlSwaggerFile.exists()
    }

    def 'Generate Swagger artifact when flag is set for multiple api source closures'() {
        given:
        def localRepo = "${testProjectOutputDirAsString}/repo"
        def swaggerRelativeDirectory = "swaggerui-" + UUID.randomUUID()
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/${swaggerRelativeDirectory}"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'maven-publish'
                id 'com.benjaminsproule.swagger'
            }
            group = 'com.benjaminsproule.swagger'
            version = '0.0.1'
            publishing {
                publications {
                    maven(MavenPublication) {
                        artifact source: "${expectedSwaggerDirectory}/groovySwagger.json", classifier: 'groovy'
                        artifact source: "${expectedSwaggerDirectory}/javaSwagger.json", classifier: 'java'
                    }
                }
                repositories {
                    maven {
                        url "${localRepo}"
                    }
                }
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    swaggerFileName = 'groovySwagger'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.java']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    swaggerFileName = 'javaSwagger'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def runResult = runPluginTask()
        new File("${localRepo}").mkdirs()
        def publishResult = pluginTaskRunnerBuilder()
            .withArguments('publishMavenPublicationToMavenRepository')
            .build()

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        publishResult.task(":publishMavenPublicationToMavenRepository").outcome == SUCCESS

        def groovySwaggerFile = new File("${localRepo}/com/benjaminsproule/swagger/${buildFile.getParentFile().getName()}/0.0.1/${buildFile.getParentFile().getName()}-0.0.1-groovy.json")
        assert groovySwaggerFile.exists()
        def javaSwaggerFile = new File("${localRepo}/com/benjaminsproule/swagger/${buildFile.getParentFile().getName()}/0.0.1/${buildFile.getParentFile().getName()}-0.0.1-java.json")
        assert javaSwaggerFile.exists()
    }

    def 'Skips task if nothing has changed'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/swaggerui"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def firstRunResult = runPluginTask()
        def secondRunResult = pluginTaskRunnerBuilder().withArguments(GenerateSwaggerDocsTask.TASK_NAME).build()

        then:
        firstRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        secondRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == UP_TO_DATE
    }

    def 'Runs task if output directory changed'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/swaggerui"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def firstRunResult = runPluginTask()
        Files.delete(Paths.get("${expectedSwaggerDirectory}/swagger.json"))
        def secondRunResult = pluginTaskRunnerBuilder().withArguments(GenerateSwaggerDocsTask.TASK_NAME).build()

        then:
        firstRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        secondRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
    }

    def 'Runs task if input directory changed'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/swaggerui"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def firstRunResult = runPluginTask()
        def newClassFile = new File('build/classes/groovy/test/com/benjaminsproule/swagger/gradleplugin/NewGradleSwaggerPluginITest.class')
        new File('build/classes/groovy/test/com/benjaminsproule/swagger/gradleplugin/GradleSwaggerPluginITest.class').readLines().each {
            newClassFile.write(it.replace('GradleSwaggerPluginITest', 'NewGradleSwaggerPluginITest'))
        }
        def secondRunResult = pluginTaskRunnerBuilder().withArguments(GenerateSwaggerDocsTask.TASK_NAME).build()

        then:
        firstRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        secondRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        cleanup:
        newClassFile.delete()
    }

    def 'Runs task if file in input directory changed'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/swaggerui"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }
            swagger {
                apiSource {
                    attachSwaggerArtifact = true
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        """

        when:
        def newClassFile = new File('build/classes/groovy/test/com/benjaminsproule/swagger/gradleplugin/NewGradleSwaggerPluginITest.class')
        new File('build/classes/groovy/test/com/benjaminsproule/swagger/gradleplugin/GradleSwaggerPluginITest.class').readLines().each {
            newClassFile.write(it.replace('GradleSwaggerPluginITest', 'NewGradleSwaggerPluginITest'))
        }
        def firstRunResult = pluginTaskRunnerBuilder().withArguments(GenerateSwaggerDocsTask.TASK_NAME).build()
        newClassFile.append("\n")
        def secondRunResult = pluginTaskRunnerBuilder().withArguments(GenerateSwaggerDocsTask.TASK_NAME).build()

        then:
        firstRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
        secondRunResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS

        cleanup:
        newClassFile.delete()
    }

    def 'Can apply plugin before declaring dependencies'() {
        given:
        def expectedSwaggerDirectory = "${testProjectOutputDirAsString}/swaggerui"
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            repositories {
                mavenLocal()
                mavenCentral()
            }

            dependencies {
                compile 'io.swagger:swagger-jersey2-jaxrs:+'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${expectedSwaggerDirectory}'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def runResult = runPluginTask()

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == SUCCESS
    }
}

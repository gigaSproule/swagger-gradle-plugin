package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.extension.SwaggerExtension
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import java.nio.file.Files

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class GradleSwaggerPluginTest {
    private Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.benjaminsproule.swagger'
    }

    @Test
    void pluginAddsGenerateSwaggerTask() {
        assertTrue(project.tasks.generateSwaggerDocumentation instanceof GenerateSwaggerDocsTask)
    }

    @Test
    void producesSwaggerDocumentation() {
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin

        def expectedSwaggerDirectory = "${project.buildDir}/swaggerui"
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
        assertTrue(Files.exists(swaggerFile.toPath()))

        JsonSlurper jsonSlurper = new JsonSlurper()

        def producedSwaggerDocument = jsonSlurper.parse(swaggerFile)
        assertEquals('2.0', producedSwaggerDocument.get('swagger'))
        assertEquals('localhost:8080', producedSwaggerDocument.get('host'))
        assertEquals('/', producedSwaggerDocument.get('basePath'))

        def info = producedSwaggerDocument.get('info')
        assertEquals('1', info.get('version'))
        assertEquals('test', info.get('title'))

        def tags = producedSwaggerDocument.get('tags')
        assertEquals(1, tags.size())
        assertEquals('/', tags.get(0).get('name'))
    }
}

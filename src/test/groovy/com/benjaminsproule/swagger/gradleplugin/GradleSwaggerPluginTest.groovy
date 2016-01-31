package com.benjaminsproule.swagger.gradleplugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * GradleSwaggerPluginTest
 */
class GradleSwaggerPluginTest {
    void setUp() {

    }

    @Test
    void pluginAddsGenerateSwaggerTask() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'swagger'
        assertTrue(project.tasks.swagger instanceof GenerateSwaggerDocsTask)
    }
}

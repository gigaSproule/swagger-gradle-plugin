package com.benjaminsproule.swagger.gradleplugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Ignore

import static org.junit.Assert.assertTrue

class GradleSwaggerPluginTest {
    @Ignore
    void pluginAddsGenerateSwaggerTask() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'swagger'
        assertTrue(project.tasks.swagger instanceof GenerateSwaggerDocsTask)
    }
}

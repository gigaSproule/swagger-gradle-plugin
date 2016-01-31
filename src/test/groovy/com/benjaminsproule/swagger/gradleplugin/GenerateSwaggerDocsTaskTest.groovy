package com.benjaminsproule.swagger.gradleplugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * GradleSwaggerTaskTest
 */
class GenerateSwaggerDocsTaskTest {
    @Test
    void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task(GenerateSwaggerDocsTask.TASK_NAME, type: GenerateSwaggerDocsTask)
        assertTrue(task instanceof GenerateSwaggerDocsTask)
    }
}

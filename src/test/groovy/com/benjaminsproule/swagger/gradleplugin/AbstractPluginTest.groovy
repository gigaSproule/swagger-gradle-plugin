package com.benjaminsproule.swagger.gradleplugin

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before

abstract class AbstractPluginTest {
    Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.benjaminsproule.swagger'
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin
    }
}

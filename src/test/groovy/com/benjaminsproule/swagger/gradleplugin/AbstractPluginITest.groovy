package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.utils.ModelModifierRemover
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before

abstract class AbstractPluginITest {
    Project project

    @Before
    void setUp() {
        project = ProjectBuilder.builder().build()
        project.configurations.create('runtime')
        project.plugins.apply JavaPlugin
        project.pluginManager.apply 'com.benjaminsproule.swagger'
        ModelModifierRemover.removeAllModelModifiers()
    }
}

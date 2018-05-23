package com.benjaminsproule.swagger.gradleplugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

abstract class AbstractPluginITest extends Specification {
    File testProjectDir
    File buildFile
    File testProjectOutputDir

    def setup() {
        testProjectDir = File.createTempDir()
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile.createNewFile()
        testProjectOutputDir = new File(testProjectDir, 'build/swagger')
    }

    BuildResult runPluginTask() {
        pluginTaskRunnerBuilder()
            .build()
    }

    GradleRunner pluginTaskRunnerBuilder() {
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments('clean', GenerateSwaggerDocsTask.TASK_NAME)
            .withPluginClasspath()
            .withTestKitDir(File.createTempDir())
            .withDebug(true)
    }
}

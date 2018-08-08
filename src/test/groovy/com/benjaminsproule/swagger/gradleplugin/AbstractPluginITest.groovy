package com.benjaminsproule.swagger.gradleplugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

abstract class AbstractPluginITest extends Specification {
    File testProjectDir
    File buildFile
    File testProjectOutputDir

    /**
     * Required as when run on Windows, it only includes a single '\' in the generated build.gradle,
     * therefore trying to escape the next character
     */
    String testProjectOutputDirAsString

    def setup() {
        testProjectDir = File.createTempDir()
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile.createNewFile()
        testProjectOutputDir = new File(testProjectDir, 'build/swagger')
        testProjectOutputDirAsString = "${testProjectOutputDir}".replace('\\', '/')
    }

    BuildResult runPluginTask() {
        pluginTaskRunnerBuilder()
            .build()
    }

    GradleRunner pluginTaskRunnerBuilder() {
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments('clean', GenerateSwaggerDocsTask.TASK_NAME, '--stacktrace')
            .withPluginClasspath()
            .withTestKitDir(File.createTempDir())
            .withGradleVersion(System.getProperty('test.gradleVersion', '4.7'))
            .withDebug(true)
    }
}

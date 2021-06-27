package com.benjaminsproule.swagger.gradleplugin

import org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

abstract class AbstractPluginITest extends Specification {
    File testProjectDir
    File buildFile
    File testProjectOutputDir
    List<File> pluginClasspath

    /**
     * Required as when run on Windows, it only includes a single '\' in the generated build.gradle,
     * therefore trying to escape the next character
     */
    String testProjectOutputDirAsString

    def setup() {
        testProjectDir = File.createTempDir()
        println(testProjectDir)
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile.createNewFile()
        testProjectOutputDir = new File(testProjectDir, 'build/swagger')
        testProjectOutputDirAsString = "${testProjectOutputDir}".replace('\\', '/')

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }
    }

    def cleanup() {
        FileUtils.deleteDirectory(testProjectDir);
    }

    BuildResult runPluginTask(boolean shouldSucceed = true) {
        def gradleRunner = pluginTaskRunnerBuilder()
        if (shouldSucceed) {
            return gradleRunner.build()
        } else {
            return gradleRunner.buildAndFail()
        }
    }

    GradleRunner pluginTaskRunnerBuilder() {
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments('clean', GenerateSwaggerDocsTask.TASK_NAME, '--stacktrace')
            .withPluginClasspath(pluginClasspath)
            .withTestKitDir(File.createTempDir())
            .withGradleVersion(System.getProperty('test.gradleVersion', '7.1'))
            .withDebug(true)
            .forwardOutput()
    }
}

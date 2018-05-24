package com.benjaminsproule.swagger.gradleplugin.classpath

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ClassFinderTest extends Specification {

    def 'ClassFinder scans both compile and runtime dependencies'() {
        setup:
        def compileClasspath = Mock(Configuration)
        def runtimeClasspath = Mock(Configuration)
        compileClasspath.resolve() >> [new File('externaltestdata/compile')]
        runtimeClasspath.resolve() >> [new File('externaltestdata/runtime')]

        def configurations = Mock(ConfigurationContainer)
        configurations.metaClass.compileClasspath = compileClasspath
        configurations.metaClass.runtimeClasspath = runtimeClasspath

        def project = ProjectBuilder.builder().build()
        project.plugins.apply JavaPlugin
        project.metaClass.configurations = configurations

        when:
        def classFinder = ClassFinder.getInstance(project)
        def compileClass = classFinder.loadClass('TestCompileClass')
        def runtimeClass = classFinder.loadClass('TestRuntimeClass')

        then:
        assert compileClass != null
        assert runtimeClass != null
    }
}

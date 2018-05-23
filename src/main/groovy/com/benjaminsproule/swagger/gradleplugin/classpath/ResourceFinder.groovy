package com.benjaminsproule.swagger.gradleplugin.classpath

import org.gradle.api.Project

class ResourceFinder {
    static instance
    Project project
    private ClassFinder classFinder

    private ResourceFinder(Project project, ClassFinder classFinder) {
        this.project = project
        this.classFinder = classFinder
    }

    //FIXME hack until we have some DI working
    static ResourceFinder getInstance(Project project) {
        if (!instance) {
            instance = new ResourceFinder(project, ClassFinder.getInstance(project))
        }
        instance
    }

    InputStream getResourceAsStream(String resourceName) {
        return new URLClassLoader(
            [project.sourceSets.main.output.resourcesDir.toURI().toURL()] as URL[],
            classFinder.classLoader
        ).getResourceAsStream(resourceName)
    }
}

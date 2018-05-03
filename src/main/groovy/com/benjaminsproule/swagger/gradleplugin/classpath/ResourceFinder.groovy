package com.benjaminsproule.swagger.gradleplugin.classpath

import org.gradle.api.Project

class ResourceFinder {
    static instance
    Project project

    private ResourceFinder(Project project) {
        this.project = project
    }

    //FIXME hack until we have some DI working
    static void createInstance(Project project) {
        instance = new ResourceFinder(project)
    }

    static ResourceFinder instance() {
        return instance
    }

    InputStream getResourceAsStream(String resourceName) {
        return new URLClassLoader(
            [project.sourceSets.main.output.resourcesDir.toURI().toURL()] as URL[],
            ClassFinder.instance().classLoader
        ).getResourceAsStream(resourceName)
    }
}

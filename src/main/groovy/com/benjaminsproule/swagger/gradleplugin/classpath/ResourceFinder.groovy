package com.benjaminsproule.swagger.gradleplugin.classpath

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ResourceFinder {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceFinder)
    static instance
    ClassLoader classLoader
    Project project

    private ResourceFinder(Project project) {
        this.project = project
        this.classCache = new HashMap<>()
    }

    //FIXME hack until we have some DI working
    static void createInstance(Project project) {
        instance = new ResourceFinder(project)
    }

    static ResourceFinder instance() {
        return instance
    }

    // TODO: Write tests
    InputStream getResourceAsStream(String resourceName) {
        return new URLClassLoader(
            [project.sourceSets.main.output.resourcesDir.toURI().toURL()] as URL[],
            getClass().getClassLoader()
        ).getResourceAsStream(resourceName)
    }
}

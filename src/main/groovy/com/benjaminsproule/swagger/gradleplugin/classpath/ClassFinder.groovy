package com.benjaminsproule.swagger.gradleplugin.classpath

import org.gradle.api.Project
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.annotation.Annotation

class ClassFinder {
    private static final Logger LOG = LoggerFactory.getLogger(ClassFinder)
    private Map<Class<? extends Annotation>, Set<Class<?>>> classCache
    private Project project
    private ClassLoader classLoader

    ClassFinder(Project project) {
        this.project = project
        this.classCache = [:]
    }

    Class<?> loadClass(String name) {
        return getClassLoader().loadClass(name)
    }

    void clearClassCache() {
        classCache.clear()
    }

    Set<Class<?>> getValidClasses(Class<? extends Annotation> clazz, List<String> packages) {
        if (classCache.containsKey(clazz)) {
            return classCache.get(clazz)
        }

        Set<Class<?>> classes = new HashSet<Class<?>>()

        if (packages) {
            packages.each { location ->
                Set<Class<?>> c = new Reflections(getClassLoader(), location).getTypesAnnotatedWith(clazz)
                classes.addAll(c)
            }
        } else {
            LOG.warn("Scanning the the entire classpath (${clazz}), you should avoid this by specifying package locations")
            Set<Class<?>> c = new Reflections(getClassLoader(), '').getTypesAnnotatedWith(clazz)
            classes.addAll(c)
        }

        classCache.put(clazz, classes)
        return classes
    }

    ClassLoader getClassLoader() {
        if (classLoader) {
            return classLoader
        }

        def urls = []
        def classpaths = [project.configurations.compileClasspath.resolve()]
        if (project.configurations.hasProperty('runtimeClasspath')) {
            classpaths += project.configurations.runtimeClasspath.resolve()
        } else {
            classpaths += project.configurations.runtime.resolve()
        }
        classpaths.flatten().each {
            urls += it.toURI().toURL()
        }

        if (project.sourceSets.main.output.getProperties()['classesDirs']) {
            project.sourceSets.main.output.classesDirs.each {
                if (it.exists()) {
                    urls += it.toURI().toURL()
                }
            }
        } else {
            urls += project.sourceSets.main.output.classesDir.toURI().toURL()
        }

        urls += project.sourceSets.main.output.resourcesDir.toURI().toURL()

        return new URLClassLoader(urls as URL[])
    }
}

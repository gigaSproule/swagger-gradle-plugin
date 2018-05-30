package com.benjaminsproule.swagger.gradleplugin.classpath

import org.gradle.api.Project
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.annotation.Annotation

class ClassFinder {
    private static final Logger LOG = LoggerFactory.getLogger(ClassFinder)
    static ClassFinder instance
    private Map<Class<? extends Annotation>, Set<Class<?>>> classCache
    private Project project
    private ClassLoader classLoader

    ClassFinder(Project project) {
        this.project = project
        this.classCache = new HashMap<>()
    }

    //FIXME hack until we have some DI working
    static ClassFinder getInstance(Project project) {
        if (!instance) {
            instance = new ClassFinder(project)
        }
        instance
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
        (project.configurations.compileClasspath.resolve() + project.configurations.runtimeClasspath.resolve()).each {
            urls.add(it.toURI().toURL())
        }

        if (project.sourceSets.main.output.getProperties()['classesDirs']) {
            project.sourceSets.main.output.classesDirs.each {
                if (it.exists()) {
                    urls.add(it.toURI().toURL())
                }
            }
        } else {
            urls.add(project.sourceSets.main.output.classesDir.toURI().toURL())
        }

        urls.add(project.sourceSets.main.output.resourcesDir.toURI().toURL())

        return new URLClassLoader(urls as URL[], getClass().getClassLoader())
    }
}

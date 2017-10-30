package com.benjaminsproule.swagger.gradleplugin.classpath

import org.gradle.api.Project
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.annotation.Annotation

class ClassFinder {
    private static final Logger LOG = LoggerFactory.getLogger(ClassFinder)
    static instance
    Map<Class<? extends Annotation>, Set<Class<?>>> classCache
    Project project

    private ClassFinder(Project project) {
        this.project = project
        this.classCache = new HashMap<>()
    }

    //FIXME hack until we have some DI working
    static void createInstance(Project project) {
        instance = new ClassFinder(project)
    }

    static ClassFinder instance() {
        return instance
    }

    Set<Class<?>> getValidClasses(Class<? extends Annotation> clazz) {
        return getValidClasses(clazz, [], '')
    }

    Set<Class<?>> getValidClasses(Class<? extends Annotation> clazz, List<String> packages, String excludes) {
        if (classCache.containsKey(clazz)) {
            return classCache.get(clazz)
        }

        Set<Class<?>> classes = new HashSet<Class<?>>()
        ClassLoader classLoader = prepareClassLoader(excludes)

        if (packages) {
            packages.each { location ->
                Set<Class<?>> c = new Reflections(classLoader, location).getTypesAnnotatedWith(clazz)
                classes.addAll(c)
            }
        } else {
            LOG.warn("Scanning the the entire classpath (${clazz}), you should avoid this by specifying package locations")
            Set<Class<?>> c = new Reflections(classLoader, '').getTypesAnnotatedWith(clazz)
            classes.addAll(c)
        }

        classCache.put(clazz, classes)
        return classes
    }

    private ClassLoader prepareClassLoader(String excludes) {
        def urls = new ArrayList<>()
        project.configurations.runtime.resolve().each {
            if (!it.name.matches(excludes)) {
                urls.add(it.toURI().toURL())
            }
        }

        if (project.sourceSets.main.output.classesDirs) {
            project.sourceSets.main.output.classesDirs.each {
                urls.add(it.toURI().toURL())
            }
        } else {
            project.sourceSets.main.output.classesDir.each {
                urls.add(it.toURI().toURL())
            }
        }

        return new URLClassLoader(urls as URL[], getClass().getClassLoader())
    }
}

package com.benjaminsproule.swagger.gradleplugin.generator

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.github.jknack.handlebars.io.ClassPathTemplateLoader

class ClassPathTemplateLoaderImpl extends ClassPathTemplateLoader {

    private ClassFinder classFinder

    ClassPathTemplateLoaderImpl(String prefix, String suffix, ClassFinder classFinder) {
        super(prefix, suffix)
        this.classFinder = classFinder
    }

    @Override
    protected URL getResource(final String location) {
        return classFinder.getClassLoader().getResource(location)
    }
}

package com.benjaminsproule.swagger.gradleplugin.generator

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension

class GeneratorFactory {

    private ClassFinder classFinder

    GeneratorFactory(ClassFinder classFinder) {
        this.classFinder = classFinder
    }

    Generator generator(ApiSourceExtension config) {
        if (config.templatePath) {
            return new ApiDocGenerator(config, classFinder)
        } else {
            return new SwaggerSpecGenerator(config)
        }
    }
}

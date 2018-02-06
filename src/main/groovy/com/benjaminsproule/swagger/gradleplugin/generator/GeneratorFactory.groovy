package com.benjaminsproule.swagger.gradleplugin.generator

import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension

class GeneratorFactory {

    static Generator generator(ApiSourceExtension config) {
        if (config.templatePath) {
            return new ApiDocGenerator(apiSource: config)
        } else {
            return new SwaggerSpecGenerator(config)
        }
    }
}

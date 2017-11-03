package com.benjaminsproule.swagger.gradleplugin.docgen

import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension

class LoaderFactory {

    def static loader(ApiSourceExtension apiSourceExtension) {
        configureGeneratorEnvironment(apiSourceExtension)
        if (apiSourceExtension.springmvc) {
            return new SpringSwaggerLoader(apiSourceExtension)
        } else {
            return new DefaultSwaggerLoader(apiSourceExtension)
        }
    }

    private static void configureGeneratorEnvironment(ApiSourceExtension apiSourceExtension) {
        new EnvironmentConfigurer(apiSourceExtension)
            .configureModelModifiers()
            .configureModelConverters()
            .configureSwaggerFilter()
            .initOutputDirectory()
    }
}

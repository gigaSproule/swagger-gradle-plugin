package com.benjaminsproule.swagger.gradleplugin.misc

import com.fasterxml.jackson.databind.Module.SetupContext
import com.fasterxml.jackson.databind.module.SimpleModule

/**
 * https://github.com/swagger-api/swagger-core/issues/2104
 */
class EnhancedSwaggerModule extends SimpleModule {

    EnhancedSwaggerModule() {
        super("1.0.0")
    }

    @Override
    void setupModule(SetupContext context) {
        super.setupModule(context)
        context.insertAnnotationIntrospector(new EnhancedSwaggerAnnotationIntrospector())
    }
}

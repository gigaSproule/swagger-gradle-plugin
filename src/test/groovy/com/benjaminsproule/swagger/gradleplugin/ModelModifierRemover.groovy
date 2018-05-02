package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.reader.resolver.ModelModifier
import io.swagger.converter.ModelConverters

class ModelModifierRemover {

    static removeAllModelModifiers() {
        def modelConverters = ModelConverters.getInstance()
        modelConverters.converters.each {
            if (it.getClass() == ModelModifier.class) {
                modelConverters.removeConverter(it)
            }
        }
    }

}

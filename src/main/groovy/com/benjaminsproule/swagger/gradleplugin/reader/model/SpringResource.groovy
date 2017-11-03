package com.benjaminsproule.swagger.gradleplugin.reader.model

import com.benjaminsproule.swagger.gradleplugin.Utils
import org.apache.commons.lang3.StringUtils

import java.lang.reflect.Method

class SpringResource {
    Class<?> controllerClass
    List<Method> methods
    String controllerMapping //FIXME should be an array
    String description

    /**
     * @param clazz (Class<?>) Controller class
     */
    SpringResource(Class<?> clazz, String description) {
        this.controllerClass = clazz
        this.description = description
        methods = new ArrayList<>()

        String[] controllerRequestMappingValues = Utils.getControllerRequestMapping(controllerClass)

        this.controllerMapping = StringUtils.removeEnd(controllerRequestMappingValues[0], "/")
    }

    void addMethod(Method m) {
        this.methods.add(m)
    }
}

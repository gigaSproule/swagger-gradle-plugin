package com.benjaminsproule.swagger.gradleplugin.reader.model

import com.benjaminsproule.swagger.gradleplugin.Utils
import org.apache.commons.lang3.StringUtils

import java.lang.reflect.Method

class SpringResource {
    Class<?> controllerClass
    List<Method> methods
    List<String> controllerMapping
    String description

    /**
     * @param clazz (Class<?>) Controller class
     */
    SpringResource(Class<?> clazz, String description) {
        this.controllerClass = clazz
        this.description = description
        methods = new ArrayList<>()

        String[] controllerRequestMappingValues = Utils.getControllerRequestMapping(controllerClass)

        def formattedControllerRequestMappings = controllerRequestMappingValues.each {
            StringUtils.remove(it, '/')
        }
        this.controllerMapping = formattedControllerRequestMappings
    }

    void addMethod(Method m) {
        this.methods.add(m)
    }
}

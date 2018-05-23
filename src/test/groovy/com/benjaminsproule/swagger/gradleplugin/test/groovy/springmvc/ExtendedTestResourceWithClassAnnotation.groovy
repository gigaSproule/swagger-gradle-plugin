package com.benjaminsproule.swagger.gradleplugin.test.groovy.springmvc

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

class ExtendedTestResourceWithClassAnnotation extends TestResourceWithClassAnnotation {

    @ApiOperation('An overriden operation description')
    @RequestMapping(path = '/overriden', method = RequestMethod.GET)
    @Override
    String overriden() {
        return ''
    }

    @RequestMapping(path = '/overridenWithoutDescription', method = RequestMethod.GET)
    @Override
    String overridenWithoutDescription() {
        return ''
    }
}

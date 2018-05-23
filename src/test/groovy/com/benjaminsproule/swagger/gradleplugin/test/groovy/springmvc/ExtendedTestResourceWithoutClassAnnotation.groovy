package com.benjaminsproule.swagger.gradleplugin.test.groovy.springmvc

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

class ExtendedTestResourceWithoutClassAnnotation extends TestResourceWithoutClassAnnotation {

    @ApiOperation('An overriden operation description')
    @RequestMapping(path = '/root/withoutannotation/overriden', method = RequestMethod.GET)
    @Override
    String overriden() {
        return ''
    }

    @RequestMapping(path = '/root/withoutannotation/overridenWithoutDescription', method = RequestMethod.GET)
    @Override
    String overridenWithoutDescription() {
        return ''
    }
}

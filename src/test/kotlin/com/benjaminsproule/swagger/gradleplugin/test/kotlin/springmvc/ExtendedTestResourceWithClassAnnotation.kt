package com.benjaminsproule.swagger.gradleplugin.test.kotlin.springmvc;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

class ExtendedTestResourceWithClassAnnotation : TestResourceWithClassAnnotation() {

    @ApiOperation("An overriden operation description")
    @RequestMapping(path = ["/overriden"], method = [(RequestMethod.GET)])
    override fun overriden(): String {
        return ""
    }

    @RequestMapping(path = ["/overridenWithoutDescription"], method = [(RequestMethod.GET)])
    override fun overridenWithoutDescription(): String {
        return ""
    }
}

package com.benjaminsproule.swagger.gradleplugin.test.kotlin.springmvc;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

class ExtendedTestResourceWithoutClassAnnotation : TestResourceWithoutClassAnnotation() {

    @ApiOperation("An overriden operation description")
    @RequestMapping(path = ["/root/withoutannotation/overriden"], method = [RequestMethod.GET])
    override fun overriden(): String {
        return ""
    }

    @RequestMapping(path = ["/root/withoutannotation/overridenWithoutDescription"], method = [RequestMethod.GET])
    override fun overridenWithoutDescription(): String {
        return ""
    }
}

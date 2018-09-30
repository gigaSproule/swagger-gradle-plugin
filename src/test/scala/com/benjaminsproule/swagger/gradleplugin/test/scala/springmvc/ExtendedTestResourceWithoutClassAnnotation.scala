package com.benjaminsproule.swagger.gradleplugin.test.scala.springmvc

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod}

class ExtendedTestResourceWithoutClassAnnotation extends TestResourceWithoutClassAnnotation {

    @ApiOperation("An overriden operation description")
    @RequestMapping(path = Array("/root/withoutannotation/overriden"), method = Array(RequestMethod.GET))
    @Override
    override def overriden(): String = {
        ""
    }

    @RequestMapping(path = Array("/root/withoutannotation/overridenWithoutDescription"), method = Array(RequestMethod.GET))
    @Override
    override def overridenWithoutDescription(): String = {
        ""
    }
}

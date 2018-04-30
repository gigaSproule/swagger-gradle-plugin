package com.benjaminsproule.swagger.gradleplugin.test.springmvc;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class ExtendedTestResourceWithoutClassAnnotation extends TestResourceWithoutClassAnnotation {

    @ApiOperation("An overriden operation description")
    @RequestMapping(path = "/root/withoutannotation/overriden", method = RequestMethod.GET)
    @Override
    public String overriden() {
        return "";
    }

    @RequestMapping(path = "/root/withoutannotation/overridenWithoutDescription", method = RequestMethod.GET)
    @Override
    public String overridenWithoutDescription() {
        return "";
    }
}

package com.benjaminsproule.swagger.gradleplugin.test.java.springmvc;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class ExtendedTestResourceWithClassAnnotation extends TestResourceWithClassAnnotation {

    @ApiOperation("An overriden operation description")
    @RequestMapping(path = "/overriden", method = RequestMethod.GET)
    @Override
    public String overriden() {
        return "";
    }

    @RequestMapping(path = "/overridenWithoutDescription", method = RequestMethod.GET)
    @Override
    public String overridenWithoutDescription() {
        return "";
    }
}

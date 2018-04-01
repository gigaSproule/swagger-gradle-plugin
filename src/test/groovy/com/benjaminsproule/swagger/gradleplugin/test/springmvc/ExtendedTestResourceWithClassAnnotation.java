package com.benjaminsproule.swagger.gradleplugin.test.springmvc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

public class ExtendedTestResourceWithClassAnnotation extends TestResourceWithClassAnnotation {

    @ApiOperation("An overriden operation description")
    @RequestMapping(path = "/overriden", method = RequestMethod.GET)
    @Override
    public String overriden() {
        return "";
    }
}

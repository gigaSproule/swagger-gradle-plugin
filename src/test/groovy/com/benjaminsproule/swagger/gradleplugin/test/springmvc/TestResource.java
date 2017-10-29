package com.benjaminsproule.swagger.gradleplugin.test.springmvc;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(tags = "/", description = "Test resource", authorizations = {@Authorization("basic")})
@RequestMapping(value = "/root", produces = {"application/json"})
public class TestResource {
    @ApiOperation("A basic operation")
    @RequestMapping(path = "/basic", method = RequestMethod.GET)
    public String basic() {
        return "";
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @RequestMapping(path = "/hidden", method = RequestMethod.GET)
    public String hidden() {
        return "";
    }
}

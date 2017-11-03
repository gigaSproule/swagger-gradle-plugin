package com.benjaminsproule.swagger.gradleplugin.test.springmvc;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
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

    @ApiOperation(value = "A generic operation", hidden = true)
    @RequestMapping(path = "/generic", method = RequestMethod.GET)
    public List<String> generic() {
        return Lists.newArrayList("");
    }
}

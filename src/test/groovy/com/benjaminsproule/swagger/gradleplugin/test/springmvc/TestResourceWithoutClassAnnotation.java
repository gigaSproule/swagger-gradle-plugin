package com.benjaminsproule.swagger.gradleplugin.test.springmvc;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
public class TestResourceWithoutClassAnnotation {
    @ApiOperation("A basic operation")
    @RequestMapping(path = "/root/withoutannotation/basic", method = RequestMethod.GET, produces = {"application/json"})
    public String basic() {
        return "";
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @RequestMapping(path = "/root/withoutannotation/hidden", method = RequestMethod.GET, produces = {"application/json"})
    public String hidden() {
        return "";
    }

    @ApiOperation(value = "A generic operation")
    @RequestMapping(path = "/root/withoutannotation/generics", method = RequestMethod.POST, produces = {"application/json"})
    public List<String> generic() {
        return Lists.newArrayList("");
    }
}

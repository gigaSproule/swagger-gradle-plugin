package com.benjaminsproule.swagger.gradleplugin.test.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.swagger.annotations.ApiOperation;

public class ExtendedTestResourceWithClassAnnotation extends TestResourceWithClassAnnotation {

    @ApiOperation("An overriden operation description")
    @Path("/overriden")
    @GET
    @Override
    public String overriden() {
        return "";
    }
}

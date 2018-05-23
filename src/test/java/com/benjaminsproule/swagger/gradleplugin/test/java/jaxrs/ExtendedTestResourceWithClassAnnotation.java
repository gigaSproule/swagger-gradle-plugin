package com.benjaminsproule.swagger.gradleplugin.test.java.jaxrs;

import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class ExtendedTestResourceWithClassAnnotation extends TestResourceWithClassAnnotation {

    @ApiOperation("An overriden operation description")
    @Path("/overriden")
    @GET
    @Override
    public String overriden() {
        return "";
    }

    @Path("/overridenWithoutDescription")
    @GET
    public String overridenWithoutDescription() {
        return "";
    }
}

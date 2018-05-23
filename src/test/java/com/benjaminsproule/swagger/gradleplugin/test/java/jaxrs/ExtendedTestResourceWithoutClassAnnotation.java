package com.benjaminsproule.swagger.gradleplugin.test.java.jaxrs;

import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class ExtendedTestResourceWithoutClassAnnotation extends TestResourceWithoutClassAnnotation {

    @ApiOperation("An overriden operation description")
    @Path("/root/withoutannotation/overriden")
    @GET
    @Override
    public String overriden() {
        return "";
    }

    @Path("/root/withoutannotation/overridenWithoutDescription")
    @GET
    public String overridenWithoutDescription() {
        return "";
    }
}

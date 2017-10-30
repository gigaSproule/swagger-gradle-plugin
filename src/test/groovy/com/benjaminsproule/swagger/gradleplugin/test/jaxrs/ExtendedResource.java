package com.benjaminsproule.swagger.gradleplugin.test.jaxrs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class ExtendedResource extends TestResource {

    @ApiOperation("A extended operation")
    @Path("/extended")
    @GET
    @Override
    public Response extended(@ApiParam SubclassModel subclass) {
        return Response.ok().build();
    }
}

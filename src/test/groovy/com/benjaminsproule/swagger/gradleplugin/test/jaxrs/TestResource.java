package com.benjaminsproule.swagger.gradleplugin.test.jaxrs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Api(tags = "/", description = "Test resource", authorizations = {@Authorization("basic")})
@Path("/root")
public class TestResource {

    @ApiOperation("A basic operation")
    @Path("/method")
    public Response basic() {
        return Response.ok().build();
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @Path("/hidden")
    public Response hidden() {
        return Response.ok().build();
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/datatype")
    @Consumes("application/json")
    @Produces("application/json")
    public Response dataType() {
        return Response.ok().build();
    }
}

package com.benjaminsproule.swagger.gradleplugin.test.jaxrs;

import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
@Path("/root/withannotation")
public class TestResourceWithAnnotation {

    @ApiOperation("A basic operation")
    @Path("/method")
    @GET
    public Response basic() {
        return Response.ok().build();
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @Path("/hidden")
    @GET
    public Response hidden() {
        return Response.ok().build();
    }

    @ApiOperation("A deprecated operation")
    @Path("/deprecated")
    @GET
    @Deprecated
    public Response deprecated() {
        return Response.ok().build();
    }

    @ApiOperation(value = "A basic operation", authorizations = {
        @Authorization(value = "oauth2", scopes = {
            @AuthorizationScope(scope = "scope", description = "scope description")
        })
    })
    @Path("/method")
    @GET
    public Response withAuth() {
        return Response.ok().build();
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/datatype")
    @Consumes("application/json")
    @Produces("application/json")
    @POST
    public Response dataType() {
        return Response.ok().build();
    }

    @ApiOperation(value = "Consumes and Produces operation", response = ResponseModel.class)
    @Path("/model")
    @POST
    public Response definitions(@ApiParam RequestModel body) {
        return Response.ok().build();
    }

    @ApiOperation(value = "Consumes and Produces operation", response = ResponseModel.class, responseContainer = "List")
    @Path("/generics")
    @POST
    public Response generics(@ApiParam List<RequestModel> body) {
        return Response.ok().build();
    }


    @ApiOperation("A extended operation")
    @Path("/basic")
    @GET
    public Response extended(@ApiParam SubclassModel subclass) {
        return Response.ok().build();
    }
}

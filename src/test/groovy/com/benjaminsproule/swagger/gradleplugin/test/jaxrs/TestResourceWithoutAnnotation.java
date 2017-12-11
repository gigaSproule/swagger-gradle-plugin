package com.benjaminsproule.swagger.gradleplugin.test.jaxrs;

import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
public class TestResourceWithoutAnnotation {

    @ApiOperation("A basic operation")
    @Path("/root/withoutannotation/method")
    @GET
    public Response basic() {
        return Response.ok().build();
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @Path("/root/withoutannotation/hidden")
    @GET
    public Response hidden() {
        return Response.ok().build();
    }

    @ApiOperation("A deprecated operation")
    @Path("/root/withoutannotation/deprecated")
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
    @Path("/root/withoutannotation/method")
    @GET
    public Response withAuth() {
        return Response.ok().build();
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/root/withoutannotation/datatype")
    @Consumes("application/json")
    @Produces("application/json")
    @POST
    public Response dataType() {
        return Response.ok().build();
    }

    @ApiOperation(value = "Consumes and Produces operation", response = ResponseModel.class)
    @Path("/root/withoutannotation/model")
    @POST
    public Response definitions(@ApiParam RequestModel body) {
        return Response.ok().build();
    }

    @ApiOperation(value = "Consumes and Produces operation", response = ResponseModel.class, responseContainer = "List")
    @Path("/root/withoutannotation/generics")
    @POST
    public Response generics(@ApiParam List<RequestModel> body) {
        return Response.ok().build();
    }


    @ApiOperation("A extended operation")
    @Path("/root/withoutannotation/basic")
    @GET
    public Response extended(@ApiParam SubclassModel subclass) {
        return Response.ok().build();
    }
}

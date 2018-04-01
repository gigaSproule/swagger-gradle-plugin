package com.benjaminsproule.swagger.gradleplugin.test.jaxrs;

import static java.util.Collections.singletonList;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel;
import com.benjaminsproule.swagger.gradleplugin.test.model.ResponseModel;
import com.benjaminsproule.swagger.gradleplugin.test.model.SubResponseModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
@Path("/root/withannotation")
public class TestResourceWithClassAnnotation {

    @ApiOperation("A basic operation")
    @Path("/basic")
    @GET
    public String basic() {
        return "";
    }

    @ApiOperation(value = "A default operation")
    @Path("/default")
    @GET
    public Response defaultResponse() {
        return Response.ok().build();
    }

    @ApiOperation(value = "A generics operation")
    @Path("/generics")
    @POST
    public List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList("");
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/datatype")
    @Consumes("application/json")
    @Produces("application/json")
    @POST
    public Response dataType(@ApiParam RequestModel requestModel) {
        return Response.ok().build();
    }

    @ApiOperation(value = "A response operation", response = ResponseModel.class)
    @Path("/response")
    @POST
    public ResponseModel response(@ApiParam List<RequestModel> body) {
        return new ResponseModel();
    }

    @ApiOperation(value = "A response container operation", response = ResponseModel.class, responseContainer = "List")
    @Path("/responseContainer")
    @POST
    public List<ResponseModel> responseContainer(@ApiParam List<RequestModel> body) {
        return singletonList(new ResponseModel());
    }

    @ApiOperation("An extended operation")
    @Path("/extended")
    @GET
    public SubResponseModel extended() {
        return new SubResponseModel();
    }

    @ApiOperation("A deprecated operation")
    @Path("/deprecated")
    @GET
    @Deprecated
    public String deprecated() {
        return "";
    }

    @ApiOperation(value = "An auth operation", authorizations = {
        @Authorization(value = "oauth2", scopes = {
            @AuthorizationScope(scope = "scope", description = "scope description")
        })
    })
    @Path("/auth")
    @GET
    public String withAuth() {
        return "";
    }

    @ApiOperation(value = "A model operation")
    @Path("/model")
    @GET
    public String model() {
        return "";
    }

    @ApiOperation("An overriden operation")
    @Path("/overriden")
    @GET
    public String overriden() {
        return "";
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @Path("/hidden")
    @GET
    public String hidden() {
        return "";
    }
}

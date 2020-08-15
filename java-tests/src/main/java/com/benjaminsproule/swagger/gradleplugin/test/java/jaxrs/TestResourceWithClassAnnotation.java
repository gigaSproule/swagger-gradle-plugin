package com.benjaminsproule.swagger.gradleplugin.test.java.jaxrs;

import com.benjaminsproule.swagger.gradleplugin.test.model.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Collections.singletonList;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
@Path("/root/withannotation")
public class TestResourceWithClassAnnotation {

    @ApiOperation("A basic operation")
    @Path("/basic")
    @GET
    public String basic() {
        return "";
    }

    @ApiOperation("A default operation")
    @Path("/default")
    @GET
    public Response defaultResponse() {
        return Response.ok().build();
    }

    @ApiOperation("A generics operation")
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
    public ResponseModel response() {
        return new ResponseModel();
    }

    @ApiOperation(value = "A response container operation", response = ResponseModel.class, responseContainer = "List")
    @Path("/responseContainer")
    @POST
    public List<ResponseModel> responseContainer() {
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

    @ApiOperation("A model operation")
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

    @ApiOperation("An overriden operation")
    @Path("/overridenWithoutDescription")
    @GET
    public String overridenWithoutDescription() {
        return "";
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @Path("/hidden")
    @GET
    public String hidden() {
        return "";
    }

    @ApiOperation("A multiple parameters operation")
    @Path("/multipleParameters/{parameter1}")
    @GET
    public String multipleParameters(@PathParam("parameter1") Double parameterDouble, @QueryParam("parameter2") Boolean parameterBool) {
        return "";
    }

    String ignoredModel(IgnoredModel ignoredModel) {
        return "";
    }

    @ApiOperation("A PATCH operation")
    @Path("/patch")
    @PATCH
    public String patch() {
        return "";
    }

    @ApiOperation("An OPTIONS operation")
    @Path("/options")
    @OPTIONS
    public Response options() {
        return Response.ok().build();
    }

    @ApiOperation("An HEAD operation")
    @Path("/head")
    @HEAD
    public String head() {
        return "";
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        @ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header")
    })
    @Path("/implicitparams")
    @POST
    public String implicitParams(String requestModel) {
        return "";
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @Path("/createdrequest")
    @POST
    public String createdRequest() {
        return "";
    }

    @ApiResponses(
        value = {
            @ApiResponse(code = 201, message = "Success", response = String.class),
            @ApiResponse(code = 422, message = "Business errors", response = String.class)}
    )
    @Path("/apiresponses")
    @POST
    public String apiResponses() {
        return "";
    }

    @ApiOperation(value = "A inner JSON sub type operation")
    @Path("/innerjsonsubtype")
    @GET
    public OuterJsonSubType innerJsonSubType() {
        return new OuterJsonSubType();
    }

    @ApiOperation("With JsonViewOne specification")
    @Path("/root/withoutannotation/withjsonview1")
    @GET
    @JsonView(TestJsonViewOne.class)
    public TestJsonViewEntity withJsonViewOne() {
        return null;
    }

    @ApiOperation("With JsonViewTwo specification")
    @Path("/root/withoutannotation/withjsonview2")
    @GET
    @JsonView(TestJsonViewTwo.class)
    public TestJsonViewEntity withJsonViewTwo() {
        return null;
    }

    @ApiOperation("Entity definition has to contain all possible fields")
    @Path("/root/withoutannotation/withoutjsonview")
    @GET
    public TestJsonViewEntity withoutJsonView() {
        return null;
    }
}

package com.benjaminsproule.swagger.gradleplugin.test.java.jaxrs;

import com.benjaminsproule.swagger.gradleplugin.test.model.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Collections.singletonList;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
public class TestResourceWithoutClassAnnotation {

    @ApiOperation("A basic operation")
    @Path("/root/withoutannotation/basic")
    @GET
    public String basic() {
        return "";
    }

    @ApiOperation("A default operation")
    @Path("/root/withoutannotation/default")
    @GET
    public Response defaultResponse() {
        return Response.ok().build();
    }

    @ApiOperation("A generics operation")
    @Path("/root/withoutannotation/generics")
    @POST
    public List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList("");
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/root/withoutannotation/datatype")
    @Consumes("application/json")
    @Produces("application/json")
    @POST
    public Response dataType(@ApiParam RequestModel requestModel) {
        return Response.ok().build();
    }

    @ApiOperation(value = "A response operation", response = ResponseModel.class)
    @Path("/root/withoutannotation/response")
    @POST
    public ResponseModel response() {
        return new ResponseModel();
    }

    @ApiOperation(value = "A response container operation", response = ResponseModel.class, responseContainer = "List")
    @Path("/root/withoutannotation/responseContainer")
    @POST
    public List<ResponseModel> responseContainer() {
        return singletonList(new ResponseModel());
    }

    @ApiOperation("An extended operation")
    @Path("/root/withoutannotation/extended")
    @GET
    public SubResponseModel extended() {
        return new SubResponseModel();
    }

    @ApiOperation("A deprecated operation")
    @Path("/root/withoutannotation/deprecated")
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
    @Path("/root/withoutannotation/auth")
    @GET
    public String withAuth() {
        return "";
    }

    @ApiOperation("A model operation")
    @Path("/root/withoutannotation/model")
    @GET
    public String model() {
        return "";
    }

    @ApiOperation("An overriden operation")
    @Path("/root/withoutannotation/overriden")
    @GET
    public String overriden() {
        return "";
    }

    @ApiOperation("An overriden operation")
    @Path("/root/withoutannotaiton/overridenWithoutDescription")
    @GET
    public String overridenWithoutDescription() {
        return "";
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @Path("/root/withoutannotation/hidden")
    @GET
    public String hidden() {
        return "";
    }

    @ApiOperation("A multiple parameters operation")
    @Path("/root/withoutannotation/multipleParameters/{parameter1}")
    @GET
    public String multipleParameters(@PathParam("parameter1") Double parameterDouble, @QueryParam("parameter2") Boolean parameterBool) {
        return "";
    }

    String ignoredModel(IgnoredModel ignoredModel) {
        return "";
    }

    @ApiOperation("A PATCH operation")
    @Path("/root/withoutannotation/patch")
    @PATCH
    public String patch() {
        return "";
    }

    @ApiOperation("An OPTIONS operation")
    @Path("/root/withoutannotation/options")
    @OPTIONS
    public Response options() {
        return Response.ok().build();
    }

    @ApiOperation("An HEAD operation")
    @Path("/root/withoutannotation/head")
    @HEAD
    public String head() {
        return "";
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        @ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header")
    })
    @Path("/root/withoutannotation/implicitparams")
    @POST
    public String implicitParams(String requestModel) {
        return "";
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @Path("/root/withoutannotation/createdrequest")
    @POST
    public String createdRequest() {
        return "";
    }

    @ApiOperation(value = "A inner JSON sub type operation")
    @Path("/root/withoutannotation/innerjsonsubtype")
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

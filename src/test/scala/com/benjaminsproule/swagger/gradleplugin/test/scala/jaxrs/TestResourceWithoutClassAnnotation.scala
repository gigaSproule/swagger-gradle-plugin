package com.benjaminsproule.swagger.gradleplugin.test.scala.jaxrs

import com.benjaminsproule.swagger.gradleplugin.test.model.{IgnoredModel, RequestModel, ResponseModel, SubResponseModel}
import io.swagger.annotations._
import javax.ws.rs._
import javax.ws.rs.core.Response

@Api(tags = Array("Test"), description = "Test resource", authorizations = Array(
    new Authorization("basic")
))
class TestResourceWithoutClassAnnotation {

    @ApiOperation("A basic operation")
    @Path("/root/withoutannotation/basic")
    @GET
    def basic(): String = {
        ""
    }

    @ApiOperation("A default operation")
    @Path("/root/withoutannotation/default")
    @GET
    def defaultResponse(): Response = {
        Response.ok().build()
    }

    @ApiOperation("A generics operation")
    @Path("/root/withoutannotation/generics")
    @POST
    def generics(@ApiParam body: List[RequestModel]): List[String] = {
        List("")
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/root/withoutannotation/datatype")
    @Consumes(Array("application/json"))
    @Produces(Array("application/json"))
    @POST
    def dataType(@ApiParam requestModel: RequestModel): Response = {
        Response.ok().build()
    }

    @ApiOperation(value = "A response operation", response = classOf[ResponseModel])
    @Path("/root/withoutannotation/response")
    @POST
    def response(): ResponseModel = {
        new ResponseModel()
    }

    @ApiOperation(value = "A response container operation", response = classOf[ResponseModel], responseContainer = "List")
    @Path("/root/withoutannotation/responseContainer")
    @POST
    def responseContainer(): List[ResponseModel] = {
        List(new ResponseModel())
    }

    @ApiOperation("An extended operation")
    @Path("/root/withoutannotation/extended")
    @GET
    def extended(): SubResponseModel = {
        new SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @Path("/root/withoutannotation/deprecated")
    @GET
    @Deprecated
    def deprecated(): String = {
        ""
    }

    @ApiOperation(value = "An auth operation", authorizations = Array(
        new Authorization(value = "oauth2", scopes = Array(
            new AuthorizationScope(scope = "scope", description = "scope description")
        ))
    ))
    @Path("/root/withoutannotation/auth")
    @GET
    def withAuth(): String = {
        ""
    }

    @ApiOperation("A model operation")
    @Path("/root/withoutannotation/model")
    @GET
    def model(): String = {
        ""
    }

    @ApiOperation("An overriden operation")
    @Path("/root/withoutannotation/overriden")
    @GET
    def overriden(): String = {
        ""
    }

    @ApiOperation("An overriden operation")
    @Path("/root/withoutannotaiton/overridenWithoutDescription")
    @GET
    def overridenWithoutDescription(): String = {
        ""
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @Path("/root/withoutannotation/hidden")
    @GET
    def hidden(): String = {
        ""
    }

    @ApiOperation("A multiple parameters operation")
    @Path("/root/withoutannotation/multipleParameters/{parameter1}")
    @GET
    def multipleParameters(@PathParam("parameter1") parameterDouble: Double, @QueryParam("parameter2") parameterBool: Boolean): String = {
        ""
    }

    def ignoredModel(ignoredModel: IgnoredModel): String = {
        ""
    }

    @ApiOperation("A PATCH operation")
    @Path("/root/withoutannotation/patch")
    @PATCH
    def patch(): String = {
        ""
    }

    @ApiOperation("An OPTIONS operation")
    @Path("/root/withoutannotation/options")
    @OPTIONS
    def options(): Response = {
        Response.ok().build()
    }

    @ApiOperation("An HEAD operation")
    @Path("/root/withoutannotation/head")
    @HEAD
    def head(): String = {
        ""
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams(Array(
        new ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body")
    ))
    @Path("/root/withoutannotation/implicitparams")
    @POST
    def implicitParams(requestModel: String): String = {
        ""
    }
}

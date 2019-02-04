package com.benjaminsproule.swagger.gradleplugin.test.kotlin.jaxrs

import com.benjaminsproule.swagger.gradleplugin.test.model.IgnoredModel
import com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel
import com.benjaminsproule.swagger.gradleplugin.test.model.ResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.model.SubResponseModel
import io.swagger.annotations.*
import java.util.Collections.singletonList
import javax.ws.rs.*
import javax.ws.rs.core.Response

@Api(tags = ["Test"], description = "Test resource", authorizations = [Authorization("basic")])
open class TestResourceWithoutClassAnnotation {

    @ApiOperation("A basic operation")
    @Path("/root/withoutannotation/basic")
    @GET
    fun basic(): String {
        return ""
    }

    @ApiOperation("A default operation")
    @Path("/root/withoutannotation/default")
    @GET
    fun defaultResponse(): Response {
        return Response.ok().build()
    }

    @ApiOperation("A generics operation")
    @Path("/root/withoutannotation/generics")
    @POST
    fun generics(@ApiParam body: List<RequestModel>): List<String> {
        return singletonList("")
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/root/withoutannotation/datatype")
    @Consumes("application/json")
    @Produces("application/json")
    @POST
    fun dataType(@ApiParam requestModel: RequestModel): Response {
        return Response.ok().build()
    }

    @ApiOperation("A response operation", response = ResponseModel::class)
    @Path("/root/withoutannotation/response")
    @POST
    fun response(): ResponseModel {
        return ResponseModel()
    }

    @ApiOperation("A response container operation", response = ResponseModel::class, responseContainer = "List")
    @Path("/root/withoutannotation/responseContainer")
    @POST
    fun responseContainer(): List<ResponseModel> {
        return singletonList(ResponseModel())
    }

    @ApiOperation("An extended operation")
    @Path("/root/withoutannotation/extended")
    @GET
    fun extended(): SubResponseModel {
        return SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @Path("/root/withoutannotation/deprecated")
    @GET
    @Deprecated(message = "Deprecated", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith("\"\""))
    fun deprecated(): String {
        return ""
    }

    @ApiOperation("An auth operation", authorizations = [
        Authorization(value = "oauth2", scopes = [
            AuthorizationScope(scope = "scope", description = "scope description")
        ])
    ])
    @Path("/root/withoutannotation/auth")
    @GET
    fun withAuth(): String {
        return ""
    }

    @ApiOperation("A model operation")
    @Path("/root/withoutannotation/model")
    @GET
    fun model(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @Path("/root/withoutannotation/overriden")
    @GET
    open fun overriden(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @Path("/root/withoutannotaiton/overridenWithoutDescription")
    @GET
    open fun overridenWithoutDescription(): String {
        return ""
    }

    @ApiOperation("A hidden operation", hidden = true)
    @Path("/root/withoutannotation/hidden")
    @GET
    fun hidden(): String {
        return ""
    }

    @ApiOperation("A multiple parameters operation")
    @Path("/root/withoutannotation/multipleParameters/{parameter1}")
    @GET
    fun multipleParameters(@PathParam("parameter1") parameterDouble: Double, @QueryParam("parameter2") parameterBool: Boolean): String {
        return ""
    }

    fun ignoredModel(ignoredModel: IgnoredModel): String {
        return ""
    }

    @ApiOperation("A PATCH operation")
    @Path("/root/withoutannotation/patch")
    @PATCH
    fun patch(): String {
        return ""
    }

    @ApiOperation("An OPTIONS operation")
    @Path("/root/withoutannotation/options")
    @OPTIONS
    fun options(): Response {
        return Response.ok().build()
    }

    @ApiOperation("An HEAD operation")
    @Path("/root/withoutannotation/head")
    @HEAD
    fun head(): String {
        return ""
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams(
        ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header"),
        ApiImplicitParam(name = "something", value = "Implicit parameter of an undefined type", dataType = "SomethingElse", paramType = "header")
    )
    @Path("/root/withoutannotation/implicitparams")
    @POST
    fun implicitParams(requestModel: String): String {
        return ""
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @Path("/root/withoutannotation/createdrequest")
    @POST
    fun createdRequest(): String {
        return "";
    }
}

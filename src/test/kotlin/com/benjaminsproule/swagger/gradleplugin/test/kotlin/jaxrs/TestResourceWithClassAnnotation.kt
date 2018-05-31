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
@Path("/root/withannotation")
open class TestResourceWithClassAnnotation {

    @ApiOperation("A basic operation")
    @Path("/basic")
    @GET
    fun basic(): String {
        return ""
    }

    @ApiOperation("A default operation")
    @Path("/default")
    @GET
    fun defaultResponse(): Response {
        return Response.ok().build()
    }

    @ApiOperation("A generics operation")
    @Path("/generics")
    @POST
    fun generics(@ApiParam body: List<RequestModel>): List<String> {
        return singletonList("") as List<String>
    }

    @ApiOperation("Consumes and Produces operation")
    @Path("/datatype")
    @Consumes("application/json")
    @Produces("application/json")
    @POST
    fun dataType(@ApiParam requestModel: RequestModel): Response {
        return Response.ok().build()
    }

    @ApiOperation("A response operation", response = ResponseModel::class)
    @Path("/response")
    @POST
    fun response(): ResponseModel {
        return ResponseModel()
    }

    @ApiOperation("A response container operation", response = ResponseModel::class, responseContainer = "List")
    @Path("/responseContainer")
    @POST
    fun responseContainer(): List<ResponseModel> {
        return singletonList(ResponseModel())
    }

    @ApiOperation("An extended operation")
    @Path("/extended")
    @GET
    fun extended(): SubResponseModel {
        return SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @Path("/deprecated")
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
    @Path("/auth")
    @GET
    fun withAuth(): String {
        return ""
    }

    @ApiOperation("A model operation")
    @Path("/model")
    @GET
    fun model(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @Path("/overriden")
    @GET
    open fun overriden(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @Path("/overridenWithoutDescription")
    @GET
    open fun overridenWithoutDescription(): String {
        return ""
    }

    @ApiOperation("A hidden operation", hidden = true)
    @Path("/hidden")
    @GET
    fun hidden(): String {
        return ""
    }

    @ApiOperation("A multiple parameters operation")
    @Path("/multipleParameters/{parameter1}")
    @GET
    fun multipleParameters(@PathParam("parameter1") parameterDouble: Double, @QueryParam("parameter2") parameterBool: Boolean): String {
        return ""
    }

    fun ignoredModel(ignoredModel: IgnoredModel): String {
        return ""
    }
}

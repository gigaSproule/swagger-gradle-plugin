package com.benjaminsproule.swagger.gradleplugin.test.kotlin.springmvc

import com.benjaminsproule.swagger.gradleplugin.test.model.*
import io.swagger.annotations.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import java.util.Collections.singletonList

@Suppress("UNUSED_PARAMETER")
@Api(tags = ["Test"], description = "Test resource", authorizations = [Authorization("basic")])
open class TestResourceWithoutClassAnnotation {

    @ApiOperation("A basic operation")
    @RequestMapping(path = ["/root/withoutannotation/basic"], method = [RequestMethod.GET])
    fun basic(): String {
        return ""
    }

    @ApiOperation("A default operation")
    @RequestMapping(path = ["/root/withoutannotation/default"], method = [RequestMethod.GET])
    fun defaultResponse(): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @ApiOperation("A generics operation")
    @RequestMapping(path = ["/root/withoutannotation/generics"], method = [RequestMethod.POST])
    fun generics(@ApiParam body: List<RequestModel>): List<String> {
        return singletonList("")
    }

    @ApiOperation("Consumes and Produces operation")
    @RequestMapping(path = ["/root/withoutannotation/datatype"], method = [RequestMethod.POST], consumes = ["application/json"], produces = ["application/json"])
    fun dataType(@ApiParam requestModel: RequestModel): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @ApiOperation("A response operation", response = ResponseModel::class)
    @RequestMapping(path = ["/root/withoutannotation/response"], method = [RequestMethod.POST])
    fun response(@ApiParam body: List<RequestModel>): ResponseModel {
        return ResponseModel()
    }

    @ApiOperation("A response container operation", response = ResponseModel::class, responseContainer = "List")
    @RequestMapping(path = ["/root/withoutannotation/responseContainer"], method = [RequestMethod.POST])
    fun responseContainer(@ApiParam body: List<RequestModel>): List<ResponseModel> {
        return singletonList(ResponseModel())
    }

    @ApiOperation("An extended operation")
    @RequestMapping(path = ["/root/withoutannotation/extended"], method = [RequestMethod.GET])
    fun extended(): SubResponseModel {
        return SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @RequestMapping(path = ["/root/withoutannotation/deprecated"], method = [RequestMethod.GET])
    @Deprecated(message = "Deprecated", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith("\"\""))
    fun deprecated(): String {
        return ""
    }

    @ApiOperation("An auth operation", authorizations = [
        Authorization(value = "oauth2", scopes = [
            AuthorizationScope(scope = "scope", description = "scope description")
        ])
    ])
    @RequestMapping(path = ["/root/withoutannotation/auth"], method = [RequestMethod.GET])
    fun withAuth(): String {
        return ""
    }

    @ApiOperation("A model operation")
    @RequestMapping(path = ["/root/withoutannotation/model"], method = [RequestMethod.GET])
    fun model(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = ["/root/withoutannotation/overriden"], method = [RequestMethod.GET])
    open fun overriden(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = ["/root/withoutannotation/overridenWithoutDescription"], method = [RequestMethod.GET])
    open fun overridenWithoutDescription(): String {
        return ""
    }

    @ApiOperation("A hidden operation", hidden = true)
    @RequestMapping(path = ["/root/withoutannotation/hidden"], method = [RequestMethod.GET])
    fun hidden(): String {
        return ""
    }

    @ApiOperation("A multiple parameters operation")
    @RequestMapping(path = ["/root/withoutannotation/multipleParameters/{parameter1}"], method = [RequestMethod.GET])
    fun multipleParameters(
        @RequestParam("parameter1") parameterDouble: Double,
        @RequestParam(name = "parameter2", required = false) parameterBool: Boolean): String {
        return ""
    }

    fun ignoredModel(ignoredModel: IgnoredModel): String {
        return ""
    }

    @ApiOperation("A PATCH operation")
    @RequestMapping(path = ["/root/withoutannotation/patch"], method = [RequestMethod.PATCH])
    fun patch(): String {
        return ""
    }

    @ApiOperation("An OPTIONS operation")
    @RequestMapping(path = ["/root/withoutannotation/options"], method = [RequestMethod.OPTIONS])
    fun options(): ResponseEntity<String> {
        return ResponseEntity.ok().build()
    }

    @ApiOperation("An HEAD operation")
    @RequestMapping(path = ["/root/withoutannotation/head"], method = [RequestMethod.HEAD])
    fun head(): String {
        return ""
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams(
        ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header")
    )
    @RequestMapping(path = ["/root/withoutannotation/implicitparams"], method = [RequestMethod.POST])
    fun implicitParams(requestModel: String): String {
        return ""
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @RequestMapping(path = ["/root/withoutannotation/createdrequest"], method = [RequestMethod.POST])
    fun createdRequest(): String {
        return ""
    }

    @ApiOperation(value = "A inner JSON sub type operation")
    @RequestMapping(path = ["/root/withoutannotation/innerjsonsubtype"], method = [RequestMethod.GET])
    fun innerJsonSubType(): OuterJsonSubType {
        return OuterJsonSubType()
    }
}

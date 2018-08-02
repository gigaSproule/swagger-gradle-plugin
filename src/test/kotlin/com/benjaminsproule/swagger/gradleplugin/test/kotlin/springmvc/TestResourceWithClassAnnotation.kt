package com.benjaminsproule.swagger.gradleplugin.test.kotlin.springmvc

import com.benjaminsproule.swagger.gradleplugin.test.model.IgnoredModel
import com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel
import com.benjaminsproule.swagger.gradleplugin.test.model.ResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.model.SubResponseModel
import io.swagger.annotations.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import java.util.Collections.singletonList

@Api(tags = ["Test"], description = "Test resource", authorizations = [Authorization("basic")])
@RequestMapping(path = ["/root/withannotation"])
open class TestResourceWithClassAnnotation {

    @ApiOperation("A basic operation")
    @RequestMapping(path = ["/basic"], method = [(RequestMethod.GET)])
    fun basic(): String {
        return ""
    }

    @ApiOperation("A default operation")
    @RequestMapping(path = ["/default"], method = [(RequestMethod.GET)])
    fun defaultResponse(): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @ApiOperation("A generics operation")
    @RequestMapping(path = ["/generics"], method = [(RequestMethod.POST)])
    fun generics(@ApiParam body: List<RequestModel>): List<String> {
        return singletonList("")
    }

    @ApiOperation("Consumes and Produces operation")
    @RequestMapping(path = ["/datatype"], method = [(RequestMethod.POST)], consumes = ["application/json"], produces = ["application/json"])
    fun dataType(@ApiParam requestModel: RequestModel): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @ApiOperation("A response operation", response = ResponseModel::class)
    @RequestMapping(path = ["/response"], method = [(RequestMethod.POST)])
    fun response(@ApiParam body: List<RequestModel>): ResponseModel {
        return ResponseModel()
    }

    @ApiOperation("A response container operation", response = ResponseModel::class, responseContainer = "List")
    @RequestMapping(path = ["/responseContainer"], method = [(RequestMethod.POST)])
    fun responseContainer(@ApiParam body: List<RequestModel>): List<ResponseModel> {
        return singletonList(ResponseModel())
    }

    @ApiOperation("An extended operation")
    @RequestMapping(path = ["/extended"], method = [(RequestMethod.GET)])
    fun extended(): SubResponseModel {
        return SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @RequestMapping(path = ["/deprecated"], method = [(RequestMethod.GET)])
    @Deprecated(message = "Deprecated", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith("\"\""))
    fun deprecated(): String {
        return ""
    }

    @ApiOperation("An auth operation", authorizations = [
        Authorization(value = "oauth2", scopes = [
            AuthorizationScope(scope = "scope", description = "scope description")
        ])
    ])
    @RequestMapping(path = ["/auth"], method = [(RequestMethod.GET)])
    fun withAuth(): String {
        return ""
    }

    @ApiOperation("A model operation")
    @RequestMapping(path = ["/model"], method = [(RequestMethod.GET)])
    fun model(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = ["/overriden"], method = [(RequestMethod.GET)])
    open fun overriden(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = ["/overridenWithoutDescription"], method = [(RequestMethod.GET)])
    open fun overridenWithoutDescription(): String {
        return ""
    }

    @ApiOperation("A hidden operation", hidden = true)
    @RequestMapping(path = ["/hidden"], method = [(RequestMethod.GET)])
    fun hidden(): String {
        return ""
    }

    @ApiOperation("A multiple parameters operation")
    @RequestMapping(path = ["/multipleParameters/{parameter1}"], method = [(RequestMethod.GET)])
    fun multipleParameters(
        @RequestParam("parameter1") parameterDouble: Double,
        @RequestParam(name = "parameter2", required = false) parameterBool: Boolean): String {
        return ""
    }

    fun ignoredModel(ignoredModel: IgnoredModel): String {
        return ""
    }

    @ApiOperation("A PATCH operation")
    @RequestMapping(path = ["/patch"], method = [(RequestMethod.PATCH)])
    fun patch(): String {
        return ""
    }

    @ApiOperation("An OPTIONS operation")
    @RequestMapping(path = ["/options"], method = [(RequestMethod.OPTIONS)])
    fun options() {
    }

    @ApiOperation("An HEAD operation")
    @RequestMapping(path = ["/head"], method = [(RequestMethod.HEAD)])
    fun head(): String {
        return ""
    }
}

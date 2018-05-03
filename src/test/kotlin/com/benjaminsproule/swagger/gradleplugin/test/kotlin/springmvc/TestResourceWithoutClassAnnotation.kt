package com.benjaminsproule.swagger.gradleplugin.test.kotlin.springmvc

import com.benjaminsproule.swagger.gradleplugin.ignore.IgnoredModel
import com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel
import com.benjaminsproule.swagger.gradleplugin.test.model.ResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.model.SubResponseModel
import io.swagger.annotations.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import java.util.Collections.singletonList

@Api(tags = ["Test"], description = "Test resource", authorizations = [(Authorization("basic"))])
open class TestResourceWithoutClassAnnotation {

    @ApiOperation("A basic operation")
    @RequestMapping(path = ["/root/withoutannotation/basic"], method = [(RequestMethod.GET)])
    fun basic(): String {
        return ""
    }

    @ApiOperation(value = "A default operation")
    @RequestMapping(path = ["/root/withoutannotation/default"], method = [(RequestMethod.GET)])
    fun defaultResponse(): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @ApiOperation(value = "A generics operation")
    @RequestMapping(path = ["/root/withoutannotation/generics"], method = [(RequestMethod.POST)])
    fun generics(@ApiParam body: List<RequestModel>): List<String> {
        return singletonList("")
    }

    @ApiOperation("Consumes and Produces operation")
    @RequestMapping(path = ["/root/withoutannotation/datatype"], method = [(RequestMethod.POST)], consumes = ["application/json"], produces = ["application/json"])
    fun dataType(@ApiParam requestModel: RequestModel): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @ApiOperation(value = "A response operation", response = ResponseModel::class)
    @RequestMapping(path = ["/root/withoutannotation/response"], method = [(RequestMethod.POST)])
    fun response(@ApiParam body: List<RequestModel>): ResponseModel {
        return ResponseModel()
    }

    @ApiOperation(value = "A response container operation", response = ResponseModel::class, responseContainer = "List")
    @RequestMapping(path = ["/root/withoutannotation/responseContainer"], method = [(RequestMethod.POST)])
    fun responseContainer(@ApiParam body: List<RequestModel>): List<ResponseModel> {
        return singletonList(ResponseModel())
    }

    @ApiOperation("An extended operation")
    @RequestMapping(path = ["/root/withoutannotation/extended"], method = [(RequestMethod.GET)])
    fun extended(): SubResponseModel {
        return SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @RequestMapping(path = ["/root/withoutannotation/deprecated"], method = [(RequestMethod.GET)])
    @Deprecated(message = "Deprecated", level = DeprecationLevel.WARNING, replaceWith = ReplaceWith("\"\""))
    fun deprecated(): String {
        return ""
    }

    @ApiOperation(value = "An auth operation", authorizations = [
        Authorization(value = "oauth2", scopes = [
            AuthorizationScope(scope = "scope", description = "scope description")
        ])
    ])
    @RequestMapping(path = ["/root/withoutannotation/auth"], method = [(RequestMethod.GET)])
    fun withAuth(): String {
        return ""
    }

    @ApiOperation(value = "A model operation")
    @RequestMapping(path = ["/root/withoutannotation/model"], method = [(RequestMethod.GET)])
    fun model(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = ["/root/withoutannotation/overriden"], method = [(RequestMethod.GET)])
    open fun overriden(): String {
        return ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = ["/root/withoutannotation/overridenWithoutDescription"], method = [(RequestMethod.GET)])
    open fun overridenWithoutDescription(): String {
        return ""
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @RequestMapping(path = ["/root/withoutannotation/hidden"], method = [(RequestMethod.GET)])
    fun hidden(): String {
        return ""
    }

    @ApiOperation(value = "An ignored model")
    @RequestMapping(value = ["/root/withoutannotation/ignoredModel"], method = [(RequestMethod.GET)])
    fun ignoredModel(ignoredModel: IgnoredModel): String {
        return ""
    }
}

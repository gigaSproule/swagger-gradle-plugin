package com.benjaminsproule.swagger.gradleplugin.test.scala.springmvc

import com.benjaminsproule.swagger.gradleplugin.test.model.{IgnoredModel, RequestModel, ResponseModel, SubResponseModel}
import io.swagger.annotations._
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam}


@Api(tags = Array("Test"), description = "Test resource", authorizations = Array(
    new Authorization("basic")
))
class TestResourceWithoutClassAnnotation {

    @ApiOperation("A basic operation")
    @RequestMapping(path = Array("/root/withoutannotation/basic"), method = Array(RequestMethod.GET))
    def basic(): String = {
        ""
    }

    @ApiOperation("A default operation")
    @RequestMapping(path = Array("/root/withoutannotation/default"), method = Array(RequestMethod.GET))
    def defaultResponse(): ResponseEntity[Any] = {
        ResponseEntity.ok().build()
    }

    @ApiOperation("A generics operation")
    @RequestMapping(path = Array("/root/withoutannotation/generics"), method = Array(RequestMethod.POST))
    def generics(@ApiParam body: List[RequestModel]): List[String] = {
        List("")
    }

    @ApiOperation("Consumes and Produces operation")
    @RequestMapping(path = Array("/root/withoutannotation/datatype"), method = Array(RequestMethod.POST), consumes = Array("application/json"), produces = Array("application/json"))
    def dataType(@ApiParam requestModel: RequestModel): ResponseEntity[Any] = {
        ResponseEntity.ok().build()
    }

    @ApiOperation(value = "A response operation", response = classOf[ResponseModel])
    @RequestMapping(path = Array("/root/withoutannotation/response"), method = Array(RequestMethod.POST))
    def response(@ApiParam body: List[RequestModel]): ResponseModel = {
        new ResponseModel()
    }

    @ApiOperation(value = "A response container operation", response = classOf[ResponseModel], responseContainer = "List")
    @RequestMapping(path = Array("/root/withoutannotation/responseContainer"), method = Array(RequestMethod.POST))
    def responseContainer(@ApiParam body: List[RequestModel]): List[ResponseModel] = {
        List(new ResponseModel())
    }

    @ApiOperation("An extended operation")
    @RequestMapping(path = Array("/root/withoutannotation/extended"), method = Array(RequestMethod.GET))
    def extended(): SubResponseModel = {
        new SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @RequestMapping(path = Array("/root/withoutannotation/deprecated"), method = Array(RequestMethod.GET))
    @Deprecated
    def deprecated(): String = {
        ""
    }

    @ApiOperation(value = "An auth operation", authorizations = Array(
        new Authorization(value = "oauth2", scopes = Array(
            new AuthorizationScope(scope = "scope", description = "scope description")
        ))
    ))
    @RequestMapping(path = Array("/root/withoutannotation/auth"), method = Array(RequestMethod.GET))
    def withAuth(): String = {
        ""
    }

    @ApiOperation("A model operation")
    @RequestMapping(path = Array("/root/withoutannotation/model"), method = Array(RequestMethod.GET))
    def model(): String = {
        ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = Array("/root/withoutannotation/overriden"), method = Array(RequestMethod.GET))
    def overriden(): String = {
        ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = Array("/root/withoutannotation/overridenWithoutDescription"), method = Array(RequestMethod.GET))
    def overridenWithoutDescription(): String = {
        ""
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @RequestMapping(path = Array("/root/withoutannotation/hidden"), method = Array(RequestMethod.GET))
    def hidden(): String = {
        ""
    }

    @ApiOperation("A multiple parameters operation")
    @RequestMapping(path = Array("/root/withoutannotation/multipleParameters/{parameter1}"), method = Array(RequestMethod.GET))
    def multipleParameters(@RequestParam("parameter1") parameterDouble: Double,
                           @RequestParam(name = "parameter2", required = false) parameterBool: Boolean): String = {
        ""
    }

    def ignoredModel(ignoredModel: IgnoredModel): String = {
        ""
    }

    @ApiOperation("A PATCH operation")
    @RequestMapping(path = Array("/root/withoutannotation/patch"), method = Array(RequestMethod.PATCH))
    def patch(): String = {
        ""
    }

    @ApiOperation("An OPTIONS operation")
    @RequestMapping(path = Array("/root/withoutannotation/options"), method = Array(RequestMethod.OPTIONS))
    def options(): ResponseEntity[Any] = {
        ResponseEntity.ok().build()
    }

    @ApiOperation("An HEAD operation")
    @RequestMapping(path = Array("/root/withoutannotation/head"), method = Array(RequestMethod.HEAD))
    def head(): String = {
        ""
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams(Array(
        new ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        new ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header"),
        new ApiImplicitParam(name = "something", value = "Implicit parameter of an undefined type", dataType = "SomethingElse", paramType = "header")
    ))
    @RequestMapping(path = Array("/root/withoutannotation/implicitparams"), method = Array(RequestMethod.POST))
    def implicitParams(requestModel: String): String = {
        ""
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @RequestMapping(path = Array("/root/withoutannotation/createdrequest"), method = Array(RequestMethod.POST))
    def createdRequest(): String = {
        ""
    }
}

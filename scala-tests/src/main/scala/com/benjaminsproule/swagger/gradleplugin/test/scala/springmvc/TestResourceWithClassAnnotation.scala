package com.benjaminsproule.swagger.gradleplugin.test.scala.springmvc

import com.benjaminsproule.swagger.gradleplugin.test.model._
import com.fasterxml.jackson.annotation.JsonView
import io.swagger.annotations._
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam}


@Api(tags = Array("Test"), description = "Test resource", authorizations = Array(
    new Authorization("basic")
))
@RequestMapping(path = Array("/root/withannotation"))
class TestResourceWithClassAnnotation {

    @ApiOperation("A basic operation")
    @RequestMapping(path = Array("/basic"), method = Array(RequestMethod.GET))
    def basic(): String = {
        ""
    }

    @ApiOperation("A default operation")
    @RequestMapping(path = Array("/default"), method = Array(RequestMethod.GET))
    def defaultResponse(): ResponseEntity[Any] = {
        ResponseEntity.ok().build()
    }

    @ApiOperation("A generics operation")
    @RequestMapping(path = Array("/generics"), method = Array(RequestMethod.POST))
    def generics(@ApiParam body: List[RequestModel]): List[String] = {
        List("")
    }

    @ApiOperation("Consumes and Produces operation")
    @RequestMapping(path = Array("/datatype"), method = Array(RequestMethod.POST), consumes = Array("application/json"), produces = Array("application/json"))
    def dataType(@ApiParam requestModel: RequestModel): ResponseEntity[Any] = {
        ResponseEntity.ok().build()
    }

    @ApiOperation(value = "A response operation", response = classOf[ResponseModel])
    @RequestMapping(path = Array("/response"), method = Array(RequestMethod.POST))
    def response(@ApiParam body: List[RequestModel]): ResponseModel = {
        new ResponseModel()
    }

    @ApiOperation(value = "A response container operation", response = classOf[ResponseModel], responseContainer = "List")
    @RequestMapping(path = Array("/responseContainer"), method = Array(RequestMethod.POST))
    def responseContainer(@ApiParam body: List[RequestModel]): List[ResponseModel] = {
        List(new ResponseModel())
    }

    @ApiOperation("An extended operation")
    @RequestMapping(path = Array("/extended"), method = Array(RequestMethod.GET))
    def extended(): SubResponseModel = {
        new SubResponseModel()
    }

    @ApiOperation("A deprecated operation")
    @RequestMapping(path = Array("/deprecated"), method = Array(RequestMethod.GET))
    @Deprecated
    def deprecated(): String = {
        ""
    }

    @ApiOperation(value = "An auth operation", authorizations = Array(
        new Authorization(value = "oauth2", scopes = Array(
            new AuthorizationScope(scope = "scope", description = "scope description")
        ))
    ))
    @RequestMapping(path = Array("/auth"), method = Array(RequestMethod.GET))
    def withAuth(): String = {
        ""
    }

    @ApiOperation("A model operation")
    @RequestMapping(path = Array("/model"), method = Array(RequestMethod.GET))
    def model(): String = {
        ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = Array("/overriden"), method = Array(RequestMethod.GET))
    def overriden(): String = {
        ""
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = Array("/overridenWithoutDescription"), method = Array(RequestMethod.GET))
    def overridenWithoutDescription(): String = {
        ""
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @RequestMapping(path = Array("/hidden"), method = Array(RequestMethod.GET))
    def hidden(): String = {
        ""
    }

    @ApiOperation("A multiple parameters operation")
    @RequestMapping(path = Array("/multipleParameters/{parameter1}"), method = Array(RequestMethod.GET))
    def multipleParameters(@RequestParam("parameter1") parameterDouble: Double,
                           @RequestParam(name = "parameter2", required = false) parameterBool: Boolean): String = {
        ""
    }

    def ignoredModel(ignoredModel: IgnoredModel): String = {
        ""
    }

    @ApiOperation("A PATCH operation")
    @RequestMapping(path = Array("/patch"), method = Array(RequestMethod.PATCH))
    def patch(): String = {
        ""
    }

    @ApiOperation("An OPTIONS operation")
    @RequestMapping(path = Array("/options"), method = Array(RequestMethod.OPTIONS))
    def options(): ResponseEntity[Any] = {
        ResponseEntity.ok().build()
    }

    @ApiOperation("An HEAD operation")
    @RequestMapping(path = Array("/head"), method = Array(RequestMethod.HEAD))
    def head(): String = {
        ""
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams(Array(
        new ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        new ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header")
    ))
    @RequestMapping(path = Array("/implicitparams"), method = Array(RequestMethod.POST))
    def implicitParams(requestModel: String): String = {
        ""
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @RequestMapping(path = Array("/createdrequest"), method = Array(RequestMethod.POST))
    def createdRequest(): String = {
        ""
    }

    @ApiOperation(value = "A inner JSON sub type operation")
    @RequestMapping(path = Array("/innerjsonsubtype"), method = Array(RequestMethod.GET))
    def innerJsonSubType(): OuterJsonSubType = {
        new OuterJsonSubType()
    }

    @ApiOperation(value = "With JsonViewOne specification")
    @RequestMapping(path = Array("/withjsonview1"), method = Array(RequestMethod.GET))
    @JsonView(value = Array(classOf[TestJsonViewOne]))
    def withJsonViewOne(): TestJsonViewEntity = {
        null
    }

    @ApiOperation("With JsonViewTwo specification")
    @RequestMapping(path = Array("/withjsonview2"), method = Array(RequestMethod.GET))
    @JsonView(value = Array(classOf[TestJsonViewTwo]))
    def withJsonViewTwo(): TestJsonViewEntity = {
        null
    }

    @ApiOperation("Entity definition has to contain all possible fields")
    @RequestMapping(path = Array("/withoutjsonview1"), method = Array(RequestMethod.GET))
    def withoutJsonView(): TestJsonViewEntity = {
        null
    }
}

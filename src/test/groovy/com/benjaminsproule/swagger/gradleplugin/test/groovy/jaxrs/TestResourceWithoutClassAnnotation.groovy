package com.benjaminsproule.swagger.gradleplugin.test.groovy.jaxrs

import com.benjaminsproule.swagger.gradleplugin.test.model.IgnoredModel
import com.benjaminsproule.swagger.gradleplugin.test.model.OuterJsonSubType
import com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel
import com.benjaminsproule.swagger.gradleplugin.test.model.ResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.model.SubResponseModel
import io.swagger.annotations.*

import javax.ws.rs.*
import javax.ws.rs.core.Response

import static java.util.Collections.singletonList

@Api(tags = 'Test', description = 'Test resource', authorizations = @Authorization('basic'))
class TestResourceWithoutClassAnnotation {

    @ApiOperation('A basic operation')
    @Path('/root/withoutannotation/basic')
    @GET
    String basic() {
        return ''
    }

    @ApiOperation('A default operation')
    @Path('/root/withoutannotation/default')
    @GET
    Response defaultResponse() {
        return Response.ok().build()
    }

    @ApiOperation('A generics operation')
    @Path('/root/withoutannotation/generics')
    @POST
    List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList('')
    }

    @ApiOperation('Consumes and Produces operation')
    @Path('/root/withoutannotation/datatype')
    @Consumes('application/json')
    @Produces('application/json')
    @POST
    Response dataType(@ApiParam RequestModel requestModel) {
        return Response.ok().build()
    }

    @ApiOperation(value = 'A response operation', response = ResponseModel)
    @Path('/root/withoutannotation/response')
    @POST
    ResponseModel response() {
        return new ResponseModel()
    }

    @ApiOperation(value = 'A response container operation', response = ResponseModel, responseContainer = 'List')
    @Path('/root/withoutannotation/responseContainer')
    @POST
    List<ResponseModel> responseContainer() {
        return singletonList(new ResponseModel())
    }

    @ApiOperation('An extended operation')
    @Path('/root/withoutannotation/extended')
    @GET
    SubResponseModel extended() {
        return new SubResponseModel()
    }

    @ApiOperation('A deprecated operation')
    @Path('/root/withoutannotation/deprecated')
    @GET
    @Deprecated
    String deprecated() {
        return ''
    }

    @ApiOperation(value = 'An auth operation', authorizations =
        @Authorization(value = 'oauth2', scopes =
            @AuthorizationScope(scope = 'scope', description = 'scope description')
        )
    )
    @Path('/root/withoutannotation/auth')
    @GET
    String withAuth() {
        return ''
    }

    @ApiOperation('A model operation')
    @Path('/root/withoutannotation/model')
    @GET
    String model() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @Path('/root/withoutannotation/overriden')
    @GET
    String overriden() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @Path('/root/withoutannotaiton/overridenWithoutDescription')
    @GET
    String overridenWithoutDescription() {
        return ''
    }

    @ApiOperation(value = 'A hidden operation', hidden = true)
    @Path('/root/withoutannotation/hidden')
    @GET
    String hidden() {
        return ''
    }

    @ApiOperation('A multiple parameters operation')
    @Path('/root/withoutannotation/multipleParameters/{parameter1}')
    @GET
    String multipleParameters(@PathParam('parameter1') Double parameterDouble, @QueryParam('parameter2') Boolean parameterBool) {
        return ''
    }

    String ignoredModel(IgnoredModel ignoredModel) {
        return ''
    }

    @ApiOperation('A PATCH operation')
    @Path('/root/withoutannotation/patch')
    @PATCH
    String patch() {
        return ''
    }

    @ApiOperation('An OPTIONS operation')
    @Path('/root/withoutannotation/options')
    @OPTIONS
    Response options() {
        return Response.ok().build()
    }

    @ApiOperation('An HEAD operation')
    @Path('/root/withoutannotation/head')
    @HEAD
    String head() {
        return ''
    }

    @ApiOperation(value = 'An implicit params operation')
    @ApiImplicitParams([
        @ApiImplicitParam(name = 'body', required = true, dataType = 'com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel', paramType = 'body'),
        @ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header"),
        @ApiImplicitParam(name = "something", value = "Implicit parameter of an undefined type", dataType = "SomethingElse", paramType = "header")
    ])
    @Path('/root/withoutannotation/implicitparams')
    @POST
    String implicitParams(String requestModel) {
        return ''
    }

    @ApiOperation(value = 'A created request operation', code = 201)
    @Path('/root/withoutannotation/createdrequest')
    @POST
    String createdRequest() {
        return ''
    }

    @ApiOperation(value = 'A inner JSON sub type operation')
    @Path('/root/withoutannotation/innerjsonsubtype')
    @GET
    OuterJsonSubType innerJsonSubType() {
        return new OuterJsonSubType()
    }
}

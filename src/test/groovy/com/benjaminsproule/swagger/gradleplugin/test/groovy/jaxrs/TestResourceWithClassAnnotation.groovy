package com.benjaminsproule.swagger.gradleplugin.test.groovy.jaxrs

import com.benjaminsproule.swagger.gradleplugin.ignore.IgnoredModel
import com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel
import com.benjaminsproule.swagger.gradleplugin.test.model.ResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.model.SubResponseModel
import io.swagger.annotations.*

import javax.ws.rs.*
import javax.ws.rs.core.Response

import static java.util.Collections.singletonList

@Api(tags = 'Test', description = 'Test resource', authorizations = @Authorization('basic'))
@Path('/root/withannotation')
class TestResourceWithClassAnnotation {

    @ApiOperation('A basic operation')
    @Path('/basic')
    @GET
    String basic() {
        return ''
    }

    @ApiOperation(value = 'A default operation')
    @Path('/default')
    @GET
    Response defaultResponse() {
        return Response.ok().build()
    }

    @ApiOperation(value = 'A generics operation')
    @Path('/generics')
    @POST
    List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList('')
    }

    @ApiOperation('Consumes and Produces operation')
    @Path('/datatype')
    @Consumes('application/json')
    @Produces('application/json')
    @POST
    Response dataType(@ApiParam RequestModel requestModel) {
        return Response.ok().build()
    }

    @ApiOperation(value = 'A response operation', response = ResponseModel.class)
    @Path('/response')
    @POST
    ResponseModel response(@ApiParam List<RequestModel> body) {
        return new ResponseModel()
    }

    @ApiOperation(value = 'A response container operation', response = ResponseModel.class, responseContainer = 'List')
    @Path('/responseContainer')
    @POST
    List<ResponseModel> responseContainer(@ApiParam List<RequestModel> body) {
        return singletonList(new ResponseModel())
    }

    @ApiOperation('An extended operation')
    @Path('/extended')
    @GET
    SubResponseModel extended() {
        return new SubResponseModel()
    }

    @ApiOperation('A deprecated operation')
    @Path('/deprecated')
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
    @Path('/auth')
    @GET
    String withAuth() {
        return ''
    }

    @ApiOperation(value = 'A model operation')
    @Path('/model')
    @GET
    String model() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @Path('/overriden')
    @GET
    String overriden() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @Path('/overridenWithoutDescription')
    @GET
    String overridenWithoutDescription() {
        return ''
    }

    @ApiOperation(value = 'A hidden operation', hidden = true)
    @Path('/hidden')
    @GET
    String hidden() {
        return ''
    }

    @ApiOperation(value = 'An ignored model')
    @Path('/ignoredModel')
    @GET
    String ignoredModel(IgnoredModel ignoredModel) {
        return ''
    }
}

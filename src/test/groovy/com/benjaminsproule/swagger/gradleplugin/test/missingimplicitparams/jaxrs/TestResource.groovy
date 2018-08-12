package com.benjaminsproule.swagger.gradleplugin.test.missingimplicitparams.jaxrs

import io.swagger.annotations.*

import javax.ws.rs.POST
import javax.ws.rs.Path

@Api
@SwaggerDefinition(info = @Info(title = 'MissingImplicitParamsAPI', version = '1', license = @License(name = 'License')))
@Path('/missingimplicitparams')
class TestResource {
    @ApiOperation(value = 'A missing implicit params operation')
    @ApiImplicitParams(
        @ApiImplicitParam(name = 'body', required = true, dataType = 'com.benjaminsproule.swagger.gradleplugin.test.model.MissingRequestModel', paramType = 'body')
    )
    @Path('/')
    @POST
    String missingImplicitParams(String body) {
        return ''
    }
}

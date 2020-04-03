package com.benjaminsproule.swagger.gradleplugin.test.missingimplicitparams.springmvc

import io.swagger.annotations.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api
@SwaggerDefinition(info = @Info(title = 'MissingImplicitParamsAPI', version = '1', license = @License(name = 'License')))
@RequestMapping(path = ['/missingimplicitparams'])
class TestResource {
    @ApiOperation(value = 'A missing implicit params operation')
    @ApiImplicitParams(
        @ApiImplicitParam(name = 'body', required = true, dataType = 'com.benjaminsproule.swagger.gradleplugin.test.model.MissingRequestModel', paramType = 'body')
    )
    @PostMapping('/')
    String missingImplicitParams(String body) {
        return ''
    }
}

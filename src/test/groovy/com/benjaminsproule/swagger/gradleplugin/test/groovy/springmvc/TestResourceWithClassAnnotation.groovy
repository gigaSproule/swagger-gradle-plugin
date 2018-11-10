package com.benjaminsproule.swagger.gradleplugin.test.groovy.springmvc

import com.benjaminsproule.swagger.gradleplugin.test.model.IgnoredModel
import com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel
import com.benjaminsproule.swagger.gradleplugin.test.model.ResponseModel
import com.benjaminsproule.swagger.gradleplugin.test.model.SubResponseModel
import io.swagger.annotations.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import static java.util.Collections.singletonList

@Api(tags = 'Test', description = 'Test resource', authorizations = @Authorization('basic'))
@RequestMapping(path = '/root/withannotation')
class TestResourceWithClassAnnotation {

    @ApiOperation('A basic operation')
    @RequestMapping(path = '/basic', method = RequestMethod.GET)
    String basic() {
        return ''
    }

    @ApiOperation('A default operation')
    @RequestMapping(path = '/default', method = RequestMethod.GET)
    ResponseEntity<?> defaultResponse() {
        return ResponseEntity.ok().build()
    }

    @ApiOperation('A generics operation')
    @RequestMapping(path = '/generics', method = RequestMethod.POST)
    List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList('')
    }

    @ApiOperation('Consumes and Produces operation')
    @RequestMapping(path = '/datatype', method = RequestMethod.POST, consumes = 'application/json', produces = 'application/json')
    ResponseEntity<?> dataType(@ApiParam RequestModel requestModel) {
        return ResponseEntity.ok().build()
    }

    @ApiOperation(value = 'A response operation', response = ResponseModel)
    @RequestMapping(path = '/response', method = RequestMethod.POST)
    ResponseModel response() {
        return new ResponseModel()
    }

    @ApiOperation(value = 'A response container operation', response = ResponseModel, responseContainer = 'List')
    @RequestMapping(path = '/responseContainer', method = RequestMethod.POST)
    List<ResponseModel> responseContainer() {
        return singletonList(new ResponseModel())
    }

    @ApiOperation('An extended operation')
    @RequestMapping(path = '/extended', method = RequestMethod.GET)
    SubResponseModel extended() {
        return new SubResponseModel()
    }

    @ApiOperation('A deprecated operation')
    @RequestMapping(path = '/deprecated', method = RequestMethod.GET)
    @Deprecated
    String deprecated() {
        return ''
    }

    @ApiOperation(value = 'An auth operation', authorizations =
        @Authorization(value = 'oauth2', scopes =
            @AuthorizationScope(scope = 'scope', description = 'scope description')
        )
    )
    @RequestMapping(path = '/auth', method = RequestMethod.GET)
    String withAuth() {
        return ''
    }

    @ApiOperation('A model operation')
    @RequestMapping(path = '/model', method = RequestMethod.GET)
    String model() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @RequestMapping(path = '/overriden', method = RequestMethod.GET)
    String overriden() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @RequestMapping(path = '/overridenWithoutDescription', method = RequestMethod.GET)
    String overridenWithoutDescription() {
        return ''
    }

    @ApiOperation(value = 'A hidden operation', hidden = true)
    @RequestMapping(path = '/hidden', method = RequestMethod.GET)
    String hidden() {
        return ''
    }

    @ApiOperation('A multiple parameters operation')
    @RequestMapping(path = '/multipleParameters/{parameter1}', method = RequestMethod.GET)
    String multipleParameters(
        @RequestParam('parameter1') Double parameterDouble,
        @RequestParam(name = 'parameter2', required = false) Boolean parameterBool) {
        return ''
    }

    String ignoredModel(IgnoredModel ignoredModel) {
        return ''
    }

    @ApiOperation('A PATCH operation')
    @RequestMapping(path = '/patch', method = RequestMethod.PATCH)
    String patch() {
        return ''
    }

    @ApiOperation('An OPTIONS operation')
    @RequestMapping(path = '/options', method = RequestMethod.OPTIONS)
    ResponseEntity options() {
        return ResponseEntity.ok().build()
    }

    @ApiOperation('An HEAD operation')
    @RequestMapping(path = '/head', method = RequestMethod.HEAD)
    String head() {
        return ''
    }

    @ApiOperation(value = 'An implicit params operation')
    @ApiImplicitParams(
        @ApiImplicitParam(name = 'body', required = true, dataType = 'com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel', paramType = 'body')
    )
    @RequestMapping(path = '/implicitparams', method = RequestMethod.POST)
    String implicitParams(String requestModel) {
        return ''
    }

    @ApiOperation(value = 'A created request operation', code = 201)
    @RequestMapping(path = '/createdrequest', method = RequestMethod.POST)
    String createdRequest() {
        return ''
    }
}

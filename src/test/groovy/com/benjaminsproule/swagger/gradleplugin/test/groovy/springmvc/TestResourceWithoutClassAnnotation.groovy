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
class TestResourceWithoutClassAnnotation {

    @ApiOperation('A basic operation')
    @RequestMapping(path = '/root/withoutannotation/basic', method = RequestMethod.GET)
    String basic() {
        return ''
    }

    @ApiOperation('A default operation')
    @RequestMapping(path = '/root/withoutannotation/default', method = RequestMethod.GET)
    ResponseEntity<?> defaultResponse() {
        return ResponseEntity.ok().build()
    }

    @ApiOperation('A generics operation')
    @RequestMapping(path = '/root/withoutannotation/generics', method = RequestMethod.POST)
    List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList('')
    }

    @ApiOperation('Consumes and Produces operation')
    @RequestMapping(path = '/root/withoutannotation/datatype', method = RequestMethod.POST, consumes = 'application/json', produces = 'application/json')
    ResponseEntity<?> dataType(@ApiParam RequestModel requestModel) {
        return ResponseEntity.ok().build()
    }

    @ApiOperation(value = 'A response operation', response = ResponseModel.class)
    @RequestMapping(path = '/root/withoutannotation/response', method = RequestMethod.POST)
    ResponseModel response() {
        return new ResponseModel()
    }

    @ApiOperation(value = 'A response container operation', response = ResponseModel.class, responseContainer = 'List')
    @RequestMapping(path = '/root/withoutannotation/responseContainer', method = RequestMethod.POST)
    List<ResponseModel> responseContainer() {
        return singletonList(new ResponseModel())
    }

    @ApiOperation('An extended operation')
    @RequestMapping(path = '/root/withoutannotation/extended', method = RequestMethod.GET)
    SubResponseModel extended() {
        return new SubResponseModel()
    }

    @ApiOperation('A deprecated operation')
    @RequestMapping(path = '/root/withoutannotation/deprecated', method = RequestMethod.GET)
    @Deprecated
    String deprecated() {
        return ''
    }

    @ApiOperation(value = 'An auth operation', authorizations =
        @Authorization(value = 'oauth2', scopes =
            @AuthorizationScope(scope = 'scope', description = 'scope description')
        )
    )
    @RequestMapping(path = '/root/withoutannotation/auth', method = RequestMethod.GET)
    String withAuth() {
        return ''
    }

    @ApiOperation('A model operation')
    @RequestMapping(path = '/root/withoutannotation/model', method = RequestMethod.GET)
    String model() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @RequestMapping(path = '/root/withoutannotation/overriden', method = RequestMethod.GET)
    String overriden() {
        return ''
    }

    @ApiOperation('An overriden operation')
    @RequestMapping(path = '/root/withoutannotation/overridenWithoutDescription', method = RequestMethod.GET)
    String overridenWithoutDescription() {
        return ''
    }

    @ApiOperation(value = 'A hidden operation', hidden = true)
    @RequestMapping(path = '/root/withoutannotation/hidden', method = RequestMethod.GET)
    String hidden() {
        return ''
    }

    @ApiOperation('A multiple parameters operation')
    @RequestMapping(path = '/root/withoutannotation/multipleParameters/{parameter1}', method = RequestMethod.GET)
    String multipleParameters(
        @RequestParam("parameter1") Double parameterDouble,
        @RequestParam(name = "parameter2", required = false) Boolean parameterBool) {
        return ''
    }

    String ignoredModel(IgnoredModel ignoredModel) {
        return ''
    }

    @ApiOperation('A PATCH operation')
    @RequestMapping(path = '/root/withoutannotation/patch', method = RequestMethod.PATCH)
    String patch() {
        return ''
    }

    @ApiOperation('An OPTIONS operation')
    @RequestMapping(path = '/root/withoutannotation/options', method = RequestMethod.OPTIONS)
    ResponseEntity options() {
        return ResponseEntity.ok().build()
    }

    @ApiOperation('An HEAD operation')
    @RequestMapping(path = '/root/withoutannotation/head', method = RequestMethod.HEAD)
    String head() {
        return ''
    }
}

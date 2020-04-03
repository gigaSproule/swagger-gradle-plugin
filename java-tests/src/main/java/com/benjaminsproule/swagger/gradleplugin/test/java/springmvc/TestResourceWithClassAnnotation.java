package com.benjaminsproule.swagger.gradleplugin.test.java.springmvc;

import com.benjaminsproule.swagger.gradleplugin.test.model.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static java.util.Collections.singletonList;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
@RequestMapping(path = "/root/withannotation")
public class TestResourceWithClassAnnotation {

    @ApiOperation("A basic operation")
    @RequestMapping(path = "/basic", method = RequestMethod.GET)
    public String basic() {
        return "";
    }

    @ApiOperation("A default operation")
    @RequestMapping(path = "/default", method = RequestMethod.GET)
    public ResponseEntity<?> defaultResponse() {
        return ResponseEntity.ok().build();
    }

    @ApiOperation("A generics operation")
    @RequestMapping(path = "/generics", method = RequestMethod.POST)
    public List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList("");
    }

    @ApiOperation("Consumes and Produces operation")
    @RequestMapping(path = "/datatype", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> dataType(@ApiParam RequestModel requestModel) {
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "A response operation", response = ResponseModel.class)
    @RequestMapping(path = "/response", method = RequestMethod.POST)
    public ResponseModel response(@ApiParam List<RequestModel> body) {
        return new ResponseModel();
    }

    @ApiOperation(value = "A response container operation", response = ResponseModel.class, responseContainer = "List")
    @RequestMapping(path = "/responseContainer", method = RequestMethod.POST)
    public List<ResponseModel> responseContainer(@ApiParam List<RequestModel> body) {
        return singletonList(new ResponseModel());
    }

    @ApiOperation("An extended operation")
    @RequestMapping(path = "/extended", method = RequestMethod.GET)
    public SubResponseModel extended() {
        return new SubResponseModel();
    }

    @ApiOperation("A deprecated operation")
    @RequestMapping(path = "/deprecated", method = RequestMethod.GET)
    @Deprecated
    public String deprecated() {
        return "";
    }

    @ApiOperation(value = "An auth operation", authorizations = {
        @Authorization(value = "oauth2", scopes = {
            @AuthorizationScope(scope = "scope", description = "scope description")
        })
    })
    @RequestMapping(path = "/auth", method = RequestMethod.GET)
    public String withAuth() {
        return "";
    }

    @ApiOperation("A model operation")
    @RequestMapping(path = "/model", method = RequestMethod.GET)
    public String model() {
        return "";
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = "/overriden", method = RequestMethod.GET)
    public String overriden() {
        return "";
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = "/overridenWithoutDescription", method = RequestMethod.GET)
    public String overridenWithoutDescription() {
        return "";
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @RequestMapping(path = "/hidden", method = RequestMethod.GET)
    public String hidden() {
        return "";
    }

    @ApiOperation("A multiple parameters operation")
    @RequestMapping(path = "/multipleParameters/{parameter1}", method = RequestMethod.GET)
    public String multipleParameters(
        @RequestParam("parameter1") Double parameterDouble,
        @RequestParam(name = "parameter2", required = false) Boolean parameterBool) {
        return "";
    }

    String ignoredModel(IgnoredModel ignoredModel) {
        return "";
    }

    @ApiOperation("A PATCH operation")
    @RequestMapping(path = "/patch", method = RequestMethod.PATCH)
    public String patch() {
        return "";
    }

    @ApiOperation("An OPTIONS operation")
    @RequestMapping(path = "/options", method = RequestMethod.OPTIONS)
    public ResponseEntity options() {
        return ResponseEntity.ok().build();
    }

    @ApiOperation("An HEAD operation")
    @RequestMapping(path = "/head", method = RequestMethod.HEAD)
    public String head() {
        return "";
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        @ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header"),
        @ApiImplicitParam(name = "something", value = "Implicit parameter of an undefined type", dataType = "SomethingElse", paramType = "header")
    })
    @RequestMapping(path = "/implicitparams", method = RequestMethod.POST)
    public String implicitParams(String requestModel) {
        return "";
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @RequestMapping(path = "/createdrequest", method = RequestMethod.POST)
    public String createdRequest() {
        return "";
    }

    @ApiOperation(value = "A inner JSON sub type operation")
    @RequestMapping(path = "/innerjsonsubtype", method = RequestMethod.GET)
    public OuterJsonSubType innerJsonSubType() {
        return new OuterJsonSubType();
    }
}

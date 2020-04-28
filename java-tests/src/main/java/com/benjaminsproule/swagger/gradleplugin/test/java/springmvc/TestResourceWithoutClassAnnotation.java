package com.benjaminsproule.swagger.gradleplugin.test.java.springmvc;

import com.benjaminsproule.swagger.gradleplugin.test.model.*;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static java.util.Collections.singletonList;

@Api(tags = "Test", description = "Test resource", authorizations = {@Authorization("basic")})
public class TestResourceWithoutClassAnnotation {

    @ApiOperation("A basic operation")
    @RequestMapping(path = "/root/withoutannotation/basic", method = RequestMethod.GET)
    public String basic() {
        return "";
    }

    @ApiOperation("A default operation")
    @RequestMapping(path = "/root/withoutannotation/default", method = RequestMethod.GET)
    public ResponseEntity<?> defaultResponse() {
        return ResponseEntity.ok().build();
    }

    @ApiOperation("A generics operation")
    @RequestMapping(path = "/root/withoutannotation/generics", method = RequestMethod.POST)
    public List<String> generics(@ApiParam List<RequestModel> body) {
        return singletonList("");
    }

    @ApiOperation("Consumes and Produces operation")
    @RequestMapping(path = "/root/withoutannotation/datatype", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> dataType(@ApiParam RequestModel requestModel) {
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "A response operation", response = ResponseModel.class)
    @RequestMapping(path = "/root/withoutannotation/response", method = RequestMethod.POST)
    public ResponseModel response(@ApiParam List<RequestModel> body) {
        return new ResponseModel();
    }

    @ApiOperation(value = "A response container operation", response = ResponseModel.class, responseContainer = "List")
    @RequestMapping(path = "/root/withoutannotation/responseContainer", method = RequestMethod.POST)
    public List<ResponseModel> responseContainer(@ApiParam List<RequestModel> body) {
        return singletonList(new ResponseModel());
    }

    @ApiOperation("An extended operation")
    @RequestMapping(path = "/root/withoutannotation/extended", method = RequestMethod.GET)
    public SubResponseModel extended() {
        return new SubResponseModel();
    }

    @ApiOperation("A deprecated operation")
    @RequestMapping(path = "/root/withoutannotation/deprecated", method = RequestMethod.GET)
    @Deprecated
    public String deprecated() {
        return "";
    }

    @ApiOperation(value = "An auth operation", authorizations = {
        @Authorization(value = "oauth2", scopes = {
            @AuthorizationScope(scope = "scope", description = "scope description")
        })
    })
    @RequestMapping(path = "/root/withoutannotation/auth", method = RequestMethod.GET)
    public String withAuth() {
        return "";
    }

    @ApiOperation("A model operation")
    @RequestMapping(path = "/root/withoutannotation/model", method = RequestMethod.GET)
    public String model() {
        return "";
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = "/root/withoutannotation/overriden", method = RequestMethod.GET)
    public String overriden() {
        return "";
    }

    @ApiOperation("An overriden operation")
    @RequestMapping(path = "/root/withoutannotation/overridenWithoutDescription", method = RequestMethod.GET)
    public String overridenWithoutDescription() {
        return "";
    }

    @ApiOperation(value = "A hidden operation", hidden = true)
    @RequestMapping(path = "/root/withoutannotation/hidden", method = RequestMethod.GET)
    public String hidden() {
        return "";
    }

    @ApiOperation("A multiple parameters operation")
    @RequestMapping(path = "/root/withoutannotation/multipleParameters/{parameter1}", method = RequestMethod.GET)
    public String multipleParameters(
        @RequestParam("parameter1") Double parameterDouble,
        @RequestParam(name = "parameter2", required = false) Boolean parameterBool) {
        return "";
    }

    String ignoredModel(IgnoredModel ignoredModel) {
        return "";
    }

    @ApiOperation("A PATCH operation")
    @RequestMapping(path = "/root/withoutannotation/patch", method = RequestMethod.PATCH)
    public String patch() {
        return "";
    }

    @ApiOperation("An OPTIONS operation")
    @RequestMapping(path = "/root/withoutannotation/options", method = RequestMethod.OPTIONS)
    public ResponseEntity options() {
        return ResponseEntity.ok().build();
    }

    @ApiOperation("An HEAD operation")
    @RequestMapping(path = "/root/withoutannotation/head", method = RequestMethod.HEAD)
    public String head() {
        return "";
    }

    @ApiOperation(value = "An implicit params operation")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "body", required = true, dataType = "com.benjaminsproule.swagger.gradleplugin.test.model.RequestModel", paramType = "body"),
        @ApiImplicitParam(name = "id", value = "Implicit parameter of primitive type string", dataType = "string", paramType = "header")
    })
    @RequestMapping(path = "/root/withoutannotation/implicitparams", method = RequestMethod.POST)
    public String implicitParams(String requestModel) {
        return "";
    }

    @ApiOperation(value = "A created request operation", code = 201)
    @RequestMapping(path = "/root/withoutannotation/createdrequest", method = RequestMethod.POST)
    public String createdRequest() {
        return "";
    }

    @ApiOperation(value = "A inner JSON sub type operation")
    @RequestMapping(path = "/root/withoutannotation/innerjsonsubtype", method = RequestMethod.GET)
    public OuterJsonSubType innerJsonSubType() {
        return new OuterJsonSubType();
    }

    @ApiOperation("With JsonViewOne specification")
    @GetMapping("/root/withoutannotation/withjsonview1")
    @JsonView(TestJsonViewOne.class)
    public TestJsonViewEntity withJsonViewOne() {
        return null;
    }

    @ApiOperation("With JsonViewTwo specification")
    @GetMapping("/root/withoutannotation/withjsonview2")
    @JsonView(TestJsonViewTwo.class)
    public TestJsonViewEntity withJsonViewTwo() {
        return null;
    }

    @ApiOperation("Entity definition has to contain all possible fields")
    @GetMapping("/root/withoutannotation/withoutjsonview")
    public TestJsonViewEntity withoutJsonView() {
        return null;
    }
}

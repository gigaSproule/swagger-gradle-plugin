package com.benjaminsproule.swagger.gradleplugin.test.jaxrs


import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewEntity
import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewOne
import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewTwo
import com.fasterxml.jackson.annotation.JsonView
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/api/jsonview")
@Api
class JsonViewTestResource {

    @ApiOperation("With JsonViewOne specification")
    @GET
    @Path("/with/1")
    @JsonView(TestJsonViewOne)
    TestJsonViewEntity withJsonViewOne() {
        return null
    }

    @ApiOperation("With JsonViewOne specification")
    @GET
    @Path("/with/2")
    @JsonView(TestJsonViewTwo)
    TestJsonViewEntity withJsonViewTwo() {
        return null
    }

    @ApiOperation("Entity definition has to contain all possible fields")
    @POST
    @Path("/without")
    TestJsonViewEntity withoutJsonView() {
        return null
    }
}

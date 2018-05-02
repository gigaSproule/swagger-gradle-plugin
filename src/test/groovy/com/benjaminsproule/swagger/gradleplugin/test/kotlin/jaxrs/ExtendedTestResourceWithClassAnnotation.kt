package com.benjaminsproule.swagger.gradleplugin.test.kotlin.jaxrs

import io.swagger.annotations.ApiOperation

import javax.ws.rs.GET
import javax.ws.rs.Path

class ExtendedTestResourceWithClassAnnotation : TestResourceWithClassAnnotation() {

    @ApiOperation("An overriden operation description")
    @Path("/overriden")
    @GET
    override fun overriden(): String {
        return ""
    }

    @Path("/overridenWithoutDescription")
    @GET
    override fun overridenWithoutDescription(): String {
        return ""
    }
}

package com.benjaminsproule.swagger.gradleplugin.test.kotlin.jaxrs

import io.swagger.annotations.ApiOperation

import javax.ws.rs.GET
import javax.ws.rs.Path

class ExtendedTestResourceWithoutClassAnnotation : TestResourceWithoutClassAnnotation() {

    @ApiOperation("An overriden operation description")
    @Path("/root/withoutannotation/overriden")
    @GET
    override fun overriden(): String {
        return ""
    }

    @Path("/root/withoutannotation/overridenWithoutDescription")
    @GET
    override fun overridenWithoutDescription(): String {
        return ""
    }
}

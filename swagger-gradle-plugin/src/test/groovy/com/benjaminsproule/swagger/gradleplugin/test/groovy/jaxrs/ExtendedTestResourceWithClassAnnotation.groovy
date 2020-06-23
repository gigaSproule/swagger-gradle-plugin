package com.benjaminsproule.swagger.gradleplugin.test.groovy.jaxrs

import io.swagger.annotations.ApiOperation

import javax.ws.rs.GET
import javax.ws.rs.Path

class ExtendedTestResourceWithClassAnnotation extends TestResourceWithClassAnnotation {

    @ApiOperation('An overriden operation description')
    @Path('/overriden')
    @GET
    @Override
    String overriden() {
        return ''
    }

    @Path('/overridenWithoutDescription')
    @GET
    String overridenWithoutDescription() {
        return ''
    }
}

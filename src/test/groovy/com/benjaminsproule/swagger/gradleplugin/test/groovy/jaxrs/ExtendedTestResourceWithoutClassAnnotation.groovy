package com.benjaminsproule.swagger.gradleplugin.test.groovy.jaxrs

import io.swagger.annotations.ApiOperation

import javax.ws.rs.GET
import javax.ws.rs.Path

class ExtendedTestResourceWithoutClassAnnotation extends TestResourceWithoutClassAnnotation {

    @ApiOperation('An overriden operation description')
    @Path('/root/withoutannotation/overriden')
    @GET
    @Override
    String overriden() {
        return ''
    }

    @Path('/root/withoutannotation/overridenWithoutDescription')
    @GET
    String overridenWithoutDescription() {
        return ''
    }
}

package com.benjaminsproule.swagger.gradleplugin.test.scala.jaxrs

import io.swagger.annotations.ApiOperation
import javax.ws.rs.{GET, Path}

class ExtendedTestResourceWithClassAnnotation extends TestResourceWithClassAnnotation {

    @ApiOperation("An overriden operation description")
    @Path("/overriden")
    @GET
    @Override
    override def overriden(): String = {
        ""
    }

    @Path("/overridenWithoutDescription")
    @GET
    override def overridenWithoutDescription(): String = {
        ""
    }
}

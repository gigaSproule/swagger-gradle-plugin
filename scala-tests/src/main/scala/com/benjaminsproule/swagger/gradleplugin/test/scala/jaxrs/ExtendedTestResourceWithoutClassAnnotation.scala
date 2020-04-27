package com.benjaminsproule.swagger.gradleplugin.test.scala.jaxrs

import io.swagger.annotations.ApiOperation
import javax.ws.rs.{GET, Path}

class ExtendedTestResourceWithoutClassAnnotation extends TestResourceWithoutClassAnnotation {

    @ApiOperation("An overriden operation description")
    @Path("/root/withoutannotation/overriden")
    @GET
    @Override
    override def overriden(): String = {
        ""
    }

    @Path("/root/withoutannotation/overridenWithoutDescription")
    @GET
    override def overridenWithoutDescription(): String = {
        ""
    }
}

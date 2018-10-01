package com.benjaminsproule.sample.scala

import io.swagger.annotations.{Api, ApiOperation}
import javax.ws.rs.{GET, Path, Produces}
import org.springframework.stereotype.Controller

@Controller
@Path("/sample")
@Produces(Array("application/json"))
@Api(value = "/sample", description = "Sample REST for Integration Testing")
class SampleResource {
    @GET
    @ApiOperation(value = "Return hello message", response = classOf[String])
    def home: String = {
        "{\"Hello\": \"World!\"}"
    }
}

package com.benjaminsproule.sample.kotlin

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.stereotype.Controller
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Controller
@Path("/sample")
@Produces("application/json")
@Api(value = "/sample", description = "Sample REST for Integration Testing")
class SampleResource {
    @GET
    @ApiOperation(value = "Return hello message", response = String::class)
    fun home(): String {
        return "{\"Hello\": \"World!\"}"
    }
}

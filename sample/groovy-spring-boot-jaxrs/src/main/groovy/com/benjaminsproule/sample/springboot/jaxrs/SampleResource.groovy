package com.benjaminsproule.sample.springboot.jaxrs

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.Info
import io.swagger.annotations.SwaggerDefinition
import org.springframework.stereotype.Controller

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Controller
@Path("/sample")
@Produces("application/json")
@SwaggerDefinition(info = @Info(title = "OneApiTitle", version = "One"))
@Api(value = "/sample", description = "Sample REST for Integration Testing")
class SampleResource {
    @GET
    @ApiOperation(value = "Return hello message", response = String)
    String home() {
        return "{\"Hello\": \"World!\"}"
    }
}

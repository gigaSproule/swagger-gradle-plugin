package com.benjaminsproule.sample.springboot.mvc;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Controller
@Path("/sample")
@Produces("application/json")
@Api(value = "/sample", description = "Sample REST for Integration Testing")
public class SampleResource {
    @GET
    @ApiOperation(value = "Return hello message", response = String.class)
    public String home() {
        return "{\"Hello\": \"World!\"}";
    }
}

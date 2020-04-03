package com.benjaminsproule.swagger.gradleplugin.test.jaxrs

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.Authorization

// NOTE: This JAX-RS resource will not be in swagger.json as it has no
// swagger annotations. This is different compared to SpringMVC. This
// is just to have a sample for JAX-RS for global available options.

@Path("/api/sample")
public class SampleResource {

    @GET
    public String getSample() {
      return "";
    }

    @POST
    public String postSample() {
      return "";
    }
}

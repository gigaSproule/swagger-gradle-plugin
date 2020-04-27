package com.benjaminsproule.swagger.gradleplugin.test.springmvc

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

@RestController
@RequestMapping("/api/pets")
@Api(tags=["pets"])  // tags take precedence to default creation
public class PetController {

    @GetMapping
    public String getPets() {
      return "";
    }

    @GetMapping(path="/eagles")
    @ApiOperation(value="Get all eagles", tags=["eagles"])  // tags take precendence over default and @Api
    public String getEagles() {
      return "";
    }
}

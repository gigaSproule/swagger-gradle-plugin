package com.benjaminsproule.sample.springboot.mvc;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleResource {

    @RequestMapping(method = RequestMethod.GET, path = "/sample", produces = "application/json")
    @ApiOperation(value = "Return hello message", response = String.class)
    public String home() {
        return "{\"Hello\": \"World!\"}";
    }
}

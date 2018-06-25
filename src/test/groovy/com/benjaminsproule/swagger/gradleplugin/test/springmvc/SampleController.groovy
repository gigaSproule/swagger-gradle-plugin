package com.benjaminsproule.swagger.gradleplugin.test.springmvc

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sample")
public class SampleController {

    @GetMapping
    public String getSample() {
      return "";
    }

    @PostMapping
    public String postSample() {
      return "";
    }
}
package com.benjaminsproule.swagger.gradleplugin.test.springmvc

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/other")
public class OtherController {

    @GetMapping
    public String getOther() {
      return "";
    }

    @PostMapping
    public String createOther() {
      return "";
    }
}

package com.benjaminsproule.swagger.gradleplugin.test.springmvc


import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewEntity
import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewOne
import com.benjaminsproule.swagger.gradleplugin.test.model.TestJsonViewTwo
import com.fasterxml.jackson.annotation.JsonView
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = "JsonView")
@RestController
@RequestMapping("/api/jsonview")
class JsonViewController {

    @ApiOperation("With JsonViewOne specification")
    @GetMapping("/with/1")
    @JsonView(TestJsonViewOne)
    TestJsonViewEntity withJsonViewOne() {
        return null
    }

    @ApiOperation("With JsonViewOne specification")
    @GetMapping("/with/2")
    @JsonView(TestJsonViewTwo)
    TestJsonViewEntity withJsonViewTwo() {
        return null
    }

    @ApiOperation("Entity definition has to contain all possible fields")
    @PostMapping("/without")
    TestJsonViewEntity withoutJsonView() {
        return null
    }


}

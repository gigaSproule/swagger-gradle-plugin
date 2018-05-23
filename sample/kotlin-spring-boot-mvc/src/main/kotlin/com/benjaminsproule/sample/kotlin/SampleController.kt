package com.benjaminsproule.sample.kotlin

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(value = "/sample", description = "Sample REST for Integration Testing")
class SampleController {

    @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf("/sample"), produces = arrayOf("application/json"))
    @ApiOperation(value = "Return hello message", response = String::class)
    fun home(): String {
        return "{\"Hello\": \"World!\"}"
    }
}

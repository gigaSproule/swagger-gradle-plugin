package com.benjaminsproule.sample.scala

import io.swagger.annotations.{Api, ApiOperation}
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}

@RestController
@Api(value = "/sample", description = "Sample REST for Integration Testing")
class SampleController {

    @RequestMapping(method = Array(RequestMethod.GET), path = Array("/sample"), produces = Array("application/json"))
    @ApiOperation(value = "Return hello message", response = classOf[String])
    def home: String = {
        "{\"Hello\": \"World!\"}"
    }
}

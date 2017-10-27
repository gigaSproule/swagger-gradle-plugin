package com.benjaminsproule.sample.kotlin

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "Sample", value = "/sample")
@Api(value = "/sample", description = "Sample REST for Integration Testing")
class HomeController : HttpServlet() {

    @ApiOperation(value = "Return hello message", response = String::class)
    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        res.writer.write("{\"Hello\": \"World!\"}")
    }
}

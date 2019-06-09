package com.benjaminsproule.swagger.gradleplugin.test.springmvc.model

import com.fasterxml.jackson.annotation.JsonView

class TestJsonViewEntity {
    @JsonView(TestJsonViewOne)
    Object viewValue
    Object requiredValue
}

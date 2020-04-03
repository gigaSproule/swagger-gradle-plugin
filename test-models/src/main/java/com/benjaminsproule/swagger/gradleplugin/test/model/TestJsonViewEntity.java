package com.benjaminsproule.swagger.gradleplugin.test.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public class TestJsonViewEntity {
    @JsonView(TestJsonViewOne.class)
    private Object viewValue;
    private Object requiredValue;
}

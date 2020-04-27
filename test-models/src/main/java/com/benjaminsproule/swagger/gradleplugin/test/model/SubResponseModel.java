package com.benjaminsproule.swagger.gradleplugin.test.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubResponseModel extends ResponseModel {
    private String value;
}


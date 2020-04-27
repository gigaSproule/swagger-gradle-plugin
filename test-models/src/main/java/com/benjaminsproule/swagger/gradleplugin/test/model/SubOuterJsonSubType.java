package com.benjaminsproule.swagger.gradleplugin.test.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubOuterJsonSubType extends OuterJsonSubType {
    private String subValue;
}

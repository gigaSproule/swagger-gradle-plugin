package com.benjaminsproule.swagger.gradleplugin.test.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubInnerJsonSubType extends InnerJsonSubType {
    private String subValue;
}

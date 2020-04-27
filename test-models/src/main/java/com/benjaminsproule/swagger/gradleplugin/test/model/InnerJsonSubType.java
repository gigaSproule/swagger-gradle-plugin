package com.benjaminsproule.swagger.gradleplugin.test.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__innerType")
@JsonSubTypes(
    @JsonSubTypes.Type(value = SubInnerJsonSubType.class, name = "SubInnerJsonSubType")
)
public class InnerJsonSubType {
    private String value;
}

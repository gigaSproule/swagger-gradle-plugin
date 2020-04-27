package com.benjaminsproule.swagger.gradleplugin.test.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__outerType")
@JsonSubTypes(
    @JsonSubTypes.Type(value = SubOuterJsonSubType.class, name = "SubOuterJsonType")
)
public class OuterJsonSubType {
    private InnerJsonSubType innerJsonSubType;
}

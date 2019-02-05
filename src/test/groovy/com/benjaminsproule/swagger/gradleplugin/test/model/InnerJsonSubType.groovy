package com.benjaminsproule.swagger.gradleplugin.test.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__innerType")
@JsonSubTypes([
    @JsonSubTypes.Type(value = SubInnerJsonSubType.class, name = "SubInnerJsonSubType")
])
class InnerJsonSubType {
    String value
}

package com.benjaminsproule.swagger.gradleplugin.test.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__outerType")
@JsonSubTypes([
    @JsonSubTypes.Type(value = SubOuterJsonSubType.class, name = "SubOuterJsonType")
])
class OuterJsonSubType {
    InnerJsonSubType innerJsonSubType
}

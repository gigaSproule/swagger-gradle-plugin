package com.benjaminsproule.swagger.gradleplugin.example

import com.fasterxml.jackson.annotation.JsonRawValue

abstract class PropertyExampleMixIn {
    PropertyExampleMixIn() {}

    @JsonRawValue
    abstract String getExample()
}

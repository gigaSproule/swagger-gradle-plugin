package com.benjaminsproule.swagger.gradleplugin.validator

interface ModelValidator<T> {
    List<String> isValid(T t)
}

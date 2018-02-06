package com.benjaminsproule.swagger.gradleplugin.reader

import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import io.swagger.models.Swagger

interface ClassSwaggerReader {
    Swagger read(Set<Class<?>> classes) throws GenerateException
}

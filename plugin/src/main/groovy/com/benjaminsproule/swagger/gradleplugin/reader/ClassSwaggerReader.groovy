package com.benjaminsproule.swagger.gradleplugin.reader

import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import io.swagger.models.Swagger

interface ClassSwaggerReader {
    Swagger read() throws GenerateException
}

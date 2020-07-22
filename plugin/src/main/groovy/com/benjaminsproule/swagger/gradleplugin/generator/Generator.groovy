package com.benjaminsproule.swagger.gradleplugin.generator

import io.swagger.models.Swagger

interface Generator {
    void generate(Swagger source)
}

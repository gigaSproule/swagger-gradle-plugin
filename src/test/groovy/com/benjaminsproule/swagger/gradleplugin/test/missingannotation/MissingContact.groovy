package com.benjaminsproule.swagger.gradleplugin.test.missingannotation

import io.swagger.annotations.Info
import io.swagger.annotations.License
import io.swagger.annotations.SwaggerDefinition
import io.swagger.annotations.Tag

@SwaggerDefinition(host = "http://annotated",
    basePath = "/annotated",
    tags = [
        @Tag(name = "Test", description = "Test tag description")
    ],
    info = @Info(title = "annotated",
        version = "annotated",
        description = "annotated description",
        termsOfService = "annotated ToS",
        license = @License(name = "annotated", url = "http://licence")))
class MissingContact {
}

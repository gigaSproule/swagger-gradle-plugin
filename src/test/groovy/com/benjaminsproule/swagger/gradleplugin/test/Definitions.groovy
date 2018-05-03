package com.benjaminsproule.swagger.gradleplugin.test

import io.swagger.annotations.*

@SwaggerDefinition(host = "http://annotated",
    basePath = "/annotated",
    tags =
        @Tag(name = "Test", description = "Test tag description")
    ,
    info = @Info(title = "annotated",
        version = "annotated",
        description = "annotated description",
        termsOfService = "annotated ToS",
        license = @License(name = "annotated", url = "http://licence"),
        contact = @Contact(name = "annotated", email = "annotated@contact.me")))
class Definitions {
}

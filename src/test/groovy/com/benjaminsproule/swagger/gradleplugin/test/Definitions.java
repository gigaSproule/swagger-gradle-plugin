package com.benjaminsproule.swagger.gradleplugin.test;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@SwaggerDefinition(host = "http://annotated",
    basePath = "/annotated",
    tags = {
        @Tag(name = "Test", description = "Test tag description")
    },
    info = @Info(title = "annotated",
        version = "annotated",
        description = "annotated description",
        termsOfService = "annotated ToS",
        license = @License(name = "annotated", url = "http://licence"),
        contact = @Contact(name = "annotated", email = "annotated@contact.me")))
public class Definitions {
}

package com.benjaminsproule.swagger.gradleplugin.swagger

import io.swagger.annotations.*

@SwaggerDefinition(
    host = 'Host',
    basePath = 'BasePath',
    tags = [
        @Tag(name = ''),
        @Tag(name = 'TagName 1', description = 'TagDescription 1', externalDocs = @ExternalDocs(value = 'ExternalDocsDescription', url = 'ExternalDocsUrl')),
        @Tag(name = 'TagName 2', description = 'TagDescription 2'),
        @Tag(name = '')
    ],
    info = @Info(title = 'InfoTitle', version = 'InfoVersion', description = 'InfoDescription', termsOfService = 'InfoTermsOfService',
        license = @License(name = 'LicenseName', url = 'LicenseUrl'),
        contact = @Contact(name = 'ContactName', url = 'ContactUrl', email = 'ContactEmail')
    ))
class SwaggerDefinitionAnnotated {
}

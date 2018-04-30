package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import org.gradle.api.internal.ClosureBackedAction

class JaxrsPluginOutputTest extends AbstractPluginOutputTest {

    protected ClosureBackedAction<SwaggerExtension> getSwaggerExtensionClosure(String expectedSwaggerDirectory) {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule']
                    schemes = ['http']
                    info {
                        title = project.name
                        version = '1'
                        license {
                            name = 'Apache 2.0'
                        }
                        contact {
                            name = 'Joe Blogs'
                        }
                    }
                    swaggerDirectory = expectedSwaggerDirectory
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                }
            }
        )
    }
}

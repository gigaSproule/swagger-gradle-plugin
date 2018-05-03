package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import org.gradle.api.internal.ClosureBackedAction

class JaxrsPluginOutputTest extends AbstractPluginOutputTest {

    @Override
    ClosureBackedAction<SwaggerExtension> getGroovySwaggerExtensionClosure(String expectedSwaggerDirectory) {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
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

    @Override
    ClosureBackedAction<SwaggerExtension> getJavaSwaggerExtensionClosure(String expectedSwaggerDirectory) {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.java']
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

    @Override
    ClosureBackedAction<SwaggerExtension> getKotlinSwaggerExtensionClosure(String expectedSwaggerDirectory) {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.kotlin']
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

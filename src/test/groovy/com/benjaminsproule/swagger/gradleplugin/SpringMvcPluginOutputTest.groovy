package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import org.gradle.api.internal.ClosureBackedAction

class SpringMvcPluginOutputTest extends AbstractPluginOutputTest {

    @Override
    ClosureBackedAction<SwaggerExtension> getGroovySwaggerExtensionClosure(String expectedSwaggerDirectory) {
        new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.groovy']
                    springmvc = true
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
                    springmvc = true
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
                    springmvc = true
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

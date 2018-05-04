package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import org.apache.commons.text.RandomStringGenerator
import org.gradle.api.internal.ClosureBackedAction
import org.junit.Test

class ApiDocsITest extends AbstractPluginITest {

    @Test
    void useSpecifiedTemplatesToGenerateSwaggerDocs() {
        def randomizer = new RandomStringGenerator.Builder().build().generate(5)
        //Template path is not actually a template path but a template file and it propbably has to be the root
        def expectedSwaggerDocsDirectory = "${project.buildDir}/swaggerdocs-${randomizer}"
        def expectedSwaggerApiDocsFile = "${expectedSwaggerDocsDirectory}/api.html"
        def templatePathValue = "classpath:/api-doc-template/strapdown.html.hbs"
        project.extensions.configure(SwaggerExtension, new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule']
                    schemes = ['http']
                    info {
                        title = project.name
                        version = '1'
                        termsOfService = 'http://localhost/tos'
                        description = 'The Api description'
                        license {
                            name = 'Apache 2.0'
                            url = 'http://localhost/license'
                        }
                        contact {
                            name = 'Joe Blogs'
                            email = 'joe.blogs@fake.com'
                        }
                    }
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                        type = 'basic'
                    }
                    templatePath = templatePathValue
                    outputPath = expectedSwaggerApiDocsFile
                }
            }
        ))

        project.tasks.generateSwaggerDocumentation.execute()

        def swaggerDir = new File(expectedSwaggerApiDocsFile)
        assert swaggerDir
    }
}

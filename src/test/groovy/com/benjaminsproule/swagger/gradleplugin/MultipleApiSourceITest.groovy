package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.model.SwaggerExtension
import groovy.json.JsonSlurper
import org.gradle.api.internal.ClosureBackedAction
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized)
class MultipleApiSourceITest extends AbstractPluginITest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Parameterized.Parameters
    static Collection<ClosureBackedAction<SwaggerExtension>> data() {
        [
            getSpringMvcSwaggerExtensionClosure(),
            getJaxRsSwaggerExtensionClosure()
        ]
    }

    private ClosureBackedAction<SwaggerExtension> swaggerExtensionClosure

    MultipleApiSourceITest(ClosureBackedAction<SwaggerExtension> swaggerExtensionClosure) {
        this.swaggerExtensionClosure = swaggerExtensionClosure
    }

    @Test
    void producesSwaggerDocumentation() {
        def expectedSwaggerDirectory = temporaryFolder.newFolder()
        def expectedSwaggerFiles = ["swagger-one", "swagger-two"]
        project.extensions.configure(SwaggerExtension, swaggerExtensionClosure)
        project.extensions.getByType(SwaggerExtension).apiSourceExtensions.eachWithIndex { apiSourceExtension, i ->
            apiSourceExtension.swaggerDirectory = expectedSwaggerDirectory
            apiSourceExtension.swaggerFileName = expectedSwaggerFiles[i]
        }

        project.tasks.generateSwaggerDocumentation.execute()

        assertSwaggerJson("${expectedSwaggerDirectory}/${expectedSwaggerFiles[0]}.json", 'One')
        assertSwaggerJson("${expectedSwaggerDirectory}/${expectedSwaggerFiles[1]}.json", 'Two')
    }

    private static void assertSwaggerJson(String swaggerJsonFile, String prefix) {
        def producedSwaggerDocument = new JsonSlurper().parse(new File(swaggerJsonFile), 'UTF-8')

        assert producedSwaggerDocument.swagger == '2.0'
        assert producedSwaggerDocument.basePath == "${prefix}"

        def info = producedSwaggerDocument.info
        assert info
        assert info.version == prefix
        assert info.title == "${prefix}ApiTitle"

        def paths = producedSwaggerDocument.get('paths')
        assert paths
        assert paths.size() == 1

        assert paths."/${prefix}Api".get.responses.'200'.schema.'$ref' == "#/definitions/MultiApiSourceParent${prefix}ResponseModel"
    }

    private static ClosureBackedAction<SwaggerExtension> getSpringMvcSwaggerExtensionClosure() {
        return new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.springmvc.TestResourceForMultiApiSource_One']
                    springmvc = true
                    schemes = ['http']
                    basePath = 'One'
                }
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.springmvc.TestResourceForMultiApiSource_Two']
                    springmvc = true
                    schemes = ['http']
                    basePath = 'Two'
                }
            }
        )
    }

    private static ClosureBackedAction<SwaggerExtension> getJaxRsSwaggerExtensionClosure() {
        return new ClosureBackedAction<SwaggerExtension>(
            {
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.jaxrs.TestResourceForMultiApiSource_One']
                    schemes = ['http']
                    basePath = 'One'
                }
                apiSource {
                    locations = ['com.benjaminsproule.swagger.gradleplugin.test.multipleapisource.jaxrs.TestResourceForMultiApiSource_Two']
                    schemes = ['http']
                    basePath = 'Two'
                }
            }
        )
    }
}

package com.benjaminsproule.swagger.gradleplugin.model

import org.junit.Before
import org.junit.Test

class SwaggerExtensionTest {
    SwaggerExtension swaggerExtension

    @Before
    void setup() {
        swaggerExtension = new SwaggerExtension(null)
    }

    @Test
    void 'Valid swagger extension validation returns no errors'() {
        swaggerExtension.apiSourceExtensions.push([isValid: { new ArrayList<>()} ] as ApiSourceExtension)

        def result = swaggerExtension.isValid()

        assert !result
    }

    @Test
    void 'Swagger extension with missing api source should provide missing api source error'() {
        def result = swaggerExtension.isValid()

        assert result
        assert result.contains('You must specify at least one apiSource element')
    }

    @Test
    void 'Swagger extension empty api source should provide missing api source error'() {
        swaggerExtension.apiSourceExtensions = []
        def result = swaggerExtension.isValid()

        assert result
        assert result.contains('You must specify at least one apiSource element')
    }

    @Test
    void 'Errors from nested objects should be returned'() {
        swaggerExtension.apiSourceExtensions.push([isValid: {['nested error']}] as ApiSourceExtension)

        def result = swaggerExtension.isValid()

        assert result
        assert result.contains('nested error')
    }
}

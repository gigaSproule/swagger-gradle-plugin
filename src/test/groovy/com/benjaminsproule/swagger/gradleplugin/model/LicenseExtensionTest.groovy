package com.benjaminsproule.swagger.gradleplugin.model

import org.junit.Before
import org.junit.Test

class LicenseExtensionTest {
    LicenseExtension licenseExtension

    @Before
    void setup() {
        licenseExtension = new LicenseExtension()
    }

    @Test
    void 'Valid licence validation returns no errors'() {
        licenseExtension.name = 'name'
        def result = licenseExtension.isValid()

        assert !result
    }

    @Test
    void 'Swagger extension with missing name should provide missing name error'() {
        def result = licenseExtension.isValid()

        assert result
        assert result.contains('info.licence.name is required by the swagger spec')
    }
}

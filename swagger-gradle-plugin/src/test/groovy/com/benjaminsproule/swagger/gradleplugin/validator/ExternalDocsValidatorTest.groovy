package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ExternalDocsExtension
import spock.lang.Specification

class ExternalDocsValidatorTest extends Specification {

    def 'isValid returns empty list if externalDocsExtension not provided'() {
        when:
        def errors = new ExternalDocsValidator().isValid(null)

        then:
        errors.size() == 0
    }

    def 'isValid returns error message if url not set'() {
        when:
        def errors = new ExternalDocsValidator().isValid(new ExternalDocsExtension())

        then:
        errors.size() == 1
        errors[0] == 'tag.externalDocs.url is required by the swagger spec'
    }

    def 'isValid returns empty list if fields are set'() {
        given:
        def externalDocsExtension = new ExternalDocsExtension()
        externalDocsExtension.url = 'url'


        when:
        def errors = new ExternalDocsValidator().isValid(externalDocsExtension)

        then:
        errors.size() == 0
    }
}

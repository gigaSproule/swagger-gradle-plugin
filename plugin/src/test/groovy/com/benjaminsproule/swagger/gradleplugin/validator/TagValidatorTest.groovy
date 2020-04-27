package com.benjaminsproule.swagger.gradleplugin.validator

import com.benjaminsproule.swagger.gradleplugin.model.ExternalDocsExtension
import com.benjaminsproule.swagger.gradleplugin.model.TagExtension
import spock.lang.Specification

class TagValidatorTest extends Specification {

    private ExternalDocsValidator mockExternalDocsValidator
    private TagValidator tagValidator

    def setup() {
        mockExternalDocsValidator = Mock(ExternalDocsValidator)
        tagValidator = new TagValidator(mockExternalDocsValidator)
    }

    def 'isValid returns empty list if tagExtension not provided'() {
        given:
        0 * mockExternalDocsValidator.isValid(_) >> []

        when:
        def errors = tagValidator.isValid(null)

        then:
        errors.size() == 0
    }

    def 'isValid returns error message if name not set'() {
        given:
        1 * mockExternalDocsValidator.isValid(_) >> []

        when:
        def errors = tagValidator.isValid(new TagExtension())

        then:
        errors.size() == 1
        errors[0] == 'tag.name is required by the swagger spec'
    }

    def 'isValid returns empty list if fields are set'() {
        given:
        def tagExtension = new TagExtension()
        tagExtension.name = 'name'

        1 * mockExternalDocsValidator.isValid(_) >> []

        when:
        def errors = tagValidator.isValid(tagExtension)

        then:
        errors.size() == 0
    }

    def 'Returns errors from other validators'() {
        given:
        def tagExtension = new TagExtension()
        tagExtension.name = 'name'
        tagExtension.externalDocs = new ExternalDocsExtension()

        1 * mockExternalDocsValidator.isValid(_) >> ['external docs validator error 1', 'external docs validator error 2']

        when:
        def errors = tagValidator.isValid(tagExtension)

        then:
        errors.size() == 2
        errors[0] == 'external docs validator error 1'
        errors[1] == 'external docs validator error 2'
    }
}

package com.benjaminsproule.swagger.gradleplugin

import io.swagger.models.Operation
import io.swagger.models.Path
import io.swagger.models.Response
import io.swagger.models.Swagger
import spock.lang.Specification

class UtilsTest extends Specification {

    def 'Sort Swagger paths'() {
        given:
        def swagger = new Swagger()
        swagger.path('path2', new Path())
        swagger.path('path1', new Path())
        assert swagger.getPaths().keySet()[0] == 'path2'
        assert swagger.getPaths().keySet()[1] == 'path1'

        when:
        Utils.sortSwagger(swagger)

        then:
        assert swagger.getPaths().keySet()[0] == 'path1'
        assert swagger.getPaths().keySet()[1] == 'path2'
    }

    def 'Sort Swagger responses'() {
        given:
        def swagger = new Swagger()
        def path = new Path()
        def operation = new Operation()
        operation.response(201, new Response())
        operation.response(200, new Response())
        path.setGet(operation)
        swagger.path('path', path)
        assert swagger.getPath('path').get.responses.keySet()[0] == '201'
        assert swagger.getPath('path').get.responses.keySet()[1] == '200'

        when:
        Utils.sortSwagger(swagger)

        then:
        assert swagger.getPath('path').get.responses.keySet()[0] == '200'
        assert swagger.getPath('path').get.responses.keySet()[1] == '201'
    }
}

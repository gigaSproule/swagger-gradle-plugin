package com.benjaminsproule.swagger.gradleplugin.reader

import com.sun.jersey.api.core.InjectParam
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart

import javax.ws.rs.BeanParam
import javax.ws.rs.FormParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import java.lang.annotation.Annotation
import java.lang.reflect.Type

enum ParameterAnnotations {
    MODEL_ATTRIBUTE(ModelAttribute),
    BEAN_PARAM(BeanParam),
    INJECT_PARAM(InjectParam),
    API_PARAM(ApiParam),
    PATH_PARAM(PathParam),
    QUERY_PARAM(QueryParam),
    HEADER_PARAM(HeaderParam),
    FORM_PARAM(FormParam),
    REQUEST_PARAM(RequestParam),
    REQUEST_BODY(RequestBody),
    PATH_VARIABLE(PathVariable),
    REQUEST_HEADER(RequestHeader),
    REQUEST_PART(RequestPart),
    COOKIE_VALUE(CookieValue)

    Type annotationType

    ParameterAnnotations(Type annotationType) {
        this.annotationType = annotationType
    }

    static boolean inValidParameterAnnotation(Class<? extends Annotation> potentialAnnotation) {
        return values().annotationType.contains(potentialAnnotation)
    }
}

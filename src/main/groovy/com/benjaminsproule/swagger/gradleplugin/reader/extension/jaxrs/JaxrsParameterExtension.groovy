package com.benjaminsproule.swagger.gradleplugin.reader.extension.jaxrs

import io.swagger.converter.ModelConverters
import io.swagger.jaxrs.ext.AbstractSwaggerExtension
import io.swagger.jaxrs.ext.SwaggerExtension
import io.swagger.models.parameters.CookieParameter
import io.swagger.models.parameters.FormParameter
import io.swagger.models.parameters.HeaderParameter
import io.swagger.models.parameters.Parameter
import io.swagger.models.parameters.PathParameter
import io.swagger.models.parameters.QueryParameter
import io.swagger.models.parameters.SerializableParameter
import io.swagger.models.properties.Property

import javax.ws.rs.CookieParam
import javax.ws.rs.DefaultValue
import javax.ws.rs.FormParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import java.lang.annotation.Annotation
import java.lang.reflect.Type

class JaxrsParameterExtension extends AbstractSwaggerExtension {

    @Override
    List<Parameter> extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip, Iterator<SwaggerExtension> chain) {
        if (this.shouldIgnoreType(type, typesToSkip)) {
            return new ArrayList<>()
        }

        List<Parameter> parameters = new ArrayList<>()
        SerializableParameter parameter = null
        for (Annotation annotation : annotations) {
            parameter = getParameter(type, parameter, annotation)
        }
        if (parameter != null) {
            parameters.add(parameter)
        }

        return parameters
    }

    static SerializableParameter getParameter(Type type, SerializableParameter parameter, Annotation annotation) {
        String defaultValue = ""
        if (annotation instanceof DefaultValue) {
            DefaultValue defaultValueAnnotation = (DefaultValue) annotation
            defaultValue = defaultValueAnnotation.value()
        }

        if (annotation instanceof QueryParam) {
            QueryParam param = (QueryParam) annotation
            QueryParameter queryParameter = new QueryParameter().name(param.value())

            if (!defaultValue.isEmpty()) {
                queryParameter.setDefaultValue(defaultValue)
            }
            Property schema = ModelConverters.getInstance().readAsProperty(type)
            if (schema != null) {
                queryParameter.setProperty(schema)
            }

            String parameterType = queryParameter.getType()
            if (parameterType == null || parameterType.equals("ref")) {
                queryParameter.setType("string")
            }
            parameter = queryParameter
        } else if (annotation instanceof PathParam) {
            PathParam param = (PathParam) annotation
            PathParameter pathParameter = new PathParameter().name(param.value())
            if (!defaultValue.isEmpty()) {
                pathParameter.setDefaultValue(defaultValue)
            }
            Property schema = ModelConverters.getInstance().readAsProperty(type)
            if (schema != null) {
                pathParameter.setProperty(schema)
            }

            String parameterType = pathParameter.getType()
            if (parameterType == null || parameterType.equals("ref")) {
                pathParameter.setType("string")
            }
            parameter = pathParameter
        } else if (annotation instanceof HeaderParam) {
            HeaderParam param = (HeaderParam) annotation
            HeaderParameter headerParameter = new HeaderParameter().name(param.value())
            headerParameter.setDefaultValue(defaultValue)
            Property schema = ModelConverters.getInstance().readAsProperty(type)
            if (schema != null) {
                headerParameter.setProperty(schema)
            }

            String parameterType = headerParameter.getType()
            if (parameterType == null || parameterType.equals("ref") || parameterType.equals("array")) {
                headerParameter.setType("string")
            }
            parameter = headerParameter
        } else if (annotation instanceof CookieParam) {
            CookieParam param = (CookieParam) annotation
            CookieParameter cookieParameter = new CookieParameter().name(param.value())
            if (!defaultValue.isEmpty()) {
                cookieParameter.setDefaultValue(defaultValue)
            }
            Property schema = ModelConverters.getInstance().readAsProperty(type)
            if (schema != null) {
                cookieParameter.setProperty(schema)
            }

            String parameterType = cookieParameter.getType()
            if (parameterType == null || parameterType.equals("ref") || parameterType.equals("array")) {
                cookieParameter.setType("string")
            }
            parameter = cookieParameter
        } else if (annotation instanceof FormParam) {
            FormParam param = (FormParam) annotation
            FormParameter formParameter = new FormParameter().name(param.value())
            if (!defaultValue.isEmpty()) {
                formParameter.setDefaultValue(defaultValue)
            }
            Property schema = ModelConverters.getInstance().readAsProperty(type)
            if (schema != null) {
                formParameter.setProperty(schema)
            }

            String parameterType = formParameter.getType()
            if (parameterType == null || parameterType.equals("ref") || parameterType.equals("array")) {
                formParameter.setType("string")
            }
            parameter = formParameter
        }

        return parameter
    }
}

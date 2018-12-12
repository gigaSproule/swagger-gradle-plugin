package com.benjaminsproule.swagger.gradleplugin.reader

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.swagger.SwaggerFactory
import io.swagger.annotations.*
import io.swagger.converter.ModelConverters
import io.swagger.jaxrs.ext.SwaggerExtension
import io.swagger.jaxrs.ext.SwaggerExtensions
import io.swagger.models.*
import io.swagger.models.parameters.*
import io.swagger.models.properties.ArrayProperty
import io.swagger.models.properties.MapProperty
import io.swagger.models.properties.Property
import io.swagger.models.properties.RefProperty
import io.swagger.util.ParameterProcessor
import io.swagger.util.PathUtils
import io.swagger.util.PrimitiveType
import io.swagger.util.ReflectionUtils
import org.apache.commons.lang3.reflect.TypeUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationUtils

import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.lang.reflect.Type

abstract class AbstractReader implements ClassSwaggerReader {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractReader)
    protected ApiSourceExtension apiSource
    protected Set<Type> typesToSkip
    protected ClassFinder classFinder
    protected Swagger swagger

    AbstractReader(ApiSourceExtension apiSource, Set<Type> typesToSkip, List<SwaggerExtension> swaggerExtensions, ClassFinder classFinder) {
        this.apiSource = apiSource
        this.typesToSkip = typesToSkip ?: []
        this.classFinder = classFinder
        this.swagger = new SwaggerFactory(classFinder).swagger(apiSource)

        swaggerExtensions = swaggerExtensions ?: []
        SwaggerExtensions.setExtensions(swaggerExtensions + customSwaggerExtensions())
    }

    /**
     * Method which allows sub-classes to modify the Swagger extension chain.
     * @param swaggerExtensions user provided swagger extensions
     */
    protected abstract List<SwaggerExtension> customSwaggerExtensions()

    protected static List<SecurityRequirement> getSecurityRequirements(Api api) {
        List<SecurityRequirement> securities = []
        if (api == null) {
            return securities
        }

        for (Authorization auth : api.authorizations()) {
            if (auth.value().isEmpty()) {
                continue
            }
            SecurityRequirement security = new SecurityRequirement()
            security.setName(auth.value())
            for (AuthorizationScope scope : auth.scopes()) {
                if (!scope.scope().isEmpty()) {
                    security.addScope(scope.scope())
                }
            }
            securities += security
        }
        return securities
    }

    protected static String parseOperationPath(String operationPath, Map<String, String> regexMap) {
        return PathUtils.parsePath(operationPath, regexMap)
    }

    protected
    static void updateOperationParameters(List<Parameter> parentParameters, Map<String, String> regexMap, Operation operation) {
        if (parentParameters != null) {
            for (Parameter param : parentParameters) {
                operation.parameter(param)
            }
        }
        for (Parameter param : operation.getParameters()) {
            String pattern = regexMap.get(param.getName())
            if (pattern != null) {
                param.setPattern(pattern)
            }
        }
    }

    protected static Map<String, Property> parseResponseHeaders(ResponseHeader[] headers) {
        if (headers == null) {
            return null
        }
        Map<String, Property> responseHeaders = null
        for (ResponseHeader header : headers) {
            if (header.name().isEmpty()) {
                continue
            }
            if (responseHeaders == null) {
                responseHeaders = [:]
            }
            Class<?> cls = header.response()

            if (cls != Void && cls != void) {
                Property property = ModelConverters.getInstance().readAsProperty(cls)
                if (property != null) {
                    Property responseProperty

                    if (header.responseContainer().equalsIgnoreCase("list")) {
                        responseProperty = new ArrayProperty(property)
                    } else if (header.responseContainer().equalsIgnoreCase("map")) {
                        responseProperty = new MapProperty(property)
                    } else {
                        responseProperty = property
                    }
                    responseProperty.setDescription(header.description())
                    responseHeaders.put(header.name(), responseProperty)
                }
            }
        }
        return responseHeaders
    }

    protected static Set<Map<String, Object>> parseCustomExtensions(Extension[] extensions) {
        if (extensions == null) {
            return Collections.emptySet()
        }
        Set<Map<String, Object>> resultSet = new HashSet<Map<String, Object>>()
        for (Extension extension : extensions) {
            if (extension == null) {
                continue
            }
            Map<String, Object> extensionProperties = [:]
            for (ExtensionProperty extensionProperty : extension.properties()) {
                String name = extensionProperty.name()
                if (!name.isEmpty()) {
                    String value = extensionProperty.value()
                    extensionProperties.put(name, value)
                }
            }
            if (!extension.name().isEmpty()) {
                Map<String, Object> wrapper = [:]
                wrapper.put(extension.name(), extensionProperties)
                resultSet += wrapper
            } else {
                resultSet += extensionProperties
            }
        }
        return resultSet
    }

    protected void updatePath(String operationPath, String httpMethod, Operation operation) {
        if (httpMethod == null) {
            return
        }
        Path path = swagger.getPath(operationPath)
        if (path == null) {
            path = new Path()
            swagger.path(operationPath, path)
        }
        path.set(httpMethod, operation)
    }

    protected void updateTagsForOperation(Operation operation, ApiOperation apiOperation) {
        if (apiOperation == null) {
            return
        }
        for (String tag : apiOperation.tags()) {
            if (!tag.isEmpty()) {
                operation.tag(tag)
                swagger.tag(new io.swagger.models.Tag().name(tag))
            }
        }
    }

    protected static boolean isApiUnreadable(boolean readHidden, Api api) {
        return !api || readHidden || api.hidden()
    }

    protected static Set<io.swagger.models.Tag> extractTags(Api api) {
        def output = new LinkedHashSet<io.swagger.models.Tag>()
        if (!api) {
            return output
        }

        api.tags().each { String tag ->
            if (tag) {
                output += new io.swagger.models.Tag().name(tag)
            }
        }

        //If no tags were found we shall derive them rom the api path
        if (!output) {
            def tagString = api.value().replace("/", "")
            if (tagString) {
                output += new io.swagger.models.Tag().name(tagString)
            }
        }
        return output
    }

    protected static void updateOperationProtocols(ApiOperation apiOperation, Operation operation) {
        if (!apiOperation) {
            return
        }
        def protocols = apiOperation.protocols().split(',')
        for (String protocol : protocols) {
            def trimmed = protocol.trim()
            if (trimmed) {
                operation.scheme(Scheme.forValue(trimmed))
            }
        }
    }

    protected Map<String, io.swagger.models.Tag> updateTagsForApi(Map<String, io.swagger.models.Tag> parentTags, Api api) {
        // the value will be used as a tag for 2.0 UNLESS a Tags annotation is present
        Map<String, io.swagger.models.Tag> tagsMap = [:]
        for (io.swagger.models.Tag tag : extractTags(api)) {
            tagsMap.put(tag.getName(), tag)
        }
        if (parentTags) {
            tagsMap.putAll(parentTags)
        }
        for (io.swagger.models.Tag tag : tagsMap.values()) {
            swagger.tag(tag)
        }
        return tagsMap
    }

    protected static boolean isPrimitive(Type cls) {
        def property = ModelConverters.getInstance().readAsProperty(cls)
        if (property) {
            switch (property.type) {
                case 'integer':
                    return true
                case 'string':
                    return true
                case 'number':
                    return true
                case 'boolean':
                    return true
                case 'array':
                    return true
                case 'file':
                    return true
            }
        }

        return false
    }

    protected static void updateOperation(String[] apiConsumes,
                                          String[] apiProduces,
                                          Map<String, io.swagger.models.Tag> tags,
                                          List<SecurityRequirement> securities,
                                          Operation operation) {
        if (operation) {
            if (operation.getConsumes() == null) {
                for (String mediaType : apiConsumes) {
                    operation.consumes(mediaType)
                }
            }
            if (operation.getProduces() == null) {
                for (String mediaType : apiProduces) {
                    operation.produces(mediaType)
                }
            }

            if (operation.getTags() == null) {
                for (String tagString : tags.keySet()) {
                    operation.tag(tagString)
                }
            }
            for (SecurityRequirement security : securities) {
                operation.security(security)
            }
        }

    }

    private static boolean isApiParamHidden(List<Annotation> parameterAnnotations) {
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation instanceof ApiParam) {
                return ((ApiParam) parameterAnnotation).hidden()
            }
        }

        return false
    }

    private static boolean hasValidAnnotations(List<Annotation> parameterAnnotations) {
        // Because method parameters can contain parameters that are valid, but
        // not part of the API contract, first check to make sure the parameter
        // has at lease one annotation before processing it.  Also, check a
        // whitelist to make sure that the annotation of the parameter is
        // compatible with spring-maven-plugin

        def hasValidAnnotation = false
        for (Annotation potentialAnnotation : parameterAnnotations) {
            if (ParameterAnnotations.inValidParameterAnnotation(potentialAnnotation.annotationType())) {
                hasValidAnnotation = true
            }
        }

        return hasValidAnnotation
    }

    protected List<Parameter> getParameters(Type type, List<Annotation> annotations) {
        if (!hasValidAnnotations(annotations) || isApiParamHidden(annotations)) {
            return Collections.emptyList()
        }

        Iterator<SwaggerExtension> chain = SwaggerExtensions.chain()
        List<Parameter> parameters = []

        def cls = TypeUtils.getRawType(type, type)
        LOG.debug("Looking for path/query/header/form/cookie params in ${cls}")

        //FIXME this code is confusing because there are many things calling next on the chain
        if (chain.hasNext()) {
            SwaggerExtension extension = chain.next()
            LOG.debug("trying extension " + extension)
            parameters = extension.extractParameters(annotations, type, typesToSkip, chain)
        }

        if (!parameters.isEmpty()) {
            for (Parameter parameter : parameters) {
                ParameterProcessor.applyAnnotations(swagger, parameter, type, annotations)
            }
        } else {
            LOG.debug("Looking for body params in ${cls}")
            if (!typesToSkip.contains(type)) {
                Parameter param = ParameterProcessor.applyAnnotations(swagger, null, type, annotations)
                if (param != null) {
                    parameters += param
                }
            }
        }
        return parameters
    }

    protected void updateApiResponse(Operation operation, ApiResponses responseAnnotation) {
        for (ApiResponse apiResponse : responseAnnotation.value()) {
            Map<String, Property> responseHeaders = parseResponseHeaders(apiResponse.responseHeaders())
            Class<?> responseClass = apiResponse.response()
            Response response = new Response()
                .description(apiResponse.message())
                .headers(responseHeaders)

            if (responseClass == Void) {
                if (operation.getResponses() != null) {
                    Response apiOperationResponse = operation.getResponses().get(String.valueOf(apiResponse.code()))
                    if (apiOperationResponse != null) {
                        response.setSchema(apiOperationResponse.getSchema())
                    }
                }
            } else {
                Map<String, Model> models = ModelConverters.getInstance().read(responseClass)
                for (String key : models.keySet()) {
                    final Property schema = new RefProperty().asDefault(key)
                    if (apiResponse.responseContainer() == "List") {
                        response.schema(new ArrayProperty(schema))
                    } else {
                        response.schema(schema)
                    }
                    swagger.model(key, models.get(key))
                }
                models = ModelConverters.getInstance().readAll(responseClass)
                for (Map.Entry<String, Model> entry : models.entrySet()) {
                    swagger.model(entry.getKey(), entry.getValue())
                }

                if (response.getSchema() == null) {
                    Map<String, Response> responses = operation.getResponses()
                    if (responses != null) {
                        Response apiOperationResponse = responses.get(String.valueOf(apiResponse.code()))
                        if (apiOperationResponse != null) {
                            response.setSchema(apiOperationResponse.getSchema())
                        }
                    }
                }
            }

            if (apiResponse.code() == 0) {
                operation.defaultResponse(response)
            } else {
                operation.response(apiResponse.code(), response)
            }
        }
    }

    protected
    static String[] updateOperationProduces(String[] parentProduces, String[] apiProduces, Operation operation) {
        if (parentProduces != null) {
            Set<String> both = new LinkedHashSet<String>(Arrays.asList(apiProduces))
            both.addAll(Arrays.asList(parentProduces))
            if (operation.getProduces() != null) {
                both.addAll(operation.getProduces())
            }
            apiProduces = both.toArray(new String[both.size()])
        }
        return apiProduces
    }

    protected
    static String[] updateOperationConsumes(String[] parentConsumes, String[] apiConsumes, Operation operation) {
        if (parentConsumes != null) {
            Set<String> both = new LinkedHashSet<String>(Arrays.asList(apiConsumes))
            both.addAll(Arrays.asList(parentConsumes))
            if (operation.getConsumes() != null) {
                both.addAll(operation.getConsumes())
            }
            apiConsumes = both.toArray(new String[both.size()])
        }
        return apiConsumes
    }

    protected void readImplicitParameters(Method method, Operation operation) {
        ApiImplicitParams implicitParams = AnnotationUtils.findAnnotation(method, ApiImplicitParams)
        if (implicitParams == null) {
            return
        }
        for (ApiImplicitParam param : implicitParams.value()) {
            Class<?> cls = ReflectionUtils.typeFromString(param.dataType());

            Parameter p = readImplicitParam(param, cls)
            if (p != null) {
                operation.addParameter(p)
            }
        }
    }

    protected Parameter readImplicitParam(ApiImplicitParam param, Class<?> apiClass) {
        Parameter parameter = null

        switch (param.paramType().trim().toLowerCase()) {
            case 'path':
                parameter = new PathParameter()
                break
            case 'query':
                parameter = new QueryParameter()
                break
            case 'form':
            case 'formdata':
                parameter = new FormParameter()
                break
            case 'body':
                parameter = new BodyParameter()
                break
            case 'header':
                parameter = new HeaderParameter()
                break
        }

        return ParameterProcessor.applyAnnotations(swagger, parameter, apiClass, [param])
    }

    static void processOperationDecorator(Operation operation, Method method) {
        final Iterator<SwaggerExtension> chain = SwaggerExtensions.chain()
        if (chain.hasNext()) {
            SwaggerExtension extension = chain.next()
            extension.decorateOperation(operation, method, chain)
        }
    }

    static Property withResponseContainer(String responseContainer, Property property) {
        if ("list".equalsIgnoreCase(responseContainer)) {
            return new ArrayProperty(property)
        }
        if ("set".equalsIgnoreCase(responseContainer)) {
            return new ArrayProperty(property).uniqueItems()
        }
        if ("map".equalsIgnoreCase(responseContainer)) {
            return new MapProperty(property)
        }
        return property
    }
}

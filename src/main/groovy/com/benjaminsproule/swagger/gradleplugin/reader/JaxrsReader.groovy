package com.benjaminsproule.swagger.gradleplugin.reader

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.reader.extension.jaxrs.BeanParamInjectionParamExtension
import com.benjaminsproule.swagger.gradleplugin.reader.extension.jaxrs.JaxrsParameterExtension
import com.fasterxml.jackson.annotation.JsonView
import com.google.common.collect.Sets
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Authorization
import io.swagger.annotations.AuthorizationScope
import io.swagger.annotations.SwaggerDefinition
import io.swagger.converter.ModelConverters
import io.swagger.jaxrs.ext.SwaggerExtension
import io.swagger.jaxrs.ext.SwaggerExtensions
import io.swagger.jersey.SwaggerJersey2Jaxrs
import io.swagger.models.Model
import io.swagger.models.Operation
import io.swagger.models.Response
import io.swagger.models.SecurityRequirement
import io.swagger.models.Swagger
import io.swagger.models.parameters.Parameter
import io.swagger.models.properties.Property
import io.swagger.models.properties.RefProperty
import io.swagger.util.ReflectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.HEAD
import javax.ws.rs.HttpMethod
import javax.ws.rs.OPTIONS
import javax.ws.rs.PATCH
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.lang.reflect.Type

class JaxrsReader extends AbstractReader {
    private static final Logger LOG = LoggerFactory.getLogger(JaxrsReader)

    JaxrsReader(ApiSourceExtension apiSourceExtension, Set<Type> typesToSkip, List<SwaggerExtension> swaggerExtensions, ClassFinder classFinder) {
        super(apiSourceExtension, typesToSkip, swaggerExtensions, classFinder)
    }

    @Override
    protected List<SwaggerExtension> customSwaggerExtensions() {
        [
            new BeanParamInjectionParamExtension(),
            new SwaggerJersey2Jaxrs(),
            new JaxrsParameterExtension()
        ]
    }

    @Override
    Swagger read() {
        Set<Class<?>> classes = getValidClasses()
        for (Class<?> cls : classes) {
            readClass(cls)
        }
        return swagger
    }

    private Swagger readClass(Class<?> cls) {
        return readClass(cls, "", null, false, new String[0], new String[0], [:], [])
    }

    private Set<Class<?>> getValidClasses() {
        Set<Class<?>> classes = Sets.union(classFinder.getValidClasses(Api, apiSource.locations), classFinder.getValidClasses(Path, apiSource.locations))
        Set<Class<?>> copied = new HashSet<>(classes)
        for (Class<?> clazz : classes) {
            for (Class<?> aClazz : classes) {
                if (clazz != aClazz && clazz.isAssignableFrom(aClazz)) {
                    copied.remove(clazz)
                }
            }
        }
        copied
    }

    private Swagger readClass(Class<?> cls, String parentPath, String parentMethod, boolean readHidden, String[] parentConsumes, String[] parentProduces, Map<String, io.swagger.models.Tag> parentTags, List<Parameter> parentParameters) {
        Api api = AnnotationUtils.findAnnotation(cls, Api)

        // only read if allowing hidden apis OR api is not marked as hidden
        if (isApiUnreadable(readHidden, api)) {
            return swagger
        }

        Path apiPath = AnnotationUtils.findAnnotation(cls, Path)
        Consumes consumes = AnnotationUtils.findAnnotation(cls, Consumes)
        Produces produces = AnnotationUtils.findAnnotation(cls, Produces)

        Map<String, io.swagger.models.Tag> tags = updateTagsForApi(parentTags, api)
        List<SecurityRequirement> securities = getSecurityRequirements(api)
        Map<String, io.swagger.models.Tag> discoveredTags = scanClasspathForTags()
        String apiDescription = api.description()

        // merge consumes, produces

        // look for method-level annotated properties

        // handle subresources by looking at return type

        // parse the method
        for (Method method : cls.getMethods()) {
            ApiOperation apiOperation = AnnotationUtils.findAnnotation(method, ApiOperation)
            if (apiOperation != null && apiOperation.hidden()) {
                continue //Skip processing for hidden operation
            }

            if (!AnnotationUtils.findAnnotation(method, GET) && !AnnotationUtils.findAnnotation(method, POST)
                && !AnnotationUtils.findAnnotation(method, PUT) && !AnnotationUtils.findAnnotation(method, DELETE)
                && !AnnotationUtils.findAnnotation(method, OPTIONS) && !AnnotationUtils.findAnnotation(method, HEAD)
                && !AnnotationUtils.findAnnotation(method, PATCH)) {
                continue // Skip processing for non-API methods
            }

            Path methodPath = AnnotationUtils.findAnnotation(method, Path)

            String operationPath = getPath(apiPath, methodPath, parentPath)
            if (operationPath != null) {
                Map<String, String> regexMap = [:]
                operationPath = parseOperationPath(operationPath, regexMap)

                String httpMethod = extractOperationMethod(apiOperation, method, SwaggerExtensions.chain())

                Operation operation = parseMethod(httpMethod, method)
                updateOperationParameters(parentParameters, regexMap, operation)
                updateOperationProtocols(apiOperation, operation)

                def apiConsumes = new String[0]
                def apiProduces = new String[0]

                if (consumes) {
                    apiConsumes = consumes.value()
                }

                if (produces) {
                    apiProduces = produces.value()
                }

                apiConsumes = updateOperationConsumes(parentConsumes, apiConsumes, operation)
                apiProduces = updateOperationProduces(parentProduces, apiProduces, operation)

                handleSubResource(apiConsumes, httpMethod, apiProduces, tags, method, operationPath, operation)

                // can't continue without a valid http method
                httpMethod = (httpMethod == null) ? parentMethod : httpMethod
                updateTagsForOperation(operation, apiOperation)
                updateOperation(apiConsumes, apiProduces, tags, securities, operation)
                updatePath(operationPath, httpMethod, operation)
                if (apiDescription) {
                    LOG.error("Using deprecated description {} on Api annotation", apiDescription)
                    operation.description(apiDescription)
                }
            }
            updateTagDescriptions(discoveredTags)
        }

        return swagger
    }

    private void updateTagDescriptions(Map<String, io.swagger.models.Tag> discoveredTags) {
        if (swagger.getTags() != null) {
            for (io.swagger.models.Tag tag : swagger.getTags()) {
                io.swagger.models.Tag rightTag = discoveredTags.get(tag.getName())
                if (rightTag != null && rightTag.getDescription() != null) {
                    tag.setDescription(rightTag.getDescription())
                }
            }
        }
    }

    private Map<String, io.swagger.models.Tag> scanClasspathForTags() {
        def tags = [:]

        classFinder
            .getValidClasses(SwaggerDefinition, apiSource.locations)
            .each {
            def swaggerDefinition = AnnotationUtils.findAnnotation(it, SwaggerDefinition)

            swaggerDefinition.tags().each {
                if (it.name()) {
                    tags.put(it.name(), new io.swagger.models.Tag().name(it.name()).description(it.description()))
                }
            }
        }

        return tags
    }

    private void handleSubResource(String[] apiConsumes, String httpMethod, String[] apiProduces, Map<String, io.swagger.models.Tag> tags, Method method, String operationPath, Operation operation) {
        if (isSubResource(httpMethod, method)) {
            Class<?> responseClass = method.getReturnType()
            readClass(responseClass, operationPath, httpMethod, true, apiConsumes, apiProduces, tags, operation.getParameters())
        }
    }

    protected static boolean isSubResource(String httpMethod, Method method) {
        Class<?> responseClass = method.getReturnType()
        return responseClass != null && httpMethod == null && AnnotationUtils.findAnnotation(method, Path) != null
    }

    private static String getPath(Path classLevelPath, Path methodLevelPath, String parentPath) {
        if (classLevelPath == null && methodLevelPath == null) {
            return null
        }
        StringBuilder stringBuilder = new StringBuilder()
        if (parentPath && parentPath != "/") {
            if (!parentPath.startsWith("/")) {
                parentPath = "/" + parentPath
            }
            if (parentPath.endsWith("/")) {
                parentPath = parentPath.substring(0, parentPath.length() - 1)
            }

            stringBuilder.append(parentPath)
        }
        if (classLevelPath) {
            stringBuilder.append(classLevelPath.value())
        }
        if (methodLevelPath && methodLevelPath.value() != "/") {
            String methodPath = methodLevelPath.value()
            if (!methodPath.startsWith("/") && !stringBuilder.toString().endsWith("/")) {
                stringBuilder.append("/")
            }
            if (methodPath.endsWith("/")) {
                methodPath = methodPath.substring(0, methodPath.length() - 1)
            }
            stringBuilder.append(methodPath)
        }
        String output = stringBuilder.toString()
        if (!output.startsWith("/")) {
            output = "/" + output
        }
        if (output.endsWith("/") && output.length() > 1) {
            return output.substring(0, output.length() - 1)
        } else {
            return output
        }
    }


    Operation parseMethod(String httpMethod, Method method) {
        int responseCode = 200
        Operation operation = new Operation()
        ApiOperation apiOperation = AnnotationUtils.findAnnotation(method, ApiOperation)

        String operationId = method.getName()
        String responseContainer = null

        Type responseClassType = null
        Map<String, Property> defaultResponseHeaders = null

        if (apiOperation != null) {
            if (apiOperation.hidden()) {
                return null
            }
            if (!apiOperation.nickname().isEmpty()) {
                operationId = apiOperation.nickname()
            }

            defaultResponseHeaders = parseResponseHeaders(apiOperation.responseHeaders())
            operation.summary(apiOperation.value()).description(apiOperation.notes())

            Set<Map<String, Object>> customExtensions = parseCustomExtensions(apiOperation.extensions())
            if (customExtensions != null) {
                for (Map<String, Object> extension : customExtensions) {
                    if (extension == null) {
                        continue
                    }
                    for (Map.Entry<String, Object> map : extension.entrySet()) {
                        operation.setVendorExtension(map.getKey().startsWith("x-") ? map.getKey() : "x-" + map.getKey(), map.getValue())
                    }
                }
            }

            if (apiOperation.response() != Void && apiOperation.response() != void) {
                responseClassType = apiOperation.response()
            }
            if (!apiOperation.responseContainer().isEmpty()) {
                responseContainer = apiOperation.responseContainer()
            }
            for (Authorization auth : apiOperation.authorizations()) {
                if (!auth.value().isEmpty()) {
                    def scopes = []
                    for (AuthorizationScope scope : auth.scopes()) {
                        if (!scope.scope().isEmpty()) {
                            scopes += scope.scope()
                        }
                    }
                    operation.addSecurity(auth.value(), scopes)
                }
            }

            responseCode = apiOperation.code()
        }
        operation.operationId(operationId)

        if (responseClassType == null) {
            // pick out response from method declaration
            LOG.debug("picking up response class from method " + method)
            responseClassType = method.getGenericReturnType()
        }
        boolean hasApiAnnotation = false
        if (responseClassType instanceof Class) {
            hasApiAnnotation = AnnotationUtils.findAnnotation((Class) responseClassType, Api) != null
        }
        def jsonView = AnnotatedElementUtils.findMergedAnnotation(method, JsonView.class)
        if (responseClassType != null
            && responseClassType != Void
            && responseClassType != void
            && responseClassType != javax.ws.rs.core.Response
            && !hasApiAnnotation
            && !isSubResource(httpMethod, method)) {
            if (isPrimitive(responseClassType)) {
                Property property = ModelConverters.getInstance().readAsProperty(responseClassType, jsonView)
                if (property != null) {
                    Property responseProperty = withResponseContainer(responseContainer, property)

                    operation.response(responseCode, new Response()
                        .description("successful operation")
                        .schema(responseProperty)
                        .headers(defaultResponseHeaders))
                }
            } else if (responseClassType != Void && responseClassType != void) {
                Map<String, Model> models = ModelConverters.getInstance().read(responseClassType, jsonView)
                if (models.isEmpty()) {
                    Property p = ModelConverters.getInstance().readAsProperty(responseClassType, jsonView)
                    operation.response(responseCode, new Response()
                        .description("successful operation")
                        .schema(p)
                        .headers(defaultResponseHeaders))
                }
                for (String key : models.keySet()) {
                    Property responseProperty = withResponseContainer(responseContainer, new RefProperty().asDefault(key))

                    operation.response(responseCode, new Response()
                        .description("successful operation")
                        .schema(responseProperty)
                        .headers(defaultResponseHeaders))
                    swagger.model(key, models.get(key))
                }
            }
            Map<String, Model> models = ModelConverters.getInstance().readAll(responseClassType, jsonView)
            for (Map.Entry<String, Model> entry : models.entrySet()) {
                swagger.model(entry.getKey(), entry.getValue())
            }
        }

        Consumes consumes = AnnotationUtils.findAnnotation(method, Consumes)
        if (consumes != null) {
            for (String mediaType : consumes.value()) {
                operation.consumes(mediaType)
            }
        }

        Produces produces = AnnotationUtils.findAnnotation(method, Produces)
        if (produces != null) {
            for (String mediaType : produces.value()) {
                operation.produces(mediaType)
            }
        }

        ApiResponses responseAnnotation = AnnotationUtils.findAnnotation(method, ApiResponses)
        if (responseAnnotation) {
            updateApiResponse(operation, responseAnnotation, jsonView)
        }

        if (AnnotationUtils.findAnnotation(method, Deprecated) != null) {
            operation.deprecated(true)
        }

        // process parameters
        def parameterTypes = method.getParameterTypes()
        def genericParameterTypes = method.getGenericParameterTypes()
        def paramAnnotations = findParamAnnotations(method)

        parameterTypes.eachWithIndex { entry, i ->
            getParameters(genericParameterTypes[i], Arrays.asList(paramAnnotations[i])).each { parameter ->
                operation.parameter(parameter)
            }
        }

        if (operation.getResponses() == null) {
            operation.defaultResponse(new Response().description("successful operation"))
        }

        // Process @ApiImplicitParams
        this.readImplicitParameters(method, operation)

        processOperationDecorator(operation, method)

        return operation
    }

    private static Annotation[][] findParamAnnotations(Method method) {
        Annotation[][] paramAnnotation = method.getParameterAnnotations()

        Method overriddenMethod = ReflectionUtils.getOverriddenMethod(method)
        while (overriddenMethod != null) {
            paramAnnotation = merge(overriddenMethod.getParameterAnnotations(), paramAnnotation)
            overriddenMethod = ReflectionUtils.getOverriddenMethod(overriddenMethod)
        }
        return paramAnnotation
    }


    private static Annotation[][] merge(Annotation[][] overriddenMethodParamAnnotation,
                                        Annotation[][] currentParamAnnotations) {
        Annotation[][] mergedAnnotations = new Annotation[overriddenMethodParamAnnotation.length][]

        for (int i = 0; i < overriddenMethodParamAnnotation.length; i++) {
            mergedAnnotations[i] = merge(overriddenMethodParamAnnotation[i], currentParamAnnotations[i])
        }
        return mergedAnnotations
    }

    private static Annotation[] merge(Annotation[] annotations,
                                      Annotation[] annotations2) {
        List<Annotation> mergedAnnotations = []
        mergedAnnotations.addAll(Arrays.asList(annotations))
        mergedAnnotations.addAll(Arrays.asList(annotations2))
        return mergedAnnotations.toArray(new Annotation[0])
    }

    static String extractOperationMethod(ApiOperation apiOperation, Method method, Iterator<SwaggerExtension> chain) {
        if (apiOperation != null && !apiOperation.httpMethod().isEmpty()) {
            return apiOperation.httpMethod().toLowerCase()
        } else if (AnnotationUtils.findAnnotation(method, GET)) {
            return "get"
        } else if (AnnotationUtils.findAnnotation(method, PUT)) {
            return "put"
        } else if (AnnotationUtils.findAnnotation(method, POST)) {
            return "post"
        } else if (AnnotationUtils.findAnnotation(method, DELETE)) {
            return "delete"
        } else if (AnnotationUtils.findAnnotation(method, OPTIONS)) {
            return "options"
        } else if (AnnotationUtils.findAnnotation(method, HEAD)) {
            return "head"
        } else if (AnnotationUtils.findAnnotation(method, PATCH)) {
            return "patch"
        } else {
            // check for custom HTTP Method annotations
            for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                Annotation[] innerAnnotations = declaredAnnotation.annotationType().getAnnotations()
                for (Annotation innerAnnotation : innerAnnotations) {
                    if (innerAnnotation instanceof HttpMethod) {
                        HttpMethod httpMethod = (HttpMethod) innerAnnotation
                        return httpMethod.value().toLowerCase()
                    }
                }
            }

            if (chain.hasNext()) {
                return chain.next().extractOperationMethod(apiOperation, method, chain)
            }
        }

        return null
    }
}

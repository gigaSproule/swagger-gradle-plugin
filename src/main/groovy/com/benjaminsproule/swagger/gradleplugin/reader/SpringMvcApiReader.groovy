package com.benjaminsproule.swagger.gradleplugin.reader

import com.benjaminsproule.swagger.gradleplugin.Utils
import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.reader.extension.spring.SpringSwaggerExtension
import com.benjaminsproule.swagger.gradleplugin.reader.model.SpringResource
import com.google.common.collect.Sets
import io.swagger.annotations.*
import io.swagger.converter.ModelConverters
import io.swagger.jaxrs.ext.SwaggerExtension
import io.swagger.jaxrs.ext.SwaggerExtensions
import io.swagger.models.*
import io.swagger.models.parameters.Parameter
import io.swagger.models.properties.Property
import io.swagger.models.properties.RefProperty
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class SpringMvcApiReader extends AbstractReader {
    private static final Logger LOG = LoggerFactory.getLogger(SpringMvcApiReader)
    private String resourcePath

    SpringMvcApiReader(ApiSourceExtension apiSourceExtension, Set<Type> typesToSkip, List<SwaggerExtension> swaggerExtensions, ClassFinder classFinder) {
        super(apiSourceExtension, typesToSkip, swaggerExtensions, classFinder)
    }

    @Override
    List<SwaggerExtension> customSwaggerExtensions() {
        [new SpringSwaggerExtension()]
    }

    @Override
    Swagger read() throws GenerateException {
        //relate all methods to one base request mapping if multiple controllers exist for that mapping
        //get all methods from each controller & find their request mapping
        //create map - resource string (after first slash) as key, new SpringResource as value
        Map<String, SpringResource> resourceMap = generateResourceMap(getValidClasses())
        for (String str : resourceMap.keySet()) {
            SpringResource resource = resourceMap.get(str)
            read(resource)
        }

        return swagger
    }

    Set<Class<?>> getValidClasses() {
        Set<Class<?>> classes = Sets.union(classFinder.getValidClasses(Api, apiSource.locations), classFinder.getValidClasses(RestController, apiSource.locations))
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

    protected
    static Map<String, SpringResource> generateResourceMap(Set<Class<?>> validClasses) throws GenerateException {
        Map<String, SpringResource> resourceMap = [:]
        for (Class<?> aClass : validClasses) {
            //This try/catch block is to stop a bamboo build from failing due to NoClassDefFoundError
            //This occurs when a class or method loaded by reflections contains a type that has no dependency
            try {
                analyzeController(aClass, resourceMap, "")
            } catch (NoClassDefFoundError e) {
                LOG.error(e.getMessage())
                LOG.info(aClass.getName())
                //exception occurs when a method type or annotation is not recognized by the plugin
            }
        }

        return resourceMap
    }

    Swagger read(SpringResource resource) {
        List<Method> methods = resource.methods
        Map<String, io.swagger.models.Tag> tags = [:]

        List<SecurityRequirement> resourceSecurities = []
        String apiDescription

        // Add the description from the controller api
        Class<?> controller = resource.controllerClass
        RequestMapping controllerRM = AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping)

        String[] controllerProduces = new String[0]
        String[] controllerConsumes = new String[0]
        if (controllerRM != null) {
            controllerConsumes = controllerRM.consumes()
            controllerProduces = controllerRM.produces()
        }

        if (controller.isAnnotationPresent(Api)) {
            Api api = AnnotatedElementUtils.findMergedAnnotation(controller, Api)
            if (isApiUnreadable(false, api)) {
                return swagger
            }
            tags = updateTagsForApi(null, api)
            resourceSecurities = getSecurityRequirements(api)
            apiDescription = api.description()
        } else if(StringUtils.equals("class", apiSource.tagStrategy)) {
          // Apply a default tag when no Api present
          io.swagger.models.Tag controllerTag  = new io.swagger.models.Tag().name(controller.getSimpleName())
          tags.put(controllerTag.getName(), controllerTag)
          swagger.tag(controllerTag)
        }

        if (resource.controllerClass.isAnnotationPresent(RequestMapping)) {
            resourcePath = resource.controllerMapping[0]
        } else if (controllerRM != null) {
            resourcePath = controllerRM.path()[0]
        } else {
            resourcePath = ""
        }

        //collect api from method with @RequestMapping
        Map<String, List<Method>> apiMethodMap = collectApisByRequestMapping(methods)

        for (String path : apiMethodMap.keySet()) {
            for (Method method : apiMethodMap.get(path)) {
                RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping)
                if (requestMapping == null) {
                    continue
                }
                ApiOperation apiOperation = AnnotatedElementUtils.findMergedAnnotation(method, ApiOperation)
                if (apiOperation != null && apiOperation.hidden()) {
                    continue
                }

                Map<String, String> regexMap = [:]
                String operationPath = parseOperationPath(path, regexMap)

                //http method
                for (RequestMethod requestMethod : requestMapping.method()) {
                    String httpMethod = requestMethod.toString().toLowerCase()
                    Operation operation = parseMethod(method)

                    updateOperationParameters([], regexMap, operation)

                    updateOperationProtocols(apiOperation, operation)

                    String[] apiProduces = requestMapping.produces()
                    String[] apiConsumes = requestMapping.consumes()

                    apiProduces = (apiProduces.length == 0) ? controllerProduces : apiProduces
                    apiConsumes = (apiConsumes.length == 0) ? controllerConsumes : apiConsumes

                    apiConsumes = updateOperationConsumes(new String[0], apiConsumes, operation)
                    apiProduces = updateOperationProduces(new String[0], apiProduces, operation)

                    updateTagsForOperation(operation, apiOperation)
                    updateOperation(apiConsumes, apiProduces, tags, resourceSecurities, operation)
                    updatePath(operationPath, httpMethod, operation)
                    if (apiDescription) {
                        LOG.error("Using deprecated description [{}] on Api annotation", apiDescription)
                        operation.description(apiDescription)
                    }
                }
            }
        }
        return swagger
    }

    private Operation parseMethod(Method method) {
        int responseCode = 200
        Operation operation = new Operation()

        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping)
        Type responseClassType = null
        List<String> produces = []
        List<String> consumes = []
        String responseContainer = null
        String operationId = method.getName()
        Map<String, Property> defaultResponseHeaders = null

        ApiOperation apiOperation = AnnotatedElementUtils.findMergedAnnotation(method, ApiOperation)

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

            for (Map<String, Object> extension : customExtensions) {
                if (extension == null) {
                    continue
                }
                for (Map.Entry<String, Object> map : extension.entrySet()) {
                    operation.setVendorExtension(
                        map.getKey().startsWith("x-")
                            ? map.getKey()
                            : "x-" + map.getKey(), map.getValue()
                    )
                }
            }

            if (apiOperation.response() != Void && apiOperation.response() != void) {
                responseClassType = apiOperation.response()
            }
            if (!apiOperation.responseContainer().isEmpty()) {
                responseContainer = apiOperation.responseContainer()
            }

            ///security
            List<SecurityRequirement> securities = []
            for (Authorization auth : apiOperation.authorizations()) {
                if (!auth.value().isEmpty()) {
                    SecurityRequirement security = new SecurityRequirement()
                    security.setName(auth.value())
                    for (AuthorizationScope scope : auth.scopes()) {
                        if (!scope.scope().isEmpty()) {
                            security.addScope(scope.scope())
                        }
                    }
                    securities += security
                }
            }
            for (SecurityRequirement sec : securities) {
                operation.security(sec)
            }

            responseCode = apiOperation.code()
        }

        if (responseClassType == null) {
            // pick out response from method declaration
            LOG.info("picking up response class from method " + method)
            responseClassType = method.getGenericReturnType()
        }
        if (responseClassType instanceof ParameterizedType && ResponseEntity == ((ParameterizedType) responseClassType).getRawType()) {
            responseClassType = ((ParameterizedType) responseClassType).getActualTypeArguments()[0]
        }
        boolean hasApiAnnotation = false
        if (responseClassType instanceof Class) {
            hasApiAnnotation = AnnotationUtils.findAnnotation((Class) responseClassType, Api) != null
        }
        if (responseClassType != null
            && responseClassType != Void
            && responseClassType != void
            && responseClassType != ResponseEntity
            && !hasApiAnnotation) {
            if (isPrimitive(responseClassType)) {
                Property property = ModelConverters.getInstance().readAsProperty(responseClassType)
                if (property != null) {
                    Property responseProperty = withResponseContainer(responseContainer, property)
                    operation.response(responseCode, new Response()
                        .description("successful operation")
                        .schema(responseProperty)
                        .headers(defaultResponseHeaders))
                }
            } else if (responseClassType != Void && responseClassType != void) {
                Map<String, Model> models = ModelConverters.getInstance().read(responseClassType)
                if (models.isEmpty()) {
                    Property pp = ModelConverters.getInstance().readAsProperty(responseClassType)
                    operation.response(responseCode, new Response()
                        .description("successful operation")
                        .schema(pp)
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
            Map<String, Model> models = ModelConverters.getInstance().readAll(responseClassType)
            for (Map.Entry<String, Model> entry : models.entrySet()) {
                swagger.model(entry.getKey(), entry.getValue())
            }
        }

        operation.operationId(operationId)

        for (String str : requestMapping.produces()) {
            if (!produces.contains(str)) {
                produces += str
            }
        }
        for (String str : requestMapping.consumes()) {
            if (!consumes.contains(str)) {
                consumes += str
            }
        }

        ApiResponses responseAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, ApiResponses)
        if (responseAnnotation != null) {
            updateApiResponse(operation, responseAnnotation)
        } else {
            ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(method, ResponseStatus)
            if (responseStatus != null) {
                operation.response(responseStatus.value().value(), new Response().description(responseStatus.reason()))
            }
        }

        Deprecated annotation = AnnotationUtils.findAnnotation(method, Deprecated)
        if (annotation != null) {
            operation.deprecated(true)
        }

        // FIXME `hidden` is never used
        boolean hidden = false
        if (apiOperation != null) {
            hidden = apiOperation.hidden()
        }

        // process parameters
        Class[] parameterTypes = method.getParameterTypes()
        Type[] genericParameterTypes = method.getGenericParameterTypes()
        Annotation[][] paramAnnotations = method.getParameterAnnotations()
        DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer()
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method)
        // paramTypes = method.getParameterTypes
        // genericParamTypes = method.getGenericParameterTypes
        parameterTypes.eachWithIndex { parameterType, i ->
            Type type = genericParameterTypes[i]
            List<Annotation> annotations = Arrays.asList(paramAnnotations[i])
            List<Parameter> parameters = getParameters(type, annotations)

            for (Parameter parameter : parameters) {
                if (parameter.getName().isEmpty()) {
                    parameter.setName(parameterNames[i])
                }
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

    private Map<String, List<Method>> collectApisByRequestMapping(List<Method> methods) {
        Map<String, List<Method>> apiMethodMap = [:]
        for (Method method : methods) {
            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping)
            if (requestMapping != null) {
                String path
                if (requestMapping.value().length != 0) {
                    path = generateFullPath(requestMapping.value()[0])
                } else {
                    path = resourcePath
                }
                if (apiMethodMap.containsKey(path)) {
                    apiMethodMap.get(path).add(method)
                } else {
                    List<Method> ms = []
                    ms += method
                    apiMethodMap.put(path, ms)
                }
            }
        }

        return apiMethodMap
    }

    private String generateFullPath(String path) {
        if (StringUtils.isNotEmpty(path)) {
            return this.resourcePath + (path.startsWith("/") ? path : '/' + path)
        } else {
            return this.resourcePath
        }
    }

    //Helper method for loadDocuments()
    private
    static Map<String, SpringResource> analyzeController(Class<?> controllerClazz, Map<String, SpringResource> resourceMap, String description) {
        String[] controllerRequestMappingValues = Utils.getControllerRequestMapping(controllerClazz)

        // Iterate over all value attributes of the class-level RequestMapping annotation
        for (String controllerRequestMappingValue : controllerRequestMappingValues) {
            for (Method method : controllerClazz.getMethods()) {
                RequestMapping methodRequestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping)

                // Look for method-level @RequestMapping annotation
                if (methodRequestMapping != null) {
                    RequestMethod[] requestMappingRequestMethods = methodRequestMapping.method()

                    // For each method-level @RequestMapping annotation, iterate over HTTP Verb
                    for (RequestMethod requestMappingRequestMethod : requestMappingRequestMethods) {
                        String[] methodRequestMappingValues = methodRequestMapping.value()

                        // Check for cases where method-level @RequestMapping#value is not set, and use the controllers @RequestMapping
                        if (methodRequestMappingValues.length == 0) {
                            // The map key is a concat of the following:
                            //   1. The controller package
                            //   2. The controller class name
                            //   3. The controller-level @RequestMapping#value
                            String resourceKey = controllerRequestMappingValue +
                                requestMappingRequestMethod
                            if (!resourceMap.containsKey(resourceKey)) {
                                resourceMap.put(
                                    resourceKey,
                                    new SpringResource(controllerClazz, description))
                            }
                            resourceMap.get(resourceKey).addMethod(method)
                        } else {
                            // Here we know that method-level @RequestMapping#value is populated, so
                            // iterate over all the @RequestMapping#value attributes, and add them to the resource map.
                            for (String methodRequestMappingValue : methodRequestMappingValues) {
                                String resourceKey = controllerRequestMappingValue +
                                    methodRequestMappingValue +
                                    requestMappingRequestMethod
                                if (!resourceMap.containsKey(resourceKey)) {
                                    resourceMap.put(resourceKey, new SpringResource(controllerClazz, description))
                                }
                                resourceMap.get(resourceKey).addMethod(method)
                            }
                        }
                    }
                }
            }
        }
        controllerClazz.getFields()
        controllerClazz.getDeclaredFields() //<--In case developer declares a field without an associated getter/setter.
        //this will allow NoClassDefFoundError to be caught before it triggers bamboo failure.

        return resourceMap
    }
}

package com.benjaminsproule.swagger.gradleplugin.reader

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import io.swagger.jaxrs.ext.SwaggerExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.reflect.Constructor
import java.lang.reflect.Type

class ReaderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ReaderFactory)
    private ClassFinder classFinder
    ApiSourceExtension apiSourceExtension

    ReaderFactory(ClassFinder classFinder, ApiSourceExtension apiSourceExtension) {
        this.classFinder = classFinder
        this.apiSourceExtension = apiSourceExtension
    }

    ClassSwaggerReader reader() {
        if (apiSourceExtension.springmvc) {
            return new SpringMvcApiReader(apiSourceExtension, loadTypesToSkip(), resolveSwaggerExtensions(), classFinder)
        } else if (apiSourceExtension.getSwaggerApiReader()) {
            return getCustomApiReader()
        } else {
            return new JaxrsReader(apiSourceExtension, loadTypesToSkip(), resolveSwaggerExtensions(), classFinder)
        }
    }

    private Set<Type> loadTypesToSkip() throws GenerateException {
        def typesToSkip = []

        if (!apiSourceExtension.getTypesToSkip()) {
            return typesToSkip
        }

        for (String typeToSkip : apiSourceExtension.getTypesToSkip()) {
            try {
                Type type = classFinder.loadClass(typeToSkip)
                typesToSkip.add(type)
            } catch (ClassNotFoundException e) {
                throw new GenerateException("${typesToSkip} could not be found", e)
            }
        }

        return typesToSkip
    }

    /**
     * Resolves all {@link io.swagger.jaxrs.ext.SwaggerExtension} instances configured to be added to the Swagger configuration.
     *
     * @return Collection < SwaggerExtension >  which should be added to the swagger configuration
     * @throws GenerateException if the swagger extensions could not be created / resolved
     */
    private List<SwaggerExtension> resolveSwaggerExtensions() throws GenerateException {
        List<String> clazzes = apiSourceExtension.getSwaggerExtensions()
        List<SwaggerExtension> resolved = new ArrayList<SwaggerExtension>()
        if (clazzes != null) {
            for (String clazz : clazzes) {
                SwaggerExtension extension
                try {
                    extension = (SwaggerExtension) Class.forName(clazz).newInstance([])
                } catch (Exception e) {
                    throw new GenerateException("Cannot load Swagger extension: " + clazz, e)
                }
                resolved.add(extension)
            }
        }
        return resolved
    }

    // TODO: Create tests for custom API reader
    private ClassSwaggerReader getCustomApiReader() throws GenerateException {
        String customReaderClassName = apiSourceExtension.getSwaggerApiReader()
        try {
            LOG.info("Reading custom API reader: " + customReaderClassName)
            Class<?> clazz = classFinder.loadClass(customReaderClassName)
            if (AbstractReader.class.isAssignableFrom(clazz)) {
                Constructor<?> constructor = clazz.getConstructor(ApiSourceExtension, Set, List, ClassFinder)
                return (ClassSwaggerReader) constructor.newInstance(apiSourceExtension, loadTypesToSkip(), resolveSwaggerExtensions(), ClassFinder)
            } else {
                return (ClassSwaggerReader) clazz.newInstance()
            }
        } catch (Exception e) {
            throw new GenerateException("Cannot load Swagger API reader: ${customReaderClassName}", e)

        }
    }
}

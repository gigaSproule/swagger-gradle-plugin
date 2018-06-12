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

    ReaderFactory(ClassFinder classFinder) {
        this.classFinder = classFinder
    }

    ClassSwaggerReader reader(ApiSourceExtension apiSourceExtension) {
        if (apiSourceExtension.springmvc) {
            return new SpringMvcApiReader(apiSourceExtension, loadTypesToSkip(apiSourceExtension), resolveSwaggerExtensions(apiSourceExtension), classFinder)
        } else if (apiSourceExtension.getSwaggerApiReader()) {
            return getCustomApiReader(apiSourceExtension)
        } else {
            return new JaxrsReader(apiSourceExtension, loadTypesToSkip(apiSourceExtension), resolveSwaggerExtensions(apiSourceExtension), classFinder)
        }
    }

    private Set<Type> loadTypesToSkip(ApiSourceExtension apiSourceExtension) throws GenerateException {
        def typesToSkip = []

        for (String typeToSkip : apiSourceExtension.typesToSkipList) {
            try {
                Type type = classFinder.loadClass(typeToSkip)
                typesToSkip += type
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
    private List<SwaggerExtension> resolveSwaggerExtensions(ApiSourceExtension apiSourceExtension) throws GenerateException {
        List<String> clazzes = apiSourceExtension.getSwaggerExtensions()
        List<SwaggerExtension> resolved = []
        if (clazzes != null) {
            for (String clazz : clazzes) {
                SwaggerExtension extension
                try {
                    extension = (SwaggerExtension) classFinder.loadClass(clazz).newInstance([])
                } catch (Exception e) {
                    throw new GenerateException("Cannot load Swagger extension: " + clazz, e)
                }
                resolved += extension
            }
        }
        return resolved
    }

    // TODO: Create tests for custom API reader
    private ClassSwaggerReader getCustomApiReader(ApiSourceExtension apiSourceExtension) throws GenerateException {
        String customReaderClassName = apiSourceExtension.getSwaggerApiReader()
        try {
            LOG.info("Reading custom API reader: " + customReaderClassName)
            Class<?> clazz = classFinder.loadClass(customReaderClassName)
            if (AbstractReader.isAssignableFrom(clazz)) {
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

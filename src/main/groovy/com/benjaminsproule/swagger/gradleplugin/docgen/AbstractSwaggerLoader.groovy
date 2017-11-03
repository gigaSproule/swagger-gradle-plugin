package com.benjaminsproule.swagger.gradleplugin.docgen

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.reader.AbstractReader
import com.benjaminsproule.swagger.gradleplugin.reader.ClassSwaggerReader
import io.swagger.annotations.Api
import io.swagger.config.FilterFactory
import io.swagger.core.filter.SpecFilter
import io.swagger.jaxrs.ext.SwaggerExtension
import io.swagger.models.Swagger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.reflect.Constructor
import java.lang.reflect.Type

abstract class AbstractSwaggerLoader {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSwaggerLoader.class)

    ApiSourceExtension apiSource

    AbstractSwaggerLoader(ApiSourceExtension apiSource) {
        this.apiSource = apiSource
    }

    Swagger loadDocuments(Swagger swagger) throws GenerateException {
        ClassSwaggerReader reader = resolveApiReader(swagger)

        swagger = reader.read(getValidClasses())
        swagger = applySwaggerFilter(swagger)

        return swagger
    }

    private static Swagger applySwaggerFilter(Swagger swagger) {
        if (FilterFactory.getFilter()) {
            swagger = new SpecFilter().filter(swagger, FilterFactory.getFilter(), new HashMap<String, List<String>>(), new HashMap<String, String>(),
                new HashMap<String, List<String>>())
        }
        swagger
    }

    protected Set<Type> loadTypesToSkip() throws GenerateException {
        def typesToSkip = []

        if (!apiSource.getTypesToSkip()) {
            return typesToSkip
        }

        for (String typeToSkip : apiSource.getTypesToSkip()) {
            try {
                Type type = Class.forName(typeToSkip)
                typesToSkip.push(type)
            } catch (ClassNotFoundException e) {
                throw new GenerateException("${typesToSkip} could not be found", e)
            }
        }

        return typesToSkip
    }

    /**
     * Resolves the API reader which should be used to scan the classes.
     *
     * @param Swagger swagger instance being built
     * @return ClassSwaggerReader to use
     * @throws GenerateException if the reader cannot be created / resolved
     */
    protected abstract ClassSwaggerReader resolveApiReader(Swagger swagger) throws GenerateException

    /**
     * Returns the set of classes which should be included in the scanning.
     *
     * @return Set < Class < ? > > containing all valid classes
     */
    protected abstract Set<Class<?>> getValidClasses()

    protected Set<Class<?>> getApiClasses() {
        return ClassFinder.instance().getValidClasses(Api, apiSource.locations, apiSource.excludePattern)
    }

    /**
     * Resolves all {@link SwaggerExtension} instances configured to be added to the Swagger configuration.
     *
     * @return Collection < SwaggerExtension >  which should be added to the swagger configuration
     * @throws GenerateException if the swagger extensions could not be created / resolved
     */
    protected List<SwaggerExtension> resolveSwaggerExtensions() throws GenerateException {
        List<String> clazzes = apiSource.getSwaggerExtensions()
        List<SwaggerExtension> resolved = new ArrayList<SwaggerExtension>()
        if (clazzes != null) {
            for (String clazz : clazzes) {
                SwaggerExtension extension
                try {
                    extension = (SwaggerExtension) Class.forName(clazz).newInstance()
                } catch (Exception e) {
                    throw new GenerateException("Cannot load Swagger extension: " + clazz, e)
                }
                resolved.add(extension)
            }
        }
        return resolved
    }

    protected static ClassSwaggerReader getCustomApiReader(Swagger swagger, String customReaderClassName) throws GenerateException {
        try {
            LOG.info("Reading custom API reader: " + customReaderClassName)
            Class<?> clazz = Class.forName(customReaderClassName)
            if (AbstractReader.class.isAssignableFrom(clazz)) {
                Constructor<?> constructor = clazz.getConstructor(Swagger.class)
                return (ClassSwaggerReader) constructor.newInstance(swagger)
            } else {
                return (ClassSwaggerReader) clazz.newInstance()
            }
        } catch (Exception e) {
            throw new GenerateException("Cannot load Swagger API reader: ${customReaderClassName}", e)
        }
    }
}

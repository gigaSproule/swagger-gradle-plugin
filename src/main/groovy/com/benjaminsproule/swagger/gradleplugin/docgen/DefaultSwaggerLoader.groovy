package com.benjaminsproule.swagger.gradleplugin.docgen

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.reader.ClassSwaggerReader
import com.benjaminsproule.swagger.gradleplugin.reader.JaxrsReader
import com.google.common.collect.Sets
import io.swagger.models.Swagger

import javax.ws.rs.Path

class DefaultSwaggerLoader extends AbstractSwaggerLoader {

    DefaultSwaggerLoader(ApiSourceExtension apiSource) {
        super(apiSource)
    }

    @Override
    Set<Class<?>> getValidClasses() {
        return Sets.union(
            getApiClasses(),
            ClassFinder.instance().getValidClasses(Path, apiSource.locations, apiSource.excludePattern))
    }

    @Override
    protected ClassSwaggerReader resolveApiReader(Swagger swagger) throws GenerateException {
        String customReaderClassName = apiSource.getSwaggerApiReader()
        if (customReaderClassName) {
            return getCustomApiReader(swagger, customReaderClassName)
        } else {
            return new JaxrsReader(apiSource, swagger, loadTypesToSkip(), resolveSwaggerExtensions())
        }
    }
}

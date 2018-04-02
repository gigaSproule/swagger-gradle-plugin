package com.benjaminsproule.swagger.gradleplugin.docgen

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.reader.ClassSwaggerReader
import com.benjaminsproule.swagger.gradleplugin.reader.JaxrsReader
import com.google.common.collect.Sets
import io.swagger.models.Swagger

import javax.ws.rs.Path

class JaxrsSwaggerLoader extends AbstractSwaggerLoader {

    JaxrsSwaggerLoader(ApiSourceExtension apiSource) {
        super(apiSource)
    }

    @Override
    Set<Class<?>> getValidClasses() {
        Set<Class<?>> classes = Sets.union(getApiClasses(), ClassFinder.instance().getValidClasses(Path, apiSource.locations))
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

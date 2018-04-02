package com.benjaminsproule.swagger.gradleplugin.docgen

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.reader.ClassSwaggerReader
import com.benjaminsproule.swagger.gradleplugin.reader.SpringMvcApiReader
import com.google.common.collect.Sets
import io.swagger.models.Swagger
import org.springframework.web.bind.annotation.RestController

class SpringSwaggerLoader extends AbstractSwaggerLoader {

    SpringSwaggerLoader(ApiSourceExtension apiSource) {
        super(apiSource)
    }

    @Override
    Set<Class<?>> getValidClasses() {
        Set<Class<?>> classes = Sets.union(getApiClasses(), ClassFinder.instance().getValidClasses(RestController, apiSource.locations))
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
            return new SpringMvcApiReader(apiSource, swagger, loadTypesToSkip(), resolveSwaggerExtensions())
        }
    }
}

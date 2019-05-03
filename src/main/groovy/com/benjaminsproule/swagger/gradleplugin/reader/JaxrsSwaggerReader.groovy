package com.benjaminsproule.swagger.gradleplugin.reader

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.google.common.collect.Sets
import io.swagger.annotations.Api
import io.swagger.annotations.ApiModel
import io.swagger.converter.ModelConverters
import io.swagger.jaxrs.Reader
import io.swagger.jaxrs.ext.SwaggerExtension
import io.swagger.models.Model
import io.swagger.models.Swagger

import javax.ws.rs.Path
import java.lang.reflect.Type

class JaxrsSwaggerReader extends AbstractReader {
    JaxrsSwaggerReader(
        final ApiSourceExtension apiSource,
        final Set<Type> typesToSkip,
        final List<SwaggerExtension> swaggerExtensions,
        final ClassFinder classFinder) {

        super(apiSource,
            Optional.ofNullable(typesToSkip).orElse(new HashSet<>()),
            Optional.ofNullable(swaggerExtensions).orElse(new ArrayList<>()),
            classFinder)
    }

    @Override
    protected List<SwaggerExtension> customSwaggerExtensions() {
        return new ArrayList<>();
    }

    @Override
    Swagger read() throws GenerateException {
        new Reader(swagger).read(getValidClasses())
        readModels()

        return swagger
    }

    private Set<Class<?>> getValidClasses() {
        final Set<Class<?>> classes = Sets.union(classFinder.getValidClasses(Api.class, apiSource.getLocations()),
            classFinder.getValidClasses(Path.class, apiSource.getLocations()))
        final Set<Class<?>> copied = new HashSet<>(classes)
        for (Class<?> clazz : classes) {
            for (Class<?> aClazz : classes) {
                if (clazz != aClazz && clazz.isAssignableFrom(aClazz)) {
                    copied.remove(clazz)
                }
            }
        }
        return copied
    }

    private void readModels() {
        classFinder.getValidClasses(ApiModel.class, apiSource.getLocations()).each { apiModelClass ->
            Map<String, Model> models = ModelConverters.getInstance().read(apiModelClass)
            models.entrySet().each { model ->
                swagger.model(model.getKey(), model.getValue())
            }
        }
    }
}

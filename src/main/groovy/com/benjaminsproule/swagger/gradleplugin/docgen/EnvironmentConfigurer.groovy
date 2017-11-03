package com.benjaminsproule.swagger.gradleplugin.docgen

import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import com.benjaminsproule.swagger.gradleplugin.misc.EnhancedSwaggerModule
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.reader.resolver.ModelModifier
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule
import io.swagger.config.FilterFactory
import io.swagger.converter.ModelConverter
import io.swagger.converter.ModelConverters
import io.swagger.core.filter.SwaggerSpecFilter
import io.swagger.util.Json
import org.gradle.api.GradleException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EnvironmentConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentConfigurer)
    ApiSourceExtension apiSource

    EnvironmentConfigurer(ApiSourceExtension apiSourceExtension) {
        this.apiSource = apiSourceExtension
    }

    EnvironmentConfigurer initOutputDirectory() {
        if (apiSource.outputPath) {
            def outputDirectory = new File(apiSource.outputPath).getParentFile()
            if (outputDirectory && !outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    throw new GradleException("Create directory[${apiSource.getOutputPath()}] for output failed.")
                }
            }
        }

        return this
    }

    EnvironmentConfigurer configureSwaggerFilter() {
        if (apiSource.getSwaggerInternalFilter() != null) {
            try {
                LOG.info("Setting filter configuration: " + apiSource.getSwaggerInternalFilter())
                FilterFactory.setFilter((SwaggerSpecFilter) Class.forName(apiSource.getSwaggerInternalFilter()).newInstance())
            } catch (Exception e) {
                throw new GenerateException("Cannot load: " + apiSource.getSwaggerInternalFilter(), e)
            }
        }

        return this
    }

    EnvironmentConfigurer configureModelConverters() {
        if (apiSource.getModelConverters()) {
            apiSource.getModelConverters().each { String modelConverter ->
                try {
                    def modelConverterClass = Class.forName(modelConverter)
                    if (ModelConverter.class.isAssignableFrom(modelConverterClass)) {
                        def modelConverterInstance = (ModelConverter) modelConverterClass.newInstance()
                        ModelConverters.getInstance().addConverter(modelConverterInstance)
                    } else {
                        throw new GradleException(String.format("Class %s has to be a subclass of %s", modelConverterClass.getName(), ModelConverter.class))
                    }
                } catch (ClassNotFoundException e) {
                    throw new GradleException(String.format("Could not find custom model converter %s", modelConverter), e)
                } catch (InstantiationException e) {
                    throw new GradleException(String.format("Unable to instantiate custom model converter %s", modelConverter), e)
                } catch (IllegalAccessException e) {
                    throw new GradleException(String.format("Unable to instantiate custom model converter %s", modelConverter), e)
                }
            }
        }

        return this
    }

    EnvironmentConfigurer configureModelModifiers() throws GenerateException, IOException {
        ObjectMapper objectMapper = Json.mapper()

        optionallyRegisterJaxbModule(objectMapper)

        ModelModifier modelModifier = new ModelModifier(objectMapper)
        if (apiSource.apiModelPropertyAccessExclusions) {
            modelModifier.setApiModelPropertyAccessExclusions(apiSource.apiModelPropertyAccessExclusions)
        }

        if (apiSource.modelSubstitute) {
            getClass().getResource(apiSource.modelSubstitute).eachLine { line ->
                def classes = line.split(":")
                if (classes.length != 2) {
                    throw new GenerateException('Bad format of override model file, it should be ${actualClassName}:${expectClassName}')
                }
                modelModifier.addModelSubstitute(classes[0].trim(), classes[1].trim())
            }
        }

        ModelConverters.getInstance().addConverter(modelModifier)
        return this
    }

    private void optionallyRegisterJaxbModule(ObjectMapper objectMapper) {
        if (apiSource.isUseJAXBAnnotationProcessor()) {
            JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule()
            if (apiSource.isUseJAXBAnnotationProcessorAsPrimary()) {
                jaxbAnnotationModule.setPriority(JaxbAnnotationModule.Priority.PRIMARY)
            } else {
                jaxbAnnotationModule.setPriority(JaxbAnnotationModule.Priority.SECONDARY)
            }
            objectMapper.registerModule(jaxbAnnotationModule)

            // to support @ApiModel on class level.
            // must be registered only if we use JaxbAnnotationModule before. Why?
            // https://github.com/swagger-api/swagger-core/issues/2104
            objectMapper.registerModule(new EnhancedSwaggerModule())
        }
    }
}

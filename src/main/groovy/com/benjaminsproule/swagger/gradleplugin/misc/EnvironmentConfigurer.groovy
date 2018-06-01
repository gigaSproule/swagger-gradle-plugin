package com.benjaminsproule.swagger.gradleplugin.misc

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
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
    private ApiSourceExtension apiSourceExtension
    private ClassFinder classFinder
    private List<ModelConverter> modelConverters = new ArrayList<>()
    private List<ModelModifier> modelModifiers = new ArrayList<>()

    EnvironmentConfigurer(ApiSourceExtension apiSourceExtension, ClassFinder classFinder) {
        this.apiSourceExtension = apiSourceExtension
        this.classFinder = classFinder
    }

    EnvironmentConfigurer initOutputDirectory() {
        if (apiSourceExtension.outputPath) {
            def outputDirectory = new File(apiSourceExtension.outputPath).getParentFile()
            if (outputDirectory && !outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    throw new GradleException("Create directory[${apiSourceExtension.getOutputPath()}] for output failed.")
                }
            }
        }

        return this
    }

    EnvironmentConfigurer configureSwaggerFilter() {
        if (apiSourceExtension.getSwaggerInternalFilter() != null) {
            try {
                LOG.info("Setting filter configuration: " + apiSourceExtension.getSwaggerInternalFilter())
                FilterFactory.setFilter((SwaggerSpecFilter) Class.forName(apiSourceExtension.getSwaggerInternalFilter()).newInstance())
            } catch (Exception e) {
                throw new GenerateException("Cannot load: " + apiSourceExtension.getSwaggerInternalFilter(), e)
            }
        }

        return this
    }

    EnvironmentConfigurer configureModelConverters() {
        if (apiSourceExtension.getModelConverters()) {
            apiSourceExtension.getModelConverters().each { String modelConverter ->
                try {
                    def modelConverterClass = Class.forName(modelConverter)
                    if (ModelConverter.class.isAssignableFrom(modelConverterClass)) {
                        def modelConverterInstance = (ModelConverter) modelConverterClass.newInstance()
                        modelConverters.add(modelConverterInstance)
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

        ModelModifier modelModifier = new ModelModifier(objectMapper, classFinder)
        if (apiSourceExtension.apiModelPropertyAccessExclusions) {
            modelModifier.setApiModelPropertyAccessExclusions(apiSourceExtension.apiModelPropertyAccessExclusions)
        }

        if (apiSourceExtension.modelSubstitute) {
            classFinder.getClassLoader().getResourceAsStream(apiSourceExtension.modelSubstitute).eachLine { line ->
                def classes = line.split(":")
                if (classes.length != 2) {
                    throw new GenerateException('Bad format of override model file, it should be ${actualClassName}:${expectClassName}')
                }
                modelModifier.addModelSubstitute(classes[0].trim(), classes[1].trim())
            }
        }

        modelModifiers.add(modelModifier)
        ModelConverters.getInstance().addConverter(modelModifier)
        return this
    }

    private void optionallyRegisterJaxbModule(ObjectMapper objectMapper) {
        if (apiSourceExtension.isUseJAXBAnnotationProcessor()) {
            JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule()
            if (apiSourceExtension.isUseJAXBAnnotationProcessorAsPrimary()) {
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

    void cleanUp() {
        classFinder.clearClassCache() // TODO: Maybe do something better here?
        (modelConverters + modelModifiers).each {
            ModelConverters.instance.removeConverter(it)
        }
    }
}

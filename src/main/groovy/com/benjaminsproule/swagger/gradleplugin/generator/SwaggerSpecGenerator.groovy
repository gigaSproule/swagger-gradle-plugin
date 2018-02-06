package com.benjaminsproule.swagger.gradleplugin.generator

import com.benjaminsproule.swagger.gradleplugin.Utils
import com.benjaminsproule.swagger.gradleplugin.example.PropertyExampleMixIn
import com.benjaminsproule.swagger.gradleplugin.except.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import io.swagger.models.Swagger
import io.swagger.models.properties.Property
import io.swagger.util.Yaml
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SwaggerSpecGenerator implements Generator {
    private static final Logger LOG = LoggerFactory.getLogger(SwaggerSpecGenerator)

    ApiSourceExtension apiSource
    ObjectMapper mapper = new ObjectMapper()
    boolean isSorted = false
    String encoding

    SwaggerSpecGenerator(ApiSourceExtension apiSourceExtension) {
        this.apiSource = apiSourceExtension
        this.encoding = apiSource.project.compileJava.options.encoding ?: "UTF-8"
    }

    @Override
    void generate(Swagger source) {
        //Not come across an appropriate solution that is not deprecated yet
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        if (apiSource.jsonExampleValues) {
            mapper.addMixIn(Property, PropertyExampleMixIn)
        }

        if (!apiSource.swaggerDirectory) {
            return
        }

        if (!isSorted) {
            Utils.sortSwagger(source)
            isSorted = true
        }
        def dir = new File(apiSource.swaggerDirectory)
        if (dir.isFile()) {
            throw new GenerateException(String.format("Swagger-outputDirectory[%s] must be a directory!", apiSource.swaggerDirectory))
        }

        if (!dir.exists()) {
            try {
                FileUtils.forceMkdir(dir)
            } catch (IOException e) {
                throw new GenerateException(String.format("Create Swagger-outputDirectory[%s] failed.", apiSource.swaggerDirectory), e)
            }
        }

        LOG.info("Writing swagger spec to ${apiSource.swaggerDirectory}")

        def fileName = apiSource.swaggerFileName ?: 'swagger'

        try {
            if (apiSource.outputFormats) {
                for (String format : apiSource.outputFormats) {
                    try {
                        switch (format.trim().toLowerCase()) {
                            case 'json':
                                writeAsJsonFormat(dir, fileName, source)
                                break
                            case 'yaml':
                                FileUtils.write(new File(dir, fileName + ".yaml"), Yaml.pretty().writeValueAsString(source), encoding)
                                break
                        }
                    } catch (Exception e) {
                        throw new GenerateException(String.format("Declared output format [%s] is not supported.", format), e)
                    }
                }
            } else {
                // Default to json
                writeAsJsonFormat(dir, fileName, source)
            }
        } catch (IOException e) {
            throw new GenerateException(e)
        }
    }

    private void writeAsJsonFormat(File dir, String fileName, Swagger source) {
        ObjectWriter jsonWriter = mapper.writer(new DefaultPrettyPrinter())
        FileUtils.write(new File(dir, fileName + ".json"), jsonWriter.writeValueAsString(source), encoding)
    }
}

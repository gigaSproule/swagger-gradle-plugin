package com.benjaminsproule.swagger.gradleplugin.generator

import com.benjaminsproule.swagger.gradleplugin.Utils
import com.benjaminsproule.swagger.gradleplugin.example.PropertyExampleMixIn
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import io.swagger.models.Swagger
import io.swagger.models.properties.Property
import io.swagger.util.ObjectMapperFactory
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SwaggerSpecGenerator implements Generator {
    private static final Logger LOG = LoggerFactory.getLogger(SwaggerSpecGenerator)
    private static final String KEY_JSON = "json"
    private static final String KEY_YAML = "yaml"

    ApiSourceExtension apiSource
    HashMap<String, ObjectMapper> typeToObjectMapperMap = new HashMap<>();
    boolean isSorted = false
    String encoding

    SwaggerSpecGenerator(ApiSourceExtension apiSourceExtension) {
        this.apiSource = apiSourceExtension
        this.encoding = 'UTF-8'
        this.typeToObjectMapperMap.put(KEY_JSON, ObjectMapperFactory.createJson())
        this.typeToObjectMapperMap.put(KEY_YAML, ObjectMapperFactory.createYaml())
    }

    @Override
    void generate(Swagger source) {
        typeToObjectMapperMap.each { type, mapper -> configureMapper(mapper) }

        if (!apiSource.swaggerDirectory) {
            return
        }

        if (!isSorted) {
            Utils.sortSwagger(source, apiSource.shouldSortArrays)
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

        for (String format : apiSource.outputFormats) {
            try {
                switch (format.trim().toLowerCase()) {
                    case 'json':
                        writeFormatByObjectMapper(typeToObjectMapperMap.get(KEY_JSON), dir, fileName, ".json", source)
                        break
                    case 'yaml':
                        writeFormatByObjectMapper(typeToObjectMapperMap.get(KEY_YAML), dir, fileName, ".yaml", source)
                        break
                }
            } catch (Exception e) {
                throw new GenerateException(String.format("Declared output format [%s] is not supported.", format), e)
            }
        }
    }

    private void configureMapper(ObjectMapper objectMapper) {
        //Not come across an appropriate solution that is not deprecated yet
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        if (apiSource.shouldSortArrays) {
            objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        }

        if (apiSource.jsonExampleValues) {
            objectMapper.addMixIn(Property, PropertyExampleMixIn)
        }
    }

    private void writeFormatByObjectMapper(ObjectMapper objectMapper, File dir, String fileName, String suffix, Swagger source) {
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter())
        FileUtils.write(new File(dir, fileName + suffix), writer.writeValueAsString(source), encoding)
    }
}

package com.github.swagger.docgen.gradleplugin;

import com.benjaminsproule.swagger.gradleplugin.SwaggerPluginExtension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SwaggerDocumentGeneratorTest
 */
public class SwaggerDocumentGeneratorTest {

    private SwaggerDocumentGenerator generator = new SwaggerDocumentGenerator(this.getClass().getClassLoader());
    private String tmpSwaggerOutputDir;
    private SwaggerPluginExtension swagger;
    private String tempDir;

    @Before
    public void setup() throws Exception {
        tempDir = Files.createTempDirectory("swagger").toFile().getAbsolutePath();
        tmpSwaggerOutputDir = Files.createTempDirectory("apidocsf").toFile().getAbsolutePath();
        swagger = new SwaggerPluginExtension();
        swagger.setApiVersion("1.0");
        swagger.setBasePath("http://example.com");
        swagger.setSwaggerUIDocBasePath("http://localhost/apidocsf");
        swagger.setEndPoints(new String[]{"sample.api"});
        swagger.setOutputPath(tempDir + File.separator + "sample.html");
        swagger.setOutputTemplate("src/test/resources/strapdown.html.mustache");
        swagger.setSwaggerDirectory(tmpSwaggerOutputDir);
        swagger.setUseOutputFlatStructure(false);
    }

    @After
    public void tearDown() throws IOException {
        new File(tmpSwaggerOutputDir).delete();
        new File(tempDir).delete();
    }

    /**
     * {
     * "apiVersion" : "1.0",
     * "swaggerVersion" : "1.1",
     * "basePath" : "http://localhost/apidocsf",
     * "apis" : [ {
     * "path" : "/v2_car.{format}",
     * "description" : "Operations about cars"
     * }, {
     * "path" : "/garage.{format}",
     * "description" : "Operations about garages"
     * }, {
     * "path" : "/car.{format}",
     * "description" : "Operations about cars"
     * } ]
     * }
     */
    @Test
    public void testSwaggerOutputFlat() throws Exception {
        swagger.setSwaggerDirectory(tmpSwaggerOutputDir);
        swagger.setUseOutputFlatStructure(true);

        File output = new File(tmpSwaggerOutputDir);
        FileUtils.deleteDirectory(output);

        generator.generateSwaggerDocuments(swagger);
        List<String> flatfiles = new ArrayList<>();

        Collections.addAll(flatfiles, output.list());
        Collections.sort(flatfiles);
        Assert.assertEquals(flatfiles.get(0), "car.json");
        Assert.assertEquals(flatfiles.get(1), "garage.json");
        Assert.assertEquals(flatfiles.get(2), "service.json");
        Assert.assertEquals(flatfiles.get(3), "v2_car.json");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(FileUtils.readFileToByteArray(new File(output, "service.json")));
        JsonNode apis = node.get("apis");
        Assert.assertEquals(apis.size(), 3);
        List<String> pathInService = new ArrayList<>();
        for (JsonNode api : apis) {
            pathInService.add(api.get("path").asText());
        }
        Collections.sort(pathInService);
        Assert.assertEquals(pathInService.get(0), "/car.{format}");
        Assert.assertEquals(pathInService.get(1), "/garage.{format}");
        Assert.assertEquals(pathInService.get(2), "/v2_car.{format}");
    }

    @Test
    public void testSwaggerOutput() throws Exception {
        swagger.setSwaggerDirectory(tmpSwaggerOutputDir);
        swagger.setUseOutputFlatStructure(false);

        File output = new File(tmpSwaggerOutputDir);
        FileUtils.deleteDirectory(output);

        generator.generateSwaggerDocuments(swagger);
        List<File> outputFiles = new ArrayList<>();

        Collections.addAll(outputFiles, output.listFiles());
        Collections.sort(outputFiles);
        Assert.assertEquals(outputFiles.get(0).getName(), "car.json");
        Assert.assertEquals(outputFiles.get(1).getName(), "garage.json");
        Assert.assertEquals(outputFiles.get(2).getName(), "service.json");
        Assert.assertEquals(outputFiles.get(3).getName(), "v2");
        File v2 = outputFiles.get(3);
        Assert.assertTrue(v2.isDirectory());
        String[] v2carfile = v2.list();
        Assert.assertEquals(v2carfile[0], "car.json");


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(FileUtils.readFileToByteArray(new File(output, "service.json")));
        JsonNode apis = node.get("apis");
        Assert.assertEquals(apis.size(), 3);
        List<String> pathInService = new ArrayList<>();
        for (JsonNode api : apis) {
            pathInService.add(api.get("path").asText());
        }
        Collections.sort(pathInService);
        Assert.assertEquals(pathInService.get(0), "/car.{format}");
        Assert.assertEquals(pathInService.get(1), "/garage.{format}");
        Assert.assertEquals(pathInService.get(2), "/v2/car.{format}");
    }

    @Test
    public void testExecute() throws Exception {
        generator.generateSwaggerDocuments(swagger);
        InputStream expectedInputStream = new FileInputStream(new File("src/test/resources/sample.html"));
        InputStream actualInputStream = new FileInputStream(new File(swagger.getOutputPath()));
        int count = 0;
        while (true) {
            count++;
            int expect = expectedInputStream.read();
            int actual = actualInputStream.read();

            Assert.assertEquals("" + count, expect, actual);
            if (expect == -1) {
                break;
            }
        }
    }

    @Test
    public void testExecuteDirectoryCreated() throws Exception {
        testPath(tempDir + File.separator + "foo" + File.separator + "bar" + File.separator + "test.html");
        testPath(tempDir + File.separator + "bar" + File.separator + "test.html");
        testPath(tempDir + File.separator + "test.html");
        testPath(tempDir + File.separator + "test.html");
    }

    private void testPath(String path) throws com.github.swagger.docgen.GenerateException, IOException {
        swagger.setOutputPath(path);

        File file = new File(path);
        generator.generateSwaggerDocuments(swagger);
        Assert.assertTrue(file.exists());
    }
}

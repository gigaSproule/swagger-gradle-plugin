package com.github.swagger.docgen;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;

import static org.testng.AssertJUnit.assertTrue;

public class AbstractDocumentSourceTest {

    public static final String OUTPUT_PATH = "foo/bar.json";
    public static final String FLAT_OUTPUT_PATH = "foo_bar.json";

    @Test
    public void testResourcePathToFilename() throws Exception {
        AbstractDocumentSource abstractDocumentSource = getAbstractDocumentSource(true);
        assertEquals(FLAT_OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("/foo/bar"));
        assertEquals(FLAT_OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("/foo/bar/"));
        assertEquals(FLAT_OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("foo/bar"));
        assertEquals(FLAT_OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("foo/bar/"));
        assertEquals("bar.json", abstractDocumentSource.resourcePathToFilename("bar"));

        abstractDocumentSource = getAbstractDocumentSource(false);
        assertEquals(OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("/foo/bar"));
        assertEquals(OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("/foo/bar/"));
        assertEquals(OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("foo/bar"));
        assertEquals(OUTPUT_PATH, abstractDocumentSource.resourcePathToFilename("foo/bar/"));
        assertEquals("bar.json", abstractDocumentSource.resourcePathToFilename("bar"));
    }

    @Test
    public void testCreateFile() throws Exception {
        AbstractDocumentSource abstractDocumentSource = getAbstractDocumentSource(true);

        File build = abstractDocumentSource.createFile(new File("build"), OUTPUT_PATH);
        assertTrue(build.getPath(), FilenameUtils.equalsNormalized("build/foo/bar.json", build.getPath()));

        build = abstractDocumentSource.createFile(new File("build"), FLAT_OUTPUT_PATH);
        assertTrue(build.getPath(), FilenameUtils.equalsNormalized("build/foo_bar.json", build.getPath()));
    }

    private AbstractDocumentSource getAbstractDocumentSource(final boolean useOutputFlatStructure) {
        return new AbstractDocumentSource(null, null, null, null, useOutputFlatStructure) {
            @Override
            public void loadDocuments() throws Exception, GenerateException {

            }
        };
    }
}

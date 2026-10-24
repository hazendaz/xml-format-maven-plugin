/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 */
package au.com.acegi.xmlformat;

import static au.com.acegi.xmlformat.IOUtil.hash;
import static au.com.acegi.xmlformat.TestUtil.getResource;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link IOUtil}.
 */
public class IOTest {

    /** The tmp. */
    @TempDir
    private File tmp;

    /**
     * Hash1.
     *
     * @throws IOException the io exception
     */
    @Test
    void hash1() throws IOException {
        testHash("/test1-in.xml", 459_402_491L);
    }

    /**
     * Hash2.
     *
     * @throws IOException the io exception
     */
    @Test
    void hash2() throws IOException {
        testHash("/test2-in.xml", 1_687_393_391L);
    }

    /**
     * Hash invalid.
     *
     * @throws IOException the io exception
     */
    @Test
    void hashInvalid() throws IOException {
        testHash("/invalid.xml", 2_274_913_643L);
    }

    /**
     * Hash file matches stream hash.
     *
     * @throws IOException the io exception
     */
    @Test
    void hashFileMatchesStreamHash() throws IOException {
        final File file = Files.createTempFile(tmp.toPath(), "junit", ".xml").toFile();
        stringToFile("<root><child/></root>", file);

        final long fileHash = hash(file);
        try (InputStream in = java.nio.file.Files.newInputStream(file.toPath())) {
            final long streamHash = hash(in);
            assertThat(fileHash, is(streamHash));
        }
    }

    /**
     * Hash different content produces different hash.
     *
     * @throws IOException the io exception
     */
    @Test
    void hashDifferentContentProducesDifferentHash() throws IOException {
        final File file1 = Files.createTempFile(tmp.toPath(), "junit-a", ".xml").toFile();
        final File file2 = Files.createTempFile(tmp.toPath(), "junit-b", ".xml").toFile();
        stringToFile("<root><child/></root>", file1);
        stringToFile("<root><other/></root>", file2);

        assertThat(hash(file1), not(hash(file2)));
    }

    /**
     * Hash same content produces same hash.
     *
     * @throws IOException the io exception
     */
    @Test
    void hashSameContentProducesSameHash() throws IOException {
        final File file1 = Files.createTempFile(tmp.toPath(), "junit-c", ".xml").toFile();
        final File file2 = Files.createTempFile(tmp.toPath(), "junit-d", ".xml").toFile();
        final String content = "<root><child/></root>";
        stringToFile(content, file1);
        stringToFile(content, file2);

        assertThat(hash(file1), is(hash(file2)));
    }

    /**
     * Test hash.
     *
     * @param resource the resource
     * @param expected the expected
     * @throws IOException the io exception
     */
    private void testHash(final String resource, final long expected) throws IOException {
        try (InputStream in = getResource(resource)) {
            final long hash = hash(in);
            assertThat(hash, is(expected));
        }
    }

}

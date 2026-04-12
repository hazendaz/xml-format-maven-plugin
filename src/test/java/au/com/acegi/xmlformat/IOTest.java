/*
 * XML Format Maven Plugin (https://github.com/acegi/xml-format-maven-plugin)
 *
 * Copyright 2011-2026 Acegi Technology Pty Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link IOUtil}.
 */
public class IOTest {

    @TempDir
    private File tmp;

    @Test
    void hash1() throws IOException {
        testHash("/test1-in.xml", 459_402_491L);
    }

    @Test
    void hash2() throws IOException {
        testHash("/test2-in.xml", 1_687_393_391L);
    }

    @Test
    void hashInvalid() throws IOException {
        testHash("/invalid.xml", 2_274_913_643L);
    }

    @Test
    void hashFileMatchesStreamHash() throws IOException {
        final File file = File.createTempFile("junit", ".xml", tmp);
        stringToFile("<root><child/></root>", file);

        final long fileHash = hash(file);
        try (InputStream in = java.nio.file.Files.newInputStream(file.toPath())) {
            final long streamHash = hash(in);
            assertThat(fileHash, is(streamHash));
        }
    }

    @Test
    void hashDifferentContentProducesDifferentHash() throws IOException {
        final File file1 = File.createTempFile("junit-a", ".xml", tmp);
        final File file2 = File.createTempFile("junit-b", ".xml", tmp);
        stringToFile("<root><child/></root>", file1);
        stringToFile("<root><other/></root>", file2);

        assertThat(hash(file1), not(hash(file2)));
    }

    @Test
    void hashSameContentProducesSameHash() throws IOException {
        final File file1 = File.createTempFile("junit-c", ".xml", tmp);
        final File file2 = File.createTempFile("junit-d", ".xml", tmp);
        final String content = "<root><child/></root>";
        stringToFile(content, file1);
        stringToFile(content, file2);

        assertThat(hash(file1), is(hash(file2)));
    }

    private void testHash(final String resource, final long expected) throws IOException {
        try (InputStream in = getResource(resource)) {
            final long hash = hash(in);
            assertThat(hash, is(expected));
        }
    }

}

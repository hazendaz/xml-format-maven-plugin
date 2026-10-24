/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 */
package au.com.acegi.xmlformat;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;

/**
 * Utility methods required by the test package.
 */
final class TestUtil {

    /**
     * Instantiates a new test util.
     */
    private TestUtil() {
    }

    /**
     * File to string.
     *
     * @param in the in
     * @return the string
     */
    static String fileToString(final File in) {
        try (InputStream is = Files.newInputStream(in.toPath())) {
            return streamToString(is);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Get resource.
     *
     * @param resource the resource
     * @return the input stream
     */
    static InputStream getResource(final String resource) {
        final InputStream in = FormatUtilTest.class.getResourceAsStream(resource);
        assertThat(resource + " not found", in, not(nullValue()));
        return in;
    }

    /**
     * Stream to string.
     *
     * @param in the in
     * @return the string
     */
    static String streamToString(final InputStream in) {
        final StringWriter sw = new StringWriter();
        final InputStreamReader reader = new InputStreamReader(in, UTF_8);
        final char[] buffer = new char[4_096];
        int n;
        try {
            while (-1 != (n = reader.read(buffer))) {
                sw.write(buffer, 0, n);
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return sw.toString();
    }

    /**
     * String to file.
     *
     * @param msg the msg
     * @param out the out
     */
    static void stringToFile(final String msg, final File out) {
        try (OutputStream fos = Files.newOutputStream(out.toPath());
                Writer writer = new OutputStreamWriter(fos, UTF_8)) {
            writer.append(msg);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}

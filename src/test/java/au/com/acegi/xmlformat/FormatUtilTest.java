/*
 * XML Format Maven Plugin (https://github.com/acegi/xml-format-maven-plugin)
 *
 * Copyright 2011-2025 Acegi Technology Pty Limited.
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

import static java.nio.charset.StandardCharsets.UTF_8;

import static au.com.acegi.xmlformat.FormatUtil.format;
import static au.com.acegi.xmlformat.FormatUtil.formatInPlace;
import static au.com.acegi.xmlformat.TestUtil.getResource;
import static au.com.acegi.xmlformat.TestUtil.streamToString;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests {@link FormatUtil}. */
public class FormatUtilTest {

    private static final String FORMATTED_XML = "<xml><hello/></xml>";
    private static final String UNFORMATTED_XML = "<xml>   <hello/> </xml>";

    @TempDir
    private File tmp;

    @Test
    void formattedWillNotChange() throws DocumentException, IOException {
        inPlaceChange(FORMATTED_XML, false);
    }

    @Test
    void test1() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndentSize(4);
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        testInOut(1, fmt);
    }

    @Test
    void test1KeepBlankLines() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndentSize(4);
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        fmt.setKeepBlankLines(true);
        testInOut(1, fmt);
    }

    @Test
    void test2() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndentSize(2);
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        testInOut(2, fmt);
    }

    @Test
    void test2KeepBlankLines() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndentSize(2);
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        fmt.setKeepBlankLines(true);
        testInOut(2, fmt);
    }

    @Test
    void test3() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndentSize(2);
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        testInOut(3, fmt);
    }

    @Test
    void test4() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndent("\t");
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        testInOut(4, fmt);
    }

    @Test
    void test4KeepBlankLines() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndent("\t");
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        fmt.setKeepBlankLines(true);
        testInOut(4, fmt);
    }

    @Test
    void test5() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndent("    ");
        fmt.setNewLineAfterDeclaration(false);
        fmt.setPadText(false);
        fmt.setTrimText(true);
        testInOut(5, fmt);
    }

    /**
     * New lines between the XML declaration and the root elements are ignored at the parse level it seems, they don't
     * reach the XMLWriter. Not ideal, but believe we can leave with this exception
     */
    @Test
    void test5KeepBlankLines() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndent("    ");
        // Set to true to keep the new line given keep blank lines will not
        fmt.setNewLineAfterDeclaration(true);
        fmt.setPadText(false);
        fmt.setTrimText(true);
        fmt.setKeepBlankLines(true);
        testInOut(5, fmt);
    }

    /**
     * New lines between the XML declaration and the root elements are ignored at the parse level it seems, they don't
     * reach the XMLWriter. Not ideal, but believe we can leave with this exception
     */
    @Test
    void test6() throws DocumentException, IOException {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setIndent("    ");
        // Set to true to keep the new line given keep blank lines will not
        fmt.setNewLineAfterDeclaration(true);
        fmt.setPadText(false);
        fmt.setTrimText(true);
        fmt.setKeepBlankLines(true);
        testInOut(6, fmt);
    }

    @Test
    void testInvalid() throws DocumentException, IOException {
        assertThrows(DocumentException.class, () -> {
            try (InputStream in = getResource("/invalid.xml")) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                format(in, out, new XmlOutputFormat());
            }
        });
    }

    @Test
    void unformattedWillChange() throws DocumentException, IOException {
        inPlaceChange(UNFORMATTED_XML, true);
    }

    private void inPlaceChange(final String txt, final boolean shouldChange) throws DocumentException, IOException {
        final File file = File.createTempFile("junit", null, tmp);
        stringToFile(txt, file);

        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setSuppressDeclaration(true);
        fmt.setIndent("");
        fmt.setNewlines(false);

        final boolean written = formatInPlace(file, fmt);
        assertThat(written, is(shouldChange));
    }

    private void testInOut(final int id, final XmlOutputFormat fmt) throws DocumentException, IOException {
        try (InputStream in = getResource("/test" + id + "-in.xml")) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            format(in, out, fmt);

            final String received = new String(out.toByteArray(), UTF_8);
            final String expected = streamToString(getResource(getOutputFileName(id, fmt)));
            assertThat(received, is(expected));
        }
    }

    private String getOutputFileName(final int id, final XmlOutputFormat fmt) {
        if (fmt.isKeepBlankLines()) {
            return "/test" + id + "-out-kbl.xml";
        }
        return "/test" + id + "-out.xml";
    }
}

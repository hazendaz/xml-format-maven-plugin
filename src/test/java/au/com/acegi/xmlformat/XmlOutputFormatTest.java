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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link XmlOutputFormat}.
 */
public class XmlOutputFormatTest {

    @Test
    void defaultIndentSizeIsTwo() {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        // Default indent is 2 spaces, represented as a string of length 2
        assertThat(fmt.getIndent().length(), is(2));
    }

    @Test
    void defaultNewlinesIsTrue() {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        assertThat(fmt.isNewlines(), is(true));
    }

    @Test
    void defaultTrimTextIsTrue() {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        assertThat(fmt.isTrimText(), is(true));
    }

    @Test
    void defaultPadTextIsTrue() {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        assertThat(fmt.isPadText(), is(true));
    }

    @Test
    void defaultKeepBlankLinesIsFalse() {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        assertThat(fmt.isKeepBlankLines(), is(false));
    }

    @Test
    void setKeepBlankLinesToTrue() {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setKeepBlankLines(true);
        assertThat(fmt.isKeepBlankLines(), is(true));
    }

    @Test
    void setKeepBlankLinesToFalse() {
        final XmlOutputFormat fmt = new XmlOutputFormat();
        fmt.setKeepBlankLines(true);
        fmt.setKeepBlankLines(false);
        assertThat(fmt.isKeepBlankLines(), is(false));
    }
}

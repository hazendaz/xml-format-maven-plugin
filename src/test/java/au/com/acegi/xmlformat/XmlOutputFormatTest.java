/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
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

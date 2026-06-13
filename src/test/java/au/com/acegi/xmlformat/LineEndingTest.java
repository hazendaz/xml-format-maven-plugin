/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 */
package au.com.acegi.xmlformat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link LineEnding}.
 */
public class LineEndingTest {

    @Test
    void systemEndingIsSystemSeparator() {
        assertThat(LineEnding.SYSTEM.getChars(), is(System.lineSeparator()));
    }

    @Test
    void lfEndingIsNewline() {
        assertThat(LineEnding.LF.getChars(), is("\n"));
    }

    @Test
    void crlfEndingIsCarriageReturnNewline() {
        assertThat(LineEnding.CRLF.getChars(), is("\r\n"));
    }

    @Test
    void crEndingIsCarriageReturn() {
        assertThat(LineEnding.CR.getChars(), is("\r"));
    }

    @Test
    void allValuesAreAccessible() {
        final LineEnding[] values = LineEnding.values();
        assertThat(values, notNullValue());
        assertThat(values.length, is(4));
    }

    @Test
    void valueOfSystem() {
        assertThat(LineEnding.valueOf("SYSTEM"), is(LineEnding.SYSTEM));
    }

    @Test
    void valueOfLf() {
        assertThat(LineEnding.valueOf("LF"), is(LineEnding.LF));
    }

    @Test
    void valueOfCrlf() {
        assertThat(LineEnding.valueOf("CRLF"), is(LineEnding.CRLF));
    }

    @Test
    void valueOfCr() {
        assertThat(LineEnding.valueOf("CR"), is(LineEnding.CR));
    }
}

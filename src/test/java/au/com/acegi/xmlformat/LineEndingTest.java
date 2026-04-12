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

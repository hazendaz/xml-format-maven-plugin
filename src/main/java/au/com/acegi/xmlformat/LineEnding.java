/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 */
package au.com.acegi.xmlformat;

/**
 * Valid line endings for use by {@link XmlFormatPlugin#lineEnding}.
 */
public enum LineEnding {

    /**
     * Use the system default line ending.
     */
    SYSTEM(),

    /**
     * Use the newline character. Typical on Unix and Unix-like systems.
     */
    LF("\n"),

    /**
     * Use the carriage return and new line characters. Typical on Windows.
     */
    CRLF("\r\n"),

    /**
     * Use the carriage return character.
     */
    CR("\r");

    private final String chars;

    LineEnding() {
        this.chars = System.lineSeparator();
    }

    LineEnding(final String value) {
        this.chars = value;
    }

    /**
     * Gets the chars.
     *
     * @return the chars
     */
    public String getChars() {
        return this.chars;
    }

}

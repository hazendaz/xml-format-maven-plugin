/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 */
package com.github.hazendaz.maven.xml_format_maven_plugin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

/** Tests coverage of generated {@link HelpMojo}. */
class HelpMojoTest {

    @Test
    void executeSupportsDetailedOutputWithInvalidSizing() {
        final HelpMojo mojo = new HelpMojo();
        setField(mojo, "detail", true);
        setField(mojo, "lineLength", 0);
        setField(mojo, "indentSize", 0);

        assertDoesNotThrow(mojo::execute);
    }

    @Test
    void executeSupportsSpecificGoalOutput() {
        final HelpMojo mojo = new HelpMojo();
        setField(mojo, "detail", true);
        setField(mojo, "goal", "check");

        assertDoesNotThrow(mojo::execute);
    }

    @Test
    void executeSupportsUnknownGoalWithoutFailure() {
        final HelpMojo mojo = new HelpMojo();
        setField(mojo, "goal", "unknown-goal");

        assertDoesNotThrow(mojo::execute);
    }

    private static void setField(final HelpMojo mojo, final String fieldName, final Object value) {
        try {
            final Field field = HelpMojo.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(mojo, value);
        } catch (final ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

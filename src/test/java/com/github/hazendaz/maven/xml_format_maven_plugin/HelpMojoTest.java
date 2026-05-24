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

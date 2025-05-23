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

import static au.com.acegi.xmlformat.FormatUtil.formatInPlace;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PREPARE_PACKAGE;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugins.annotations.Mojo;
import org.dom4j.DocumentException;

/**
 * Finds the XML files in a project and automatically reformats them.
 */
@Mojo(name = "xml-format", defaultPhase = PREPARE_PACKAGE, threadSafe = true)
public final class XmlFormatPlugin extends AbstractXmlPlugin {

    @Override
    protected boolean processFile(final File input, final XmlOutputFormat fmt) throws DocumentException, IOException {
        final boolean changed = formatInPlace(input, fmt);
        if (getLog().isDebugEnabled()) {
            final String msg = changed ? "Formatted" : "Unchanged";
            getLog().debug("[xml-format] " + msg + ": " + input);
        }
        return changed;
    }

    @Override
    protected void afterAllProcessed(final boolean neededFormatting) {
        // nothing to do
    }
}

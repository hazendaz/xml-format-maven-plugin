/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
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

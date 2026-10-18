/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 */
package au.com.acegi.xmlformat;

import static au.com.acegi.xmlformat.TestUtil.fileToString;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration-style tests for {@link AbstractXmlPlugin} subclasses.
 *
 * <p>
 * Mojos are instantiated directly. Package-private setters in {@link AbstractXmlPlugin} handle mandatory parameters,
 * while Java reflection configures private {@code @Parameter} fields whose Maven defaults differ from their Java
 * field defaults.
 */
class AbstractXmlPluginTest {

    private static final String UNFORMATTED_XML = "<root>  <child/>  </root>";
    private static final String INCLUDE_ALL_XML = "**/*.xml";

    @TempDir
    private File tmp;

    // -------------------------------------------------------------------------
    // Mojo instantiation
    // -------------------------------------------------------------------------

    @Test
    void xmlFormatMojoCanBeInstantiated() {
        assertThat(new XmlFormatPlugin(), notNullValue());
    }

    @Test
    void xmlCheckMojoCanBeInstantiated() {
        assertThat(new XmlCheckPlugin(), notNullValue());
    }

    // -------------------------------------------------------------------------
    // xml-format goal — parameter variations
    // -------------------------------------------------------------------------

    @Test
    void xmlFormatWithDefaultSettings() throws Exception {
        final File proj = createProjectDir("default");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted, containsString("\n"));
    }

    @Test
    void xmlFormatWithTabIndent() throws Exception {
        final File proj = createProjectDir("tabindent");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "tabIndent", true);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted, containsString("\t"));
    }

    @Test
    void xmlFormatWithCrlfLineEnding() throws Exception {
        final File proj = createProjectDir("crlf");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "lineEnding", LineEnding.CRLF);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted, containsString("\r\n"));
    }

    @Test
    void xmlFormatWithCrLineEnding() throws Exception {
        final File proj = createProjectDir("cr");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "lineEnding", LineEnding.CR);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted, containsString("\r"));
    }

    @Test
    void xmlFormatWithKeepBlankLines() throws Exception {
        final File proj = createProjectDir("keepblanklines");
        final File target = createTargetDir(proj);
        final String xmlWithBlankLine = "<root>\n\n  <child/>\n\n</root>";
        writeXmlFile(proj, "sample.xml", xmlWithBlankLine);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "keepBlankLines", true);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted, containsString("\n\n"));
    }

    @Test
    void xmlFormatWithExpandEmptyElements() throws Exception {
        final File proj = createProjectDir("expand");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", "<root><child/></root>");

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "expandEmptyElements", true);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted, containsString("<child></child>"));
    }

    @Test
    void xmlFormatWithCustomIndentSize() throws Exception {
        final File proj = createProjectDir("indent4");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "indentSize", 4);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted, containsString("    <child"));
    }

    @Test
    void xmlFormatSkipDoesNotModifyFiles() throws Exception {
        final File proj = createProjectDir("skip-format");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        mojo.setSkip(true);
        mojo.execute();

        assertThat(fileToString(proj.toPath().resolve("sample.xml").toFile()), is(UNFORMATTED_XML));
    }

    @Test
    void xmlFormatEmptyFileIsIgnored() throws Exception {
        final File proj = createProjectDir("empty");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "empty.xml", "");

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        mojo.execute();

        assertThat(fileToString(proj.toPath().resolve("empty.xml").toFile()), is(""));
    }

    @Test
    void xmlFormatWithSuppressedDeclaration() throws Exception {
        final File proj = createProjectDir("suppress");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "suppressDeclaration", true);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        assertThat(formatted.contains("<?xml"), is(false));
    }

    @Test
    void xmlFormatWithOmitEncoding() throws Exception {
        final File proj = createProjectDir("omit-encoding");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        setFieldValue(mojo, "omitEncoding", true);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        // Encoding attribute should be absent from the XML declaration.
        assertThat(formatted.contains("encoding="), is(false));
    }

    // -------------------------------------------------------------------------
    // xml-check goal variations
    // -------------------------------------------------------------------------

    @Test
    void xmlCheckSkipDoesNotFailOnUnformattedFiles() throws Exception {
        final File proj = createProjectDir("skip-check");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlCheckPlugin mojo = configureMojo(new XmlCheckPlugin(), proj, target);
        mojo.setSkip(true);
        mojo.execute();
    }

    @Test
    void xmlCheckPassesOnAlreadyFormattedFile() throws Exception {
        final File proj = createProjectDir("check-pass");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        // First format the file so that the check will pass.
        configureMojo(new XmlFormatPlugin(), proj, target).execute();

        // Now verify the check mojo considers the file already formatted.
        configureMojo(new XmlCheckPlugin(), proj, target).execute();
    }

    @Test
    void xmlCheckFailsOnUnformattedFile() throws Exception {
        final File proj = createProjectDir("check-fail");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlCheckPlugin mojo = configureMojo(new XmlCheckPlugin(), proj, target);
        assertThrows(MojoExecutionException.class, mojo::execute);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private File createProjectDir(final String name) throws IOException {
        final File proj = tmp.toPath().resolve(name).toFile();
        if (!proj.mkdirs()) {
            throw new IOException("Could not create project dir: " + proj);
        }
        return proj;
    }

    private File createTargetDir(final File proj) throws IOException {
        final File target = proj.toPath().resolve("target").toFile();
        if (!target.mkdirs()) {
            throw new IOException("Could not create target dir: " + target);
        }
        return target;
    }

    private void writeXmlFile(final File dir, final String filename, final String content) {
        stringToFile(content, dir.toPath().resolve(filename).toFile());
    }

    /**
     * Applies the mandatory parameters to any {@link AbstractXmlPlugin} mojo via package-private setters.
     * Also sets {@code @Parameter} fields whose Maven defaults differ from their Java field defaults.
     */
    private <T extends AbstractXmlPlugin> T configureMojo(
            final T mojo, final File baseDir, final File targetDir) throws ReflectiveOperationException {
        mojo.setBaseDirectory(baseDir);
        mojo.setTargetDirectory(targetDir);
        mojo.setIncludes(INCLUDE_ALL_XML);
        mojo.setExcludes();

        setFieldValue(mojo, "newlines", true);
        setFieldValue(mojo, "trimText", true);
        setFieldValue(mojo, "indentSize", 2);

        return mojo;
    }

    /**
     * Sets a field value using reflection, including private fields declared by a superclass.
     */
    private void setFieldValue(final Object target, final String fieldName, final Object value)
            throws ReflectiveOperationException {
        Class<?> type = target.getClass();

        while (type != null) {
            try {
                final Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (final NoSuchFieldException exception) {
                type = type.getSuperclass();
            }
        }

        throw new NoSuchFieldException(fieldName);
    }
}

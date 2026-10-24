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

    /** The unformatted xml. */
    private static final String UNFORMATTED_XML = "<root>  <child/>  </root>";
    /** The include all xml. */
    private static final String INCLUDE_ALL_XML = "**/*.xml";

    /** The tmp. */
    @TempDir
    private File tmp;

    // -------------------------------------------------------------------------
    // Mojo instantiation
    // -------------------------------------------------------------------------

    /**
     * Xml format mojo can be instantiated.
     */
    @Test
    void xmlFormatMojoCanBeInstantiated() {
        assertThat(new XmlFormatPlugin(), notNullValue());
    }

    /**
     * Xml check mojo can be instantiated.
     */
    @Test
    void xmlCheckMojoCanBeInstantiated() {
        assertThat(new XmlCheckPlugin(), notNullValue());
    }

    // -------------------------------------------------------------------------
    // xml-format goal — parameter variations
    // -------------------------------------------------------------------------

    /**
     * Xml format with default settings.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format with tab indent.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format with crlf line ending.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format with cr line ending.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format with keep blank lines.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format with expand empty elements.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format with custom indent size.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format skip does not modify files.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format empty file is ignored.
     *
     * @throws Exception the exception
     */
    @Test
    void xmlFormatEmptyFileIsIgnored() throws Exception {
        final File proj = createProjectDir("empty");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "empty.xml", "");

        final XmlFormatPlugin mojo = configureMojo(new XmlFormatPlugin(), proj, target);
        mojo.execute();

        assertThat(fileToString(proj.toPath().resolve("empty.xml").toFile()), is(""));
    }

    /**
     * Xml format with suppressed declaration.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml format with omit encoding.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml check skip does not fail on unformatted files.
     *
     * @throws Exception the exception
     */
    @Test
    void xmlCheckSkipDoesNotFailOnUnformattedFiles() throws Exception {
        final File proj = createProjectDir("skip-check");
        final File target = createTargetDir(proj);
        writeXmlFile(proj, "sample.xml", UNFORMATTED_XML);

        final XmlCheckPlugin mojo = configureMojo(new XmlCheckPlugin(), proj, target);
        mojo.setSkip(true);
        mojo.execute();
    }

    /**
     * Xml check passes on already formatted file.
     *
     * @throws Exception the exception
     */
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

    /**
     * Xml check fails on unformatted file.
     *
     * @throws Exception the exception
     */
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

    /**
     * Create project dir.
     *
     * @param name the name
     * @return the file
     * @throws IOException the io exception
     */
    private File createProjectDir(final String name) throws IOException {
        final File proj = tmp.toPath().resolve(name).toFile();
        if (!proj.mkdirs()) {
            throw new IOException("Could not create project dir: " + proj);
        }
        return proj;
    }

    /**
     * Create target dir.
     *
     * @param proj the proj
     * @return the file
     * @throws IOException the io exception
     */
    private File createTargetDir(final File proj) throws IOException {
        final File target = proj.toPath().resolve("target").toFile();
        if (!target.mkdirs()) {
            throw new IOException("Could not create target dir: " + target);
        }
        return target;
    }

    /**
     * Write xml file.
     *
     * @param dir the dir
     * @param filename the filename
     * @param content the content
     */
    private void writeXmlFile(final File dir, final String filename, final String content) {
        stringToFile(content, dir.toPath().resolve(filename).toFile());
    }

    /**
     * Applies the mandatory parameters to any {@link AbstractXmlPlugin} mojo via package-private setters.
     * Also sets {@code @Parameter} fields whose Maven defaults differ from their Java field defaults.
     
     * @param mojo the mojo
     * @param baseDir the base dir
     * @param targetDir the target dir
     * @return the t
     * @throws ReflectiveOperationException the reflective operation exception
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
     
     * @param target the target
     * @param fieldName the field name
     * @param value the value
     * @throws ReflectiveOperationException the reflective operation exception
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

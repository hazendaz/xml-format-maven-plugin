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

import static au.com.acegi.xmlformat.TestUtil.fileToString;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration-style tests for {@link AbstractXmlPlugin} subclasses using the Maven Plugin Testing Harness
 * ({@link MojoRule}).
 *
 * <p>
 * The harness is used specifically for its {@code setVariableValueToObject} utility, which reflects into private
 * {@code @Parameter} fields that have no package-private setters — such as {@code tabIndent}, {@code lineEnding},
 * {@code keepBlankLines}, {@code expandEmptyElements}, and {@code indentSize}.
 *
 * <p>
 * Mojos are created directly via {@code new} rather than Plexus container lookup (which requires a pre-generated Sisu
 * component index). The package-private setters in {@link AbstractXmlPlugin} handle the mandatory parameters while the
 * harness handles the remainder via reflection.
 */
public class AbstractXmlPluginMojoRuleTest {

    /** Exposes the protected {@code before}/{@code after} lifecycle hooks for use in JUnit 5. */
    private static final class TestMojoRule extends MojoRule {
        @Override
        public void before() throws Throwable {
            super.before();
        }

        @Override
        public void after() {
            super.after();
        }
    }

    private static final String UNFORMATTED_XML = "<root>  <child/>  </root>";
    private static final String INCLUDE_ALL_XML = "**/*.xml";

    @TempDir
    private File tmp;

    private final TestMojoRule rule = new TestMojoRule();

    @BeforeEach
    void setupHarness() throws Throwable {
        rule.before();
    }

    @AfterEach
    void tearDownHarness() {
        rule.after();
    }

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
    // xml-format goal — parameter variations via MojoRule reflection
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
        rule.setVariableValueToObject(mojo, "tabIndent", true);
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
        rule.setVariableValueToObject(mojo, "lineEnding", LineEnding.CRLF);
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
        rule.setVariableValueToObject(mojo, "lineEnding", LineEnding.CR);
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
        rule.setVariableValueToObject(mojo, "keepBlankLines", true);
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
        rule.setVariableValueToObject(mojo, "expandEmptyElements", true);
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
        rule.setVariableValueToObject(mojo, "indentSize", 4);
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
        rule.setVariableValueToObject(mojo, "suppressDeclaration", true);
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
        rule.setVariableValueToObject(mojo, "omitEncoding", true);
        mojo.execute();

        final String formatted = fileToString(proj.toPath().resolve("sample.xml").toFile());
        // Encoding attribute should be absent from the XML declaration
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

        // First format the file so that the check will pass
        configureMojo(new XmlFormatPlugin(), proj, target).execute();

        // Now verify the check mojo considers the file already formatted
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
     * Applies the mandatory parameters to any {@link AbstractXmlPlugin} mojo via the package-private setters.
     * Additionally uses the harness reflection helper to set the {@code @Parameter} fields whose Maven default values
     * differ from the Java field defaults (because no explicit field initializer is present in the source).
     */
    private <T extends AbstractXmlPlugin> T configureMojo(final T mojo, final File baseDir, final File targetDir)
            throws IllegalAccessException {
        mojo.setBaseDirectory(baseDir);
        mojo.setTargetDirectory(targetDir);
        mojo.setIncludes(INCLUDE_ALL_XML);
        mojo.setExcludes();
        // These @Parameter fields have defaultValue != Java field default, so we must set them explicitly.
        rule.setVariableValueToObject(mojo, "newlines", true);
        rule.setVariableValueToObject(mojo, "trimText", true);
        rule.setVariableValueToObject(mojo, "indentSize", 2);
        return mojo;
    }
}

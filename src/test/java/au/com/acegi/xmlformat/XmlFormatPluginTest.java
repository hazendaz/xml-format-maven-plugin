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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link XmlFormatPlugin}.
 */
public class XmlFormatPluginTest {

    /** The empty file name. */
    private static final String EMPTY_FILE_NAME = "empty.xml";
    /** The empty txt. */
    private static final String EMPTY_TXT = "";
    /** The err file name. */
    private static final String ERR_FILE_NAME = "error.xml";
    /** The err txt. */
    private static final String ERR_TXT = "<xml> <hello> hello not closed! </xml>";
    /** The no chg txt. */
    private static final String NO_CHG_TXT = "<xml> <leave-me-alone/> </xml>";
    /** The to chg txt. */
    private static final String TO_CHG_TXT = "<xml> <hello/> </xml>";
    /** The include all xml. */
    private static final String INCLUDE_ALL_XML = "**/*.xml";

    /** The tmp. */
    @TempDir
    private File tmp;
    /** The error. */
    private File error;
    /** The log. */
    @SuppressWarnings("PMD.ProperLogger")
    private Log log;
    /** The no change. */
    private File noChange;
    /** The proj. */
    private File proj;
    /** The target. */
    private File target;
    /** The to change. */
    private File toChange;

    /**
     * Before.
     *
     * @throws IOException the io exception
     */
    @BeforeEach
    void before() throws IOException {
        proj = newFolder(tmp, "junit");
        target = proj.toPath().resolve("target").toFile();
        assertThat(target.mkdir(), is(true));

        toChange = proj.toPath().resolve("my.xml").toFile();
        stringToFile(TO_CHG_TXT, toChange);

        noChange = target.toPath().resolve("exclude-me.xml").toFile();
        stringToFile(NO_CHG_TXT, noChange);

        final File empty = proj.toPath().resolve(EMPTY_FILE_NAME).toFile();
        stringToFile(EMPTY_TXT, empty);

        error = proj.toPath().resolve(ERR_FILE_NAME).toFile();
        stringToFile(ERR_TXT, error);

        assertThat(fileToString(toChange), is(TO_CHG_TXT));
        assertThat(fileToString(noChange), is(NO_CHG_TXT));
        assertThat(fileToString(empty), is(EMPTY_TXT));
        assertThat(fileToString(error), is(ERR_TXT));

        log = mock(Log.class);
    }

    /**
     * Plugin excludes error.
     *
     * @throws MojoExecutionException the mojo execution exception
     * @throws MojoFailureException the mojo failure exception
     */
    @Test
    void pluginExcludesError() throws MojoExecutionException, MojoFailureException {
        final XmlFormatPlugin plugin = new XmlFormatPlugin();
        plugin.setLog(log);
        when(log.isDebugEnabled()).thenReturn(true);
        when(log.isErrorEnabled()).thenReturn(true);

        plugin.setBaseDirectory(proj);
        plugin.setExcludes("**/" + ERR_FILE_NAME);
        plugin.setIncludes(INCLUDE_ALL_XML);
        plugin.setTargetDirectory(target);

        plugin.execute();

        verify(log, never()).isErrorEnabled();
        verify(log, atLeastOnce()).isDebugEnabled();
        verify(log, atLeastOnce()).debug(anyString());

        assertThat(fileToString(toChange), not(TO_CHG_TXT));
        assertThat(fileToString(noChange), is(NO_CHG_TXT));
        assertThat(fileToString(error), is(ERR_TXT));
    }

    /**
     * Plugin reports error.
     *
     * @throws MojoExecutionException the mojo execution exception
     */
    @Test
    @SuppressWarnings("PMD.JUnitUseExpected")
    void pluginReportsError() throws MojoExecutionException {
        final XmlFormatPlugin plugin = new XmlFormatPlugin();
        plugin.setLog(log);
        when(log.isDebugEnabled()).thenReturn(false);
        when(log.isErrorEnabled()).thenReturn(true);

        plugin.setBaseDirectory(proj);
        plugin.setExcludes("");
        plugin.setIncludes(INCLUDE_ALL_XML);
        plugin.setTargetDirectory(target);

        Assertions.assertThrows(MojoFailureException.class, () -> {
            plugin.execute();
            fail("Should have raised exception when handling error");
        });

        verify(log, atLeastOnce()).error(anyString(), any(Throwable.class));
        verify(log, atLeastOnce()).isDebugEnabled();
        verify(log, never()).debug(anyString());

        assertThat(fileToString(toChange), not(TO_CHG_TXT));
        assertThat(fileToString(noChange), is(NO_CHG_TXT));
        assertThat(fileToString(error), is(ERR_TXT));
    }

    /**
     * Plugin skip target folder.
     *
     * @throws MojoExecutionException the mojo execution exception
     * @throws MojoFailureException the mojo failure exception
     */
    @Test
    @SuppressWarnings("PMD.JUnitUseExpected")
    void pluginSkipTargetFolder() throws MojoExecutionException, MojoFailureException {
        final XmlFormatPlugin plugin = new XmlFormatPlugin();
        plugin.setLog(log);

        plugin.setSkipTargetFolder(false);
        when(log.isDebugEnabled()).thenReturn(true);
        when(log.isErrorEnabled()).thenReturn(true);

        plugin.setBaseDirectory(proj);
        plugin.setExcludes("**/" + ERR_FILE_NAME);
        plugin.setIncludes(INCLUDE_ALL_XML);
        plugin.setTargetDirectory(target);

        plugin.execute();

        assertThat(fileToString(toChange), not(TO_CHG_TXT));
        assertThat(fileToString(noChange), not(NO_CHG_TXT));
        assertThat(fileToString(error), is(ERR_TXT));
    }

    /**
     * Plugin skip.
     *
     * @throws MojoExecutionException the mojo execution exception
     * @throws MojoFailureException the mojo failure exception
     */
    @Test
    void pluginSkip() throws MojoExecutionException, MojoFailureException {
        final XmlFormatPlugin plugin = new XmlFormatPlugin();
        plugin.setLog(log);
        plugin.setSkip(true);
        when(log.isDebugEnabled()).thenReturn(true);
        when(log.isInfoEnabled()).thenReturn(true);
        when(log.isErrorEnabled()).thenReturn(true);

        plugin.setBaseDirectory(proj);
        plugin.setExcludes("**/" + ERR_FILE_NAME);
        plugin.setIncludes(INCLUDE_ALL_XML);
        plugin.setTargetDirectory(target);

        plugin.execute();

        verify(log, atLeastOnce()).info("[xml-format] Skipped");

        assertThat(fileToString(toChange), is(TO_CHG_TXT));
    }

    /**
     * New folder.
     *
     * @param root the root
     * @param subDirs the sub dirs
     * @return the file
     * @throws IOException the io exception
     */
    private static File newFolder(final File root, final String... subDirs) throws IOException {
        final String subFolder = String.join("/", subDirs);
        final File result = root.toPath().resolve(subFolder).toFile();
        if (!result.mkdirs()) {
            throw new IOException("Couldn't create folders " + root);
        }
        return result;
    }
}

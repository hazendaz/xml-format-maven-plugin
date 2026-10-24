/*
 * SPDX-License-Identifier: Apache-2.0
 * See LICENSE file for details.
 *
 * Copyright 2025-2026 Hazendaz
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 */
package au.com.acegi.xmlformat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Node;
import org.dom4j.io.XMLWriter;

/**
 * Subclass of {@link XMLWriter} that preserves blank lines in outupt (at most one of them, among two subsequent tags).
 */
class BlankLinesWriter extends XMLWriter {

    /**
     * Instantiates a new blank lines writer.
     *
     * @param out the out
     * @param fmt the fmt
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    BlankLinesWriter(final OutputStream out, final XmlOutputFormat fmt) throws UnsupportedEncodingException {
        super(out, fmt);
    }

    @Override
    protected void writeString(final String text) throws IOException {
        if (text == null || text.isEmpty()) {
            return;
        }

        String input = text;
        if (isEscapeText()) {
            input = escapeElementEntities(text);
        }

        if (getOutputFormat().isTrimText()) {
            boolean first = true;
            final String[] tokens = input.split("[ \\t\\r\\f]+");

            final NewLinesHandler newLinesHandler = new NewLinesHandler();
            for (int i = 0; i < tokens.length; i++) {
                final String token = tokens[i];

                // Only if more tokens exist, continue
                if (newLinesHandler.processToken(token, i < tokens.length - 1)) {
                    continue;
                }

                if (first) {
                    first = false;
                    if (lastOutputNodeType == Node.TEXT_NODE) {
                        writer.write(" ");
                    }
                } else {
                    writer.write(" ");
                }

                writer.write(token.trim());
                lastOutputNodeType = Node.TEXT_NODE;
            }
            newLinesHandler.finished();
        } else {
            lastOutputNodeType = Node.TEXT_NODE;
            writer.write(input);
        }
    }

    /**
     * The Class NewLinesHandler.
     */
    private final class NewLinesHandler {
        /** The new lines count. */
        private int newLinesCount;

        /**
         * Processes the token, counting the newlines and producing at most one in output.
         *
         * @param token
         *            The token to be written
         * @param hasMoreTokens
         *            Does more tokens exist
         *
         * @return True if the token needs to be skipped (it's a newline or a set of newlines)
         *
         * @throws IOException
         *             If an I/O error occurs.
         */
        private boolean processToken(final String token, final boolean hasMoreTokens) throws IOException {
            final int tokenNewLines = StringUtils.countMatches(token, '\n');
            if (tokenNewLines > 0) {
                newLinesCount += tokenNewLines;
                return hasMoreTokens;
            }
            if (newLinesCount > 1) {
                writer.write("\n");
                newLinesCount = 0;
            }
            return false;
        }

        /**
         * Marks the end of token streams, allows to emit a last newlines if the last tokens were all newlines.
         *
         * @throws IOException
         *             If an I/O error occurs.
         */
        private void finished() throws IOException {
            if (newLinesCount > 1) {
                writer.write("\n");
            }
        }
    }
}

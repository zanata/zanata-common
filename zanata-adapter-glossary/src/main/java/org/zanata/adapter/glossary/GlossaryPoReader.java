/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.adapter.glossary;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.StringUtils;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.catalog.parse.MessageStreamParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.zanata.common.LocaleId;
import org.zanata.rest.dto.GlossaryEntry;
import org.zanata.rest.dto.GlossaryTerm;

/**
 *
 * @author Alex Eng <a
 *         href="mailto:aeng@redhat.com">aeng@redhat.GlossaryPoReadercom</a>
 *
 **/
public class GlossaryPoReader extends AbstractGlossaryPushReader {
    private static final Logger log = LoggerFactory
            .getLogger(GlossaryPoReader.class);

    private final LocaleId srcLang;
    private final LocaleId transLang;
    private final int batchSize;

    /**
     * This class will close the reader
     *
     * @param srcLang
     * @param transLang
     * @param batchSize
     */
    public GlossaryPoReader(LocaleId srcLang, LocaleId transLang,
            int batchSize) {
        this.srcLang = srcLang;
        this.transLang = transLang;
        this.batchSize = batchSize;
    }

    @Override
    public List<List<GlossaryEntry>> extractGlossary(Reader reader) throws IOException {
        ReaderInputStream ris = new ReaderInputStream(reader);
        try {
            InputSource potInputSource = new InputSource(ris);
            potInputSource.setEncoding("utf8");
            return extractTemplate(potInputSource);
        } finally {
            ris.close();
        }
    }

    private List<List<GlossaryEntry>>
            extractTemplate(InputSource potInputSource) {
        int entryCount = 0;
        MessageStreamParser messageParser = createParser(potInputSource);
        List<List<GlossaryEntry>> glossaries =
                new ArrayList<List<GlossaryEntry>>();

        List<GlossaryEntry> glossaryEntries = new ArrayList<GlossaryEntry>();

        while (messageParser.hasNext()) {
            Message message = messageParser.next();

            if (message.isHeader()) {
                // log.warn("term: [{}] is ignored - message is header",
                // message.getMsgid());
            } else if (message.isObsolete()) {
                // log.warn("term: [{}] is ignored - message obsolete",
                // message.getMsgid());
            } else if (message.isPlural()) {
                // log.warn("term: [{}] is ignored - message is plural",
                // message.getMsgid());
            } else if (message.isFuzzy()) {
                log.warn("term: [{}] is ignored - state fuzzy",
                        message.getMsgid());
            } else {
                GlossaryEntry entry = new GlossaryEntry();
                entry.setSrcLang(srcLang);

                GlossaryTerm srcTerm = new GlossaryTerm();
                srcTerm.setLocale(srcLang);
                srcTerm.setContent(message.getMsgid());

                GlossaryTerm targetTerm = new GlossaryTerm();
                targetTerm.setLocale(transLang);
                targetTerm.setContent(message.getMsgstr());

                StringBuilder sb = new StringBuilder();
                if (!StringUtils.isEmpty(entry.getSourceReference())) {
                    sb.append(entry.getSourceReference());
                }
                if (!StringUtils.isEmpty(StringUtils.join(
                        message.getSourceReferences(), "\n"))) {
                    sb.append(StringUtils.join(
                            message.getSourceReferences(), "\n"));
                }

                entry.setSourceReference(sb.toString());

                String description = Joiner.on("\n").skipNulls()
                    .join(message.getExtractedComments());

                entry.setDescription(description);

                String targetComment = Joiner.on("\n").skipNulls()
                    .join(message.getComments());

                targetTerm.setComment(targetComment);

                entry.getGlossaryTerms().add(srcTerm);
                entry.getGlossaryTerms().add(targetTerm);

                glossaryEntries.add(entry);
                entryCount++;
            }

            if (entryCount == batchSize || !messageParser.hasNext()) {
                glossaries.add(glossaryEntries);
                entryCount = 0;
                glossaryEntries = new ArrayList<GlossaryEntry>();
            }
        }
        return glossaries;
    }

    static MessageStreamParser createParser(InputSource inputSource) {
        MessageStreamParser messageParser;
        if (inputSource.getCharacterStream() != null)
            messageParser =
                    new MessageStreamParser(inputSource.getCharacterStream());
        else if (inputSource.getByteStream() != null) {
            if (inputSource.getEncoding() != null)
                messageParser =
                        new MessageStreamParser(inputSource.getByteStream(),
                                Charset.forName(inputSource.getEncoding()));
            else
                messageParser =
                        new MessageStreamParser(inputSource.getByteStream(),
                                Charset.forName("UTF-8"));
        } else if (inputSource.getSystemId() != null) {
            try {
                URL url = new URL(inputSource.getSystemId());

                if (inputSource.getEncoding() != null)
                    messageParser =
                            new MessageStreamParser(url.openStream(),
                                    Charset.forName(inputSource.getEncoding()));
                else
                    messageParser =
                            new MessageStreamParser(url.openStream(),
                                    Charset.forName("UTF-8"));
            } catch (IOException e) {
                throw new RuntimeException(
                        "failed to get input from url in inputSource", e);
            }
        } else
            throw new RuntimeException("not a valid inputSource");

        return messageParser;
    }
}

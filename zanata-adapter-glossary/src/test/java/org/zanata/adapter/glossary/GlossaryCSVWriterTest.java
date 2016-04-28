package org.zanata.adapter.glossary;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.zanata.common.LocaleId;
import org.zanata.rest.dto.GlossaryEntry;
import org.zanata.rest.dto.GlossaryTerm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Alex Eng<a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class GlossaryCSVWriterTest {

    @Test
    public void glossaryWriteTest1() throws IOException {
        GlossaryCSVWriter writer = new GlossaryCSVWriter();
        String filePath = "target/output.csv";

        FileWriter fileWriter = new FileWriter(filePath);
        LocaleId srcLocale = LocaleId.EN_US;

        List<GlossaryEntry> entries = new ArrayList<>();
        GlossaryEntry entry1 =
            generateGlossaryEntry(srcLocale, "pos", "desc");
        entry1.getGlossaryTerms().add(generateGlossaryTerm("1.content-en-us", LocaleId.EN_US));
        entry1.getGlossaryTerms().add(generateGlossaryTerm("1.content-de", LocaleId.DE));
        entry1.getGlossaryTerms().add(generateGlossaryTerm("1.content-es", LocaleId.ES));
        entries.add(entry1);

        GlossaryEntry entry2 =
            generateGlossaryEntry(srcLocale, "pos", "desc");
        entry2.getGlossaryTerms().add(generateGlossaryTerm("2.content-en-us", LocaleId.EN_US));
        entry2.getGlossaryTerms().add(generateGlossaryTerm("2.content-de", LocaleId.DE));
        entry2.getGlossaryTerms().add(generateGlossaryTerm("2.content-es", LocaleId.ES));
        entries.add(entry2);

        GlossaryEntry entry3 =
            generateGlossaryEntry(srcLocale, "pos", "desc");
        entry3.getGlossaryTerms().add(generateGlossaryTerm("3.content-en-us", LocaleId.EN_US));
        entry3.getGlossaryTerms().add(generateGlossaryTerm("3.content-de", LocaleId.DE));
        entry3.getGlossaryTerms().add(generateGlossaryTerm("3.content-es", LocaleId.ES));
        entries.add(entry3);

        List<LocaleId> transLocales = new ArrayList<>();
        transLocales.add(LocaleId.DE);
        transLocales.add(LocaleId.ES);

        writer.write(fileWriter, entries, srcLocale, transLocales);

        GlossaryCSVReader reader = new GlossaryCSVReader(srcLocale, 300);
        File sourceFile = new File(filePath);

        Reader inputStreamReader =
            new InputStreamReader(new FileInputStream(sourceFile), "UTF-8");
        BufferedReader br = new BufferedReader(inputStreamReader);

        List<List<GlossaryEntry>> glossaries = reader.extractGlossary(br);
        assertThat(glossaries.size(), Matchers.equalTo(1));
        assertThat(glossaries.get(0), Matchers.equalTo(entries));
    }

    private GlossaryEntry generateGlossaryEntry(LocaleId srcLocale, String pos,
        String description) {
        GlossaryEntry entry = new GlossaryEntry();
        entry.setPos(pos);
        entry.setDescription(description);
        entry.setSrcLang(srcLocale);
        return entry;
    }

    private GlossaryTerm generateGlossaryTerm(String content, LocaleId localeId) {
        GlossaryTerm term = new GlossaryTerm();
        term.setContent(content);
        term.setLocale(localeId);
        return term;
    }

}

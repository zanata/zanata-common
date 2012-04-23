package org.zanata.adapter.xliff;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.zanata.common.LocaleId;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.TextFlow;

@Test(groups = { "unit-tests" })
public class XliffWriterTest
{
   private String testDir = "src/test/resources/";
   private String generateDir = "target/xliffGenerate/";
   private String generatedDocName = "Generated_StringResource";
   private String generatedDocFileName = generatedDocName + "_en_US.xml";

   XliffReader reader;

   @Test
   public void checkTransUnit() throws FileNotFoundException
   {
      prepareTemplateDoc();

      File generatedFile = new File(generateDir, "/" + generatedDocFileName);
      InputSource inputSource = new InputSource(new FileInputStream(generatedFile));
      Resource doc = reader.extractTemplate(inputSource, LocaleId.EN_US, generatedDocName);

      TextFlow firstTextFlow = doc.getTextFlows().get(0);
      TextFlow lastTextFlow = doc.getTextFlows().get(doc.getTextFlows().size() - 1);

      assertThat(firstTextFlow.getContents(), equalTo(asList("Translation Unit 1")));
      assertThat(lastTextFlow.getContents(), equalTo(asList("Translation Unit 3")));
   }

   @Test
   public void extractSizeTest() throws FileNotFoundException
   {
      prepareTemplateDoc();

      File generatedFile = new File(generateDir, "/" + generatedDocFileName);
      InputSource inputSource = new InputSource(new FileInputStream(generatedFile));
      Resource doc = reader.extractTemplate(inputSource, LocaleId.EN_US, generatedDocName);

      assertThat(doc.getTextFlows().size(), is(3));
   }

   private void prepareTemplateDoc() throws FileNotFoundException
   {
      String docName = "StringResource_en_US.xml";

      reader = new XliffReader();

      File file = new File(testDir, "/" + docName);
      InputSource inputSource = new InputSource(new FileInputStream(file));
      Resource doc = reader.extractTemplate(inputSource, LocaleId.EN_US, docName);
      doc.setName(generatedDocName);

      XliffWriter.write(new File(generateDir), doc, "en_US");
   }
}

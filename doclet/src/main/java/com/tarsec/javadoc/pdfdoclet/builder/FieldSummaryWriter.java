package com.tarsec.javadoc.pdfdoclet.builder;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.Destinations;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Utils;
import java.util.Comparator;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.*;

/**
 *
 * @author mdo
 */
public class FieldSummaryWriter extends AbstractSummaryWriter
{
  private static final Logger LOG = LoggerFactory.getLogger(FieldSummaryWriter.class);

  public FieldSummaryWriter(DocletEnvironment environment, Document pdfDocument)
  {
    super(environment, pdfDocument);
  }

  public void addFieldsSummary(TypeElement type, List<VariableElement> fields) throws DocumentException
  {
    LOG.debug("Print fields summary");
    fields.sort(Comparator.comparing(field -> field.getSimpleName().toString()));
    
    PdfPTable fieldsTable = createSummaryTable("Field");

    for (VariableElement field : fields) {
      // test if field is deprecated
      boolean isDeprecated = Utils.isDeprecated(environment.getDocTrees(), type) ||
                             Utils.isDeprecated(environment.getDocTrees(), field);
      Phrase deprecatedPhrase = null;

      if (isDeprecated) {
        //deprecatedPhrase = new CustomDeprecatedPhrase(field);
      }

      addFieldRow(type, fieldsTable, field, isDeprecated, null/*deprecatedPhrase*/);
    }

    pdfDocument.add(fieldsTable);
  }
  
  private void addFieldRow(TypeElement type, PdfPTable mainTable, VariableElement field, boolean isDeprecated, Phrase deprecatedPhrase) throws DocumentException
  {
    String name = field.getSimpleName().toString();
    String modifier = Utils.getMemberModifiers(field);
    String commentText = Utils.getFirstSentence(environment.getDocTrees(), field);
    String destination = Destinations.getFieldLinkQualifier(type, field);

    Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

    PdfPTable commentsTable = createColumnsAndDeprecated(objs, isDeprecated, deprecatedPhrase);
    Object constantValue = field.getConstantValue();

    if (constantValue != null) {
      // Add 2nd comment line (left cell empty, right cell text)
      commentsTable.addCell(new Phrase(""));
      Chunk valueTextChunk = new Chunk("Value: ", Fonts.getFont(TIMES_ROMAN, PLAIN, 10));
      Chunk valueContentChunk = new Chunk(constantValue.toString(), Fonts.getFont(COURIER, BOLD, 10));
      Phrase constantValuePhrase = new Phrase("");
      constantValuePhrase.add(valueTextChunk);
      constantValuePhrase.add(valueContentChunk);
      commentsTable.addCell(constantValuePhrase);
    }

    PdfPTable anotherinnertable = new PdfPTable(1);
    anotherinnertable.setWidthPercentage(100f);
    anotherinnertable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

    PdfPTable innerTable = addDeclaration(modifier, null);

    // Link to field
    LinkPhrase linkPhrase = new LinkPhrase(destination, name, Fonts.getFont(COURIER, 9));

    // right part of the table
    PdfPCell cell = PDFUtil.createElementCell(2, linkPhrase);
    cell.setPaddingTop((float) 2.0);
    cell.setPaddingLeft((float) 7.0);
    anotherinnertable.addCell(cell);
    anotherinnertable.addCell(commentsTable);

    innerTable.addCell(anotherinnertable);
    mainTable.addCell(innerTable);
  }  
  
}

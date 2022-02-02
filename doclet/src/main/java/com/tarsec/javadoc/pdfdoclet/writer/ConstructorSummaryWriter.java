/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tarsec.javadoc.pdfdoclet.writer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.COURIER;
import static com.tarsec.javadoc.pdfdoclet.writer.AbstractSummaryWriter.*;

/**
 *
 * @author mdo
 */
public class ConstructorSummaryWriter extends AbstractSummaryWriter
{
  private static final Logger LOG = LoggerFactory.getLogger(ConstructorSummaryWriter.class);

  public ConstructorSummaryWriter(DocletEnvironment environment, Document pdfDocument)
  {
    super(environment, pdfDocument);
  }
  

  public void addConstructorsSummary(TypeElement type, List<ExecutableElement> constructors) throws DocumentException
  {
    LOG.debug("Print constructors summary");
    constructors.sort(Comparator.comparing(constructor -> constructor.getSimpleName().toString()));
    
    PdfPTable fieldsTable = createSummaryTable("Constructor");
    
    for (ExecutableElement constructor : constructors) {
      // test if field is deprecated
      boolean isDeprecated = Utils.isDeprecated(environment.getDocTrees(), type) ||
                             Utils.isDeprecated(environment.getDocTrees(), constructor);
      Phrase deprecatedPhrase = null;

      if (isDeprecated) {
        //deprecatedPhrase = new CustomDeprecatedPhrase(field);
      }

      addConstructorRow(type, fieldsTable, constructor, isDeprecated, null/*deprecatedPhrase*/);
    }

    pdfDocument.add(fieldsTable);
  }
  
  
  private void addConstructorRow(TypeElement type, PdfPTable mainTable, ExecutableElement constructor, boolean isDeprecated, Phrase deprecatedPhrase) throws DocumentException
  {
    String name = type.getSimpleName().toString();
    String modifier = Utils.getMemberModifiers(constructor);
    String commentText = Utils.getFirstSentence(environment.getDocTrees(), constructor);
    String destination = Destinations.getMethodLinkQualifier(type, constructor);

    Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

    PdfPTable commentsTable = createColumnsAndDeprecated(objs, isDeprecated, deprecatedPhrase);

    // Link to constructor
    Font constructorFont = Fonts.getFont(COURIER, 9);
    Phrase signaturePhrase = new Phrase("", constructorFont);
    signaturePhrase.add(new LinkPhrase(destination, name, constructorFont));

    signaturePhrase.add("(");
    List<? extends VariableElement> parameters = constructor.getParameters();
    for (int i = 0; i < parameters.size(); i++) {
      signaturePhrase.add(getParameterTypePhrase(parameters.get(i), 9));
      signaturePhrase.add(" ");
      signaturePhrase.add(parameters.get(i).getSimpleName().toString());
      if (i != (parameters.size() - 1)) {
        signaturePhrase.add(", ");
      }
    }
    signaturePhrase.add(")");

    PdfPCell cell = PDFUtil.createElementCell(2, signaturePhrase);
    cell.setPaddingLeft((float) 7.0);
    
    PdfPTable anotherinnertable = new PdfPTable(1);
    anotherinnertable.setWidthPercentage(100f);
    anotherinnertable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
    anotherinnertable.addCell(cell);
    anotherinnertable.addCell(commentsTable);

    PdfPTable innerTable = addDeclaration(modifier, null);
    innerTable.addCell(anotherinnertable);

    mainTable.addCell(innerTable);
  }
}

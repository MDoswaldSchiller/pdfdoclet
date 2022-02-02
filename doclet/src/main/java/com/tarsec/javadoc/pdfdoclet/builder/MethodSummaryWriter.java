/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tarsec.javadoc.pdfdoclet.builder;

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
import static com.tarsec.javadoc.pdfdoclet.builder.AbstractSummaryWriter.*;

/**
 *
 * @author mdo
 */
public class MethodSummaryWriter extends AbstractSummaryWriter
{
  private static final Logger LOG = LoggerFactory.getLogger(MethodSummaryWriter.class);

  public MethodSummaryWriter(DocletEnvironment environment, Document pdfDocument)
  {
    super(environment, pdfDocument);
  }

  public void addMethodsSummary(TypeElement type, List<ExecutableElement> methods) throws DocumentException
  {
    LOG.debug("Print methods summary");
    methods.sort(Comparator.comparing(method -> method.getSimpleName().toString()));

    PdfPTable fieldsTable = createSummaryTable("Methods");

    for (ExecutableElement method : methods) {
      // test if field is deprecated
      boolean isDeprecated = Utils.isDeprecated(environment.getDocTrees(), type)
                             || Utils.isDeprecated(environment.getDocTrees(), method);
      Phrase deprecatedPhrase = null;

      if (isDeprecated) {
        //deprecatedPhrase = new CustomDeprecatedPhrase(field);
      }

      addMethodRow(type, fieldsTable, method, isDeprecated, null/*deprecatedPhrase*/);
    }

    pdfDocument.add(fieldsTable);
  }

  private void addMethodRow(TypeElement type, PdfPTable mainTable, ExecutableElement method, boolean isDeprecated, Phrase deprecatedPhrase) throws DocumentException
  {
    String name = method.getSimpleName().toString();
    String modifier = Utils.getMemberModifiers(method);
    String commentText = Utils.getFirstSentence(environment.getDocTrees(), method);
    String destination = Destinations.getMethodLinkQualifier(type, method);

    PdfPTable rowTable = addDeclaration(modifier, getReturnType(method, 9));

    Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

    PdfPTable commentsTable = createColumnsAndDeprecated(objs, isDeprecated, deprecatedPhrase);

    PdfPTable rightColumnInnerTable = new PdfPTable(1);
    rightColumnInnerTable.setWidthPercentage(100f);
    rightColumnInnerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

    // Link to method
    Font methodFont = Fonts.getFont(COURIER, 9);
    Phrase phrase = new Phrase("", methodFont);
    phrase.add(new LinkPhrase(destination, name, methodFont));

    phrase.add("(");
    List<? extends VariableElement> parameters = method.getParameters();
    for (int i = 0; i < parameters.size(); i++) {
      phrase.add(getParameterTypePhrase(parameters.get(i), 9));
      phrase.add(" ");
      phrase.add(parameters.get(i).getSimpleName().toString());
      if (i != (parameters.size() - 1)) {
        phrase.add(", ");
      }
    }
    phrase.add(")");

    PdfPCell cell = PDFUtil.createElementCell(2, phrase);
    cell.setPaddingLeft((float) 7.0);
    rightColumnInnerTable.addCell(cell);
    rightColumnInnerTable.addCell(commentsTable);

    // Now fill in right column as well
    rowTable.addCell(rightColumnInnerTable);

    // And add inner table to main summary table as a new row
    mainTable.addCell(rowTable);
  }

  public Phrase getReturnType(ExecutableElement method, int fontSize) throws DocumentException
  {
    Phrase returnPhrase = null;
    String returnType = method.getReturnType().toString();

    if (returnType != null && (returnType.trim().length() > 0)) {
      javax.lang.model.element.Element returnClass = environment.getTypeUtils().asElement(method.getReceiverType());

//      if (returnClass instanceof TypeElement) {
//        returnType = JavadocUtil.getQualifiedNameIfNecessary(returnClass).trim();
//        returnPhrase = new LinkPhrase(returnClass.qualifiedName(),
//                                      returnType + dimension, fontSize, false);
//      }
//      else {
      returnPhrase = new LinkPhrase(returnType,
                                    returnType/* + dimension*/, fontSize, false);
//      }
    }

    return returnPhrase;
  }

}

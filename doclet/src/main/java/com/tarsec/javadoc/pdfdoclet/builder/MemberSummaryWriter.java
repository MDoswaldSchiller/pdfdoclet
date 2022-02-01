/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tarsec.javadoc.pdfdoclet.builder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.PDFDocument;
import com.tarsec.javadoc.pdfdoclet.elements.CellNoBorderNoPadding;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPCell;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPTable;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Utils;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.*;

/**
 *
 * @author mdo
 */
public class MemberSummaryWriter
{
  private static final Logger LOG = LoggerFactory.getLogger(MemberSummaryWriter.class);

  private final DocletEnvironment environment;
  private final Document pdfDocument;

  public MemberSummaryWriter(DocletEnvironment environment, Document pdfDocument)
  {
    this.environment = Objects.requireNonNull(environment);
    this.pdfDocument = Objects.requireNonNull(pdfDocument);
  }

  public void appendMemberTable(TypeElement type) throws DocumentException
  {
    float[] widths = {(float) 6.0, (float) 94.0};
    PdfPTable table = new PdfPTable(widths);
    table.setWidthPercentage((float) 100);
    
    
    List<VariableElement> fields = ElementFilter.fieldsIn(type.getEnclosedElements());
    if (!fields.isEmpty()) {
      addFieldsSummary(type, fields);
    }
    
    List<ExecutableElement> constructors = ElementFilter.constructorsIn(type.getEnclosedElements());
    if (!constructors.isEmpty()) {
      addConstructorsSummary(type, constructors);
    }

    List<ExecutableElement> methods = ElementFilter.methodsIn(type.getEnclosedElements());
    if (!methods.isEmpty()) {
      addMethodsSummary(type, methods);
    }
    
  }
  
  
  private void addFieldsSummary(TypeElement type, List<VariableElement> fields) throws DocumentException
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
    String destination = String.format("%s.%s", type.getQualifiedName(), field.getSimpleName());

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
  
  private void addConstructorsSummary(TypeElement type, List<ExecutableElement> constructors) throws DocumentException
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
    String destination = String.format("%s.%s", type.getQualifiedName(), constructor.getSimpleName());

    Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

    PdfPTable commentsTable = createColumnsAndDeprecated(objs, isDeprecated, deprecatedPhrase);

    PdfPTable anotherinnertable = new PdfPTable(1);
    anotherinnertable.setWidthPercentage(100f);
    anotherinnertable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

    // Link to constructor
    Font constructorFont = Fonts.getFont(COURIER, 9);
    Phrase phrase = new Phrase("", constructorFont);
    phrase.add(new LinkPhrase(destination, name, constructorFont));

    phrase.add("(");
    List<? extends VariableElement> parameters = constructor.getParameters();
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
    anotherinnertable.addCell(cell);
    anotherinnertable.addCell(commentsTable);

    PdfPTable innerTable = addDeclaration(modifier, null);
    innerTable.addCell(anotherinnertable);

    mainTable.addCell(innerTable);
  }
  
  
  private void addMethodsSummary(TypeElement type, List<ExecutableElement> methods) throws DocumentException
  {
    LOG.debug("Print methods summary");
    methods.sort(Comparator.comparing(method -> method.getSimpleName().toString()));
    
    PdfPTable fieldsTable = createSummaryTable("Methods");
    
    for (ExecutableElement method : methods) {
      // test if field is deprecated
      boolean isDeprecated = Utils.isDeprecated(environment.getDocTrees(), type) ||
                             Utils.isDeprecated(environment.getDocTrees(), method);
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
    String destination = String.format("%s.%s.%s", type.getQualifiedName(), method.getSimpleName(), method.getParameters().stream().map(v -> v.asType().toString()).collect(Collectors.joining(".")));

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
  
  
  
  private LinkPhrase getParameterTypePhrase(VariableElement param, int fontSize)
  {
    System.out.println("Type: " + param.asType());
    javax.lang.model.element.Element paramElement = environment.getTypeUtils().asElement(param.asType());
    
    String fullType = paramElement != null ? paramElement.toString() : param.asType().toString();
    String shortType = param.asType().toString();
    return new LinkPhrase(fullType, shortType, fontSize, false);
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
  
  
    /**
   * Creates a summary table with a coloured title bar.
   *
   * @param title The title for the summary table.
   * @return The newly created summary table.
   * @throws DocumentException If something fails.
   */
  private PdfPTable createSummaryTable(String title) throws DocumentException
  {
    PdfPTable mainTable = new CustomPdfPTable();
    PdfPCell colorTitleCell = new CustomPdfPCell(title + " Summary");
    
    // Some empty space...
    PDFDocument.instance().add(new Paragraph((float) 8.0, " "));
    mainTable.addCell(colorTitleCell);

    return mainTable;
  }
  
    /**
   * Creates the inner table for both columns. The left column already contains
   * the declaration text part.
   *
   * @param text The text (like "static final"..)
   * @param innerTable The table with both columns
   */
  private static PdfPTable addDeclaration(String text, Phrase returnType)
      throws DocumentException
  {
    PdfPTable innerTable = new PdfPTable(2);
    innerTable.setWidthPercentage(100f);
    innerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
    innerTable.setWidths(new int[]{24, 76});
    Paragraph declarationParagraph = new Paragraph((float) 9.0);
    Chunk leftPart = new Chunk(text, Fonts.getFont(IConstants.COURIER, 9));
    declarationParagraph.add(leftPart);
    if (returnType != null) {
      declarationParagraph.add(returnType);
      declarationParagraph.add(new Chunk(" ", Fonts.getFont(IConstants.COURIER, 9)));
    }
    PdfPCell cell = new CustomPdfPCell(Rectangle.RIGHT, declarationParagraph, 1, BaseColor.GRAY);
    cell.setPaddingTop((float) 4.0);
    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);

    innerTable.addCell(cell);
    return innerTable;
  }

  /**
   * Creates the two columns for the summary table and, if necessary, fills in
   * the "Deprecated" text. Otherwise, the given elements are filled in.
   *
   * @param descPhr The phrases which will be filled with the description or the
   * deprecated text.
   * @param objs The description elements.
   * @param isDeprecated If true, the whole class/method is deprecated.
   * @param deprecatedPhrase The phrase for the deprecated text.
   * @return The summary table columns.
   * @throws DocumentException If something failed.
   */
  private static PdfPTable createColumnsAndDeprecated(Element[] objs,
                                                      boolean isDeprecated, Phrase deprecatedPhrase)
      throws DocumentException
  {

    PdfPTable commentsTable = null;
    commentsTable = new PdfPTable(2);
    commentsTable.setWidths(new int[]{5, 95});
    commentsTable.setWidthPercentage(100f);
    commentsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
    commentsTable.addCell(new Phrase(""));

    Phrase descPhr = new Phrase();

    CellNoBorderNoPadding cell = new CellNoBorderNoPadding(descPhr);

    commentsTable.addCell(cell);

    if (isDeprecated) {
      // if the method is deprecated...
      // do not print the comment text...
      // just print the deprecated text
      descPhr.add(new Phrase(IConstants.LB_DEPRECATED_TAG, Fonts.getFont(
                             IConstants.TIMES_ROMAN, BOLD, 10)));
      descPhr.add(deprecatedPhrase);
    }
    else if (objs.length != 0) {
      for (int i = 0; i < objs.length; i++) {
        if (objs[i] instanceof com.itextpdf.text.List) {
          cell.addElement(objs[i]);
          descPhr = new Phrase("");
          cell.addElement(descPhr);
        }
        else {
          descPhr.add(objs[i]);
        }
      }
    }

    return commentsTable;
  }
}

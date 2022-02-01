/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tarsec.javadoc.pdfdoclet.builder;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.util.Objects;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mdo
 */
public class MemberWriter
{

  private static final Logger LOG = LoggerFactory.getLogger(MemberWriter.class);

  private final DocletEnvironment environment;
  private final Document pdfDocument;

  public MemberWriter(DocletEnvironment environment, Document pdfDocument)
  {
    this.environment = Objects.requireNonNull(environment);
    this.pdfDocument = Objects.requireNonNull(pdfDocument);
  }

  public void appendMemberTable(TypeElement type) throws DocumentException
  {
    float[] widths = {(float) 6.0, (float) 94.0};
    PdfPTable table = new PdfPTable(widths);
    table.setWidthPercentage((float) 100);
    
    
  }
  
  private void addHeader(PdfPTable table) throws DocumentException
  {
    PdfPCell colorTitleCell = null;

    // Some empty space...
    pdfDocument.add(new Paragraph((float) 6.0, " "));

//    if (isConstructor) {
//      colorTitleCell = new CustomPdfPCell("Constructors");
//    }
//    else if (isField) {
//      colorTitleCell = new CustomPdfPCell("Fields");
//    }
//    else {
//      colorTitleCell = new CustomPdfPCell("Methods");
//    }

    colorTitleCell.setColspan(2);
    table.addCell(colorTitleCell);
  }
  
  
  
  public void append(ExecutableElement constructorElement) throws DocumentException
  {
    boolean isFirst = false;
    boolean isDeprecated = false;

    appendMethodDeclaration(constructorElement);
    
//    String declaration = Utils.getModifiers(constructorElement);
//    printMember(
//        declaration, null,
//        constructors[i],
//        constructors[i].parameters(), null,
//        isFirst, false, true, isDeprecated, deprecatedPhrase, null);

    //TagLists.printMemberTags(constructors[i]);
  }

//  public static void printMember(
//      String declaration, Phrase returnType, ProgramElementDoc commentDoc,
//      Parameter[] parms, ClassDoc[] thrownExceptions,
//      boolean isFirst, boolean isField, boolean isConstructor,
//      boolean isDeprecated, Phrase deprecatedPhrase, Object constantValue)
//      throws Exception
//  {
//
//    LOG.debug(">");
//
//    String name = commentDoc.name();
//
//    State.setCurrentMember(State.getCurrentClass() + "." + name);
////    State.setCurrentDoc(commentDoc);
//
//    LOG.info("....> " + State.getCurrentMember());
//
//    // Create bookmark anchors
//    // Returns the text, resolving any "inheritDoc" inline tags
//    String commentText = JavadocUtil.getComment(commentDoc);
//
//    // TODO: The following line may set the wrong page number
//    //      in the index, when the member gets printed on a
//    //      new page completely (because it is in one table).
//    // Solution unknown yet. Probably split up table.
//    PDFDoclet.getIndex().addToMemberList(State.getCurrentMember());
//
//    // Prepare list of exceptions (if it throws any)
//    String throwsText = "throws";
//    int parmsColumn = declaration.length()
//                      + (name.length() - throwsText.length());
//
//    // First output text line (declaration of method and first parameter or "()" ).
//    // This first line is a special case because the class name is bold,
//    // while the rest is regular plain text, so it must be built using three Chunks.
//    Paragraph declarationParagraph = new Paragraph((float) 10.0);
//
//    // left part / declaration ("public static..")
//    Chunk leftPart = new Chunk(declaration, Fonts.getFont(COURIER, 10));
//
//    declarationParagraph.add(leftPart);
//
//    if (returnType != null) {
//      // left middle part / declaration ("public static..")
//      declarationParagraph.add(returnType);
//      declarationParagraph.add(new Chunk(" ", Fonts.getFont(COURIER, 10)));
//      parmsColumn = 2;
//    }
//
//    // right middle part / bold class name
//    declarationParagraph.add(new Chunk(name, Fonts.getFont(COURIER, BOLD, 10)));
//
//    if (!isField) {
//      // 1st parameter or empty brackets
//
//      if ((parms != null) && (parms.length > 0)) {
//        Phrase wholePhrase = new Phrase("(", Fonts.getFont(COURIER, 10));
//        // create link for parameter type
//        wholePhrase.add(PDFUtil.getParameterTypePhrase(parms[0], 10));
//        // then normal text for parameter name
//        wholePhrase.add(" " + parms[0].name());
//        if (parms.length > 1) {
//          wholePhrase.add(",");
//        }
//        else {
//          wholePhrase.add(")");
//        }
//
//        // In order to have the parameter types in the bookmark,
//        // make the current state text more detailled
//        String txt = State.getCurrentMethod() + "(";
//        for (int i = 0; i < parms.length; i++) {
//          if (i > 0) {
//            txt = txt + ",";
//          }
//          txt = txt + JavadocUtil.getParameterType(parms[i]);
//        }
//        txt = txt + ")";
//        State.setCurrentMethod(txt);
//
//        // right part / parameter and brackets
//        declarationParagraph.add(wholePhrase);
//
//      }
//      else {
//
//        String lastPart = "()";
//        State.setCurrentMethod(State.getCurrentMethod() + lastPart);
//        // right part / parameter and brackets
//        declarationParagraph.add(new Chunk(lastPart, Fonts.getFont(COURIER, 10)));
//      }
//
//    }
//
//    int rows = 2;
//
//    if (isFirst) {
//      rows++;
//    }
//
  
  
  

  
  
  
  
  
  
  
//   
//
//    // Method name (large, first line of a method description block)
//    Phrase linkPhrase = Destinations.createDestination(commentDoc.name(),
//                                                       commentDoc, Fonts.getFont(TIMES_ROMAN, BOLD, 14));
//    Paragraph nameTitle = new Paragraph(linkPhrase);
//    PdfPCell nameCell = new CellNoBorderNoPadding(nameTitle);
//
//    if (isFirst) {
//      nameCell.setPaddingTop(10);
//    }
//    else {
//      nameCell.setPaddingTop(0);
//    }
//
//    nameCell.setPaddingBottom(8);
//    nameCell.setColspan(1);
//
//    // Create nested table in order to try to prevent the stuff inside
//    // this table from being ripped appart over a page break. The method
//    // name and the declaration/parm/exception line(s) should always be
//    // together, because everything else just looks bad
//    PdfPTable linesTable = new PdfPTable(1);
//    linesTable.addCell(nameCell);
//    linesTable.addCell(new CellNoBorderNoPadding(declarationParagraph));
//
//    if (!isField) {
//      // Set up following declaration lines
//      Paragraph[] params = PDFUtil.createParameters(parmsColumn, parms);
//      Paragraph[] exceps = PDFUtil.createExceptions(parmsColumn,
//                                                    thrownExceptions);
//
//      for (int i = 0; i < params.length; i++) {
//        linesTable.addCell(new CellNoBorderNoPadding(params[i]));
//      }
//
//      for (int i = 0; i < exceps.length; i++) {
//        linesTable.addCell(new CellNoBorderNoPadding(exceps[i]));
//      }
//    }
//
//    // Create cell for inserting the nested table into the outer table
//    PdfPCell cell = new PdfPCell(linesTable);
//    cell.setPadding(5);
//    cell.setBorder(Rectangle.NO_BORDER);
//    cell.setColspan(2);
//    table.addCell(cell);
//
//    // The empty, left cell (the invisible indentation column)
//    State.setContinued(true);
//
//    PdfPCell leftCell = PDFUtil.createElementCell(5, new Phrase("", Fonts.getFont(TIMES_ROMAN, BOLD, 6)));
//    PdfPCell spacingCell = new PdfPCell();
//    spacingCell.setFixedHeight((float) 8.0);
//    spacingCell.setBorder(Rectangle.NO_BORDER);
//    table.addCell(spacingCell);
//    table.addCell(spacingCell);
//
//    // The descriptive method explanation text
//    if (isDeprecated) {
//      Phrase commentPhrase = new Phrase();
//      commentPhrase.add(new Phrase(IConstants.LB_DEPRECATED_TAG,
//                                   Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
//      commentPhrase.add(deprecatedPhrase);
//      table.addCell(leftCell);
//      table.addCell(PDFUtil.createElementCell(0, commentPhrase));
//
//      commentPhrase = new Phrase();
//      commentPhrase.add(Chunk.NEWLINE);
//      table.addCell(leftCell);
//      table.addCell(PDFUtil.createElementCell(0, commentPhrase));
//    }
//
//    Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);
//
//    if (objs.length == 1) {
//      table.addCell(leftCell);
//      table.addCell(PDFUtil.createElementCell(0, objs[0]));
//    }
//    else {
//      table.addCell(leftCell);
//      table.addCell(PDFUtil.createElementCell(0, Element.ALIGN_LEFT, objs));
//    }
//
//    // TODO: FORMAT THIS CONSTANT VALUE OUTPUT CORRECTLY
//    if (isField) {
//      if (constantValue != null) {
//        // Add 2nd comment line (left cell empty, right cell text)
//        Chunk valueTextChunk = new Chunk("Constant value: ", Fonts.getFont(
//                                         TIMES_ROMAN, PLAIN, 10));
//        Chunk valueContentChunk = new Chunk(constantValue.toString(), Fonts
//                                            .getFont(COURIER, BOLD, 10));
//        Phrase constantValuePhrase = new Phrase("");
//        constantValuePhrase.add(valueTextChunk);
//        constantValuePhrase.add(valueContentChunk);
//        table.addCell(leftCell);
//        table.addCell(PDFUtil.createElementCell(0, constantValuePhrase));
//      }
//    }
//
//    // Add whole method block to document
//    PDFDocument.add(table);
//
//    LOG.debug("<");
//  }
  
  private void appendMethodDeclaration(ExecutableElement method) throws DocumentException
  {
    
  }

}

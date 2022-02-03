package com.tarsec.javadoc.pdfdoclet.writer;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
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
import java.util.Objects;
import javax.lang.model.element.VariableElement;
import jdk.javadoc.doclet.DocletEnvironment;
import static com.tarsec.javadoc.pdfdoclet.IConstants.BOLD;

/**
 *
 * @author mdo
 */
public class AbstractSummaryWriter
{
  protected final DocletEnvironment environment;
  protected Document pdfDocument;

  public AbstractSummaryWriter(DocletEnvironment environment, Document pdfDocument)
  {
    this.environment = Objects.requireNonNull(environment);
    this.pdfDocument = Objects.requireNonNull(pdfDocument);
  }
  
  /**
   * Creates a summary table with a coloured title bar.
   *
   * @param title The title for the summary table.
   * @return The newly created summary table.
   * @throws DocumentException If something fails.
   */
  protected PdfPTable createSummaryTable(String title) throws DocumentException
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
  protected static PdfPTable addDeclaration(String text, Phrase returnType) throws DocumentException
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
  protected static PdfPTable createColumnsAndDeprecated(Element[] objs, boolean isDeprecated, Phrase deprecatedPhrase) throws DocumentException
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
  
  protected LinkPhrase getParameterTypePhrase(VariableElement param, int fontSize)
  {
    javax.lang.model.element.Element paramElement = environment.getTypeUtils().asElement(param.asType());
    
    String fullType = paramElement != null ? paramElement.toString() : param.asType().toString();
    String shortType = param.asType().toString();
    return new LinkPhrase(fullType, shortType, fontSize, false);
  }
}

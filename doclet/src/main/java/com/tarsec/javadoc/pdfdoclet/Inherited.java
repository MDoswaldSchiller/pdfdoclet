/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.tarsec.javadoc.pdfdoclet.elements.CellBorderPadding;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPCell;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPTable;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.*;

/**
 * Prints the inherited tables.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Inherited
{
  private static final Logger LOG = LoggerFactory.getLogger(Inherited.class);

  private final Document pdfDocument;

  public Inherited(Document pdfDocument)
  {
    this.pdfDocument = pdfDocument;
  }
  
  /**
   * Prints inherited methods and fields from superclasses
   *
   * @param supercls class source to get inherited fields and methods for.
   * @param show SHOW_METHODS or SHOW_FIELDS
   * @throws Exception
   */
  public void print(ClassDoc supercls, int show) throws Exception
  {
    String type;

    FieldDoc[] fields = supercls.fields();

    Arrays.sort(fields);
    LOG.debug("Fields: " + fields.length);

    if (supercls.isInterface()) {
      type = "interface";
    }
    else {
      type = "class";
    }

    // Create cell for additional spacing below
    PdfPCell spacingCell = new PdfPCell();
    spacingCell.addElement(new Chunk(" "));
    spacingCell.setFixedHeight((float) 4.0);
    spacingCell.setBorder(Rectangle.BOTTOM + Rectangle.LEFT + Rectangle.RIGHT);
    spacingCell.setBorderColor(BaseColor.GRAY);

    if ((fields.length > 0) && (show == SHOW_FIELDS)) {
      pdfDocument.add(new Paragraph((float) 6.0, " "));

      PdfPTable table = new PdfPTable(1);
      table.setWidthPercentage((float) 100);

      Paragraph newLine = new Paragraph();
      newLine.add(new Chunk("Fields inherited from " + type + " ",
                            Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
      newLine.add(new LinkPhrase(supercls.qualifiedTypeName(),
                                 null, 10, false));

      table.addCell(new CustomPdfPCell(newLine, COLOR_INHERITED_SUMMARY));

      Paragraph paraList = new Paragraph();

      for (int i = 0; i < fields.length; i++) {
        paraList.add(new LinkPhrase(
            fields[i].qualifiedName(),
            fields[i].name(), 10,
            false));

        if (i != (fields.length - 1)) {
          paraList.add(new Chunk(", ", Fonts.getFont(TIMES_ROMAN, 9)));
        }
      }

      PdfPCell contentCell = new CellBorderPadding(paraList);
      float leading = (float) contentCell.getLeading() + (float) 1.1;
      contentCell.setLeading(leading, leading);
      table.addCell(contentCell);
      table.addCell(spacingCell);

      pdfDocument.add(table);
    }

    MethodDoc[] meth = supercls.methods();

    Arrays.sort(meth);
    LOG.debug("Methods: " + meth.length);

    if ((meth.length > 0) && (show == SHOW_METHODS)) {
      pdfDocument.add(new Paragraph((float) 6.0, " "));

      PdfPTable table = new CustomPdfPTable();

      Paragraph newLine = new Paragraph();
      newLine.add(new Chunk("Methods inherited from " + type + " ",
                            Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
      newLine.add(new LinkPhrase(supercls.qualifiedTypeName(),
                                 null, 10, false));

      table.addCell(new CustomPdfPCell(newLine, COLOR_INHERITED_SUMMARY));
      Paragraph paraList = new Paragraph();

      for (int i = 0; i < meth.length; i++) {
        String methodLabel = meth[i].name();

        // Do not list static initializers like "<clinit>"
        if (!methodLabel.startsWith("<")) {
          paraList.add(new LinkPhrase(
              supercls.qualifiedTypeName() + "." + meth[i].name(),
              meth[i].name(), 10,
              false));

          if (i != (meth.length - 1)) {
            paraList.add(new Chunk(", ", Fonts.getFont(COURIER, 10)));
          }
        }
      }

      PdfPCell contentCell = new CellBorderPadding(paraList);
      float leading = (float) contentCell.getLeading() + (float) 1.1;
      contentCell.setLeading(leading, leading);
      table.addCell(contentCell);
      table.addCell(spacingCell);

      pdfDocument.add(table);
    }

    // Print inherited interfaces / class methods and fields recursively
    ClassDoc supersupercls = null;

    if (supercls.isClass()) {
      supersupercls = supercls.superclass();
    }

    if (supersupercls != null) {
      String className = supersupercls.qualifiedName();
      if (ifClassMustBePrinted(className)) {
        print(supersupercls, show);
      }
    }

    ClassDoc[] interfaces = supercls.interfaces();
    LOG.debug("Interfaces: " + interfaces.length);
    for (int i = 0; i < interfaces.length; i++) {
      supersupercls = interfaces[i];
      String className = supersupercls.qualifiedName();
      if (ifClassMustBePrinted(className)) {
        print(supersupercls, show);
      }
    }
  }

  /**
   *
   * @param className
   * @return
   */
  public static boolean ifClassMustBePrinted(String className)
  {
    boolean printClass = true;
    if (!Destinations.isValid(className)
        && !Configuration.isShowExternalInheritedSummaryActive()) {
      LOG.debug("Do not print inherited table for external element " + className);
      printClass = false;
    }
    return printClass;
  }
}

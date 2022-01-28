/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.elements.CellNoBorderNoPadding;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;

/**
 * Prints the implementor info.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Implementors implements IConstants
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(Implementors.class);

  /**
   * Prints all known subclasses or implementing classes.
   *
   * @param title The label for the name list
   * @param names The names (classes or interfaces)
   * @throws Exception
   */
  public static void print(String title, String[] names) throws Exception
  {
    float[] widths = {(float) 6.0, (float) 94.0};
    PdfPTable table = new PdfPTable(widths);
    table.setWidthPercentage((float) 100);

    PdfPCell spacingCell = new CellNoBorderNoPadding(new Phrase(""));
    spacingCell.setFixedHeight((float) 8.0);
    spacingCell.setColspan(2);
    table.addCell(spacingCell);
    PdfPCell titleCell = new CellNoBorderNoPadding(new Paragraph((float) 20.0,
                                                                 title, Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
    titleCell.setColspan(2);
    table.addCell(titleCell);

    PdfPCell leftCell = PDFUtil.createElementCell(5, new Phrase(""));
    Paragraph descPg = new Paragraph((float) 24.0);

    for (int i = names.length - 1; i > -1; i--) {
      String subclassName = JavadocUtil.getQualifiedNameIfNecessary(names[i]);
      Phrase subclassPhrase = new LinkPhrase(names[i], subclassName, 10, true);
      descPg.add(subclassPhrase);

      if (i > 0) {
        descPg.add(new Chunk(", ", Fonts.getFont(COURIER, BOLD, 10)));
      }
    }

    table.addCell(leftCell);
    table.addCell(new CellNoBorderNoPadding(descPg));

    table.addCell(spacingCell);
    PDFDocument.instance().add(table);
  }

}

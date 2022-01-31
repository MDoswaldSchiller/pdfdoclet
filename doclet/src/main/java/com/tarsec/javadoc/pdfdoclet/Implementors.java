/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;


import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.elements.CellNoBorderNoPadding;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Utils;
import java.util.Collection;

/**
 * Prints the implementor info.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Implementors implements IConstants
{
  /**
   * Prints all known subclasses or implementing classes.
   *
   * @param title The label for the name list
   * @param names The names (classes or interfaces)
   * @throws Exception
   */
  public static PdfPTable create(String title, Collection<String> names) throws DocumentException
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
    int count = 0;

    for (String name : names) {
      if (count > 0) {
        descPg.add(new Chunk(", ", Fonts.getFont(COURIER, BOLD, 10)));
      }
      count++;
    
      String subclassName = Utils.getQualifiedNameIfNecessary(name);
      Phrase subclassPhrase = new LinkPhrase(name, subclassName, 10, true);
      descPg.add(subclassPhrase);
    }

    table.addCell(leftCell);
    table.addCell(new CellNoBorderNoPadding(descPg));

    table.addCell(spacingCell);
    return table;
  }

}

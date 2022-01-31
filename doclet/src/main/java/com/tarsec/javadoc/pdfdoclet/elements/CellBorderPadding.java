/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * Customized version(s) of PdfPCell with a border of with 1 (gray) and a
 * padding of 6.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CellBorderPadding extends PdfPCell
{
  /**
   * Creates a PdfPCell with a border and a padding of 6.
   *
   * @param data The cell content.
   */
  public CellBorderPadding(Phrase data)
  {
    super(data);
    super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.BOTTOM
                    + Rectangle.RIGHT);
    super.setPadding(6);
    super.setBorderWidth(1);
    super.setBorderColor(BaseColor.GRAY);
  }

  /**
   * Creates a PdfPCell with no bottom border.
   *
   * @param data The cell content.
   */
  public CellBorderPadding(Paragraph data)
  {
    super(data);
    super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.RIGHT);
    super.setPadding(6);
    super.setBorderWidth(1);
    super.setBorderColor(BaseColor.GRAY);
  }

}

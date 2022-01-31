/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;


import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * Customized version(s) of PdfPCell without a border and without padding.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CellNoBorderNoPadding extends PdfPCell
{
  /**
   * Creates a PdfPCell with a border and a padding of 6.
   *
   * @param data The cell content.
   */
  public CellNoBorderNoPadding(Phrase data)
  {
    super(data);
    super.setBorder(Rectangle.NO_BORDER);
    super.setPadding(0);
  }

  /**
   * Creates a PdfPCell with a border and a padding of 6.
   *
   * @param data The cell content.
   */
  public CellNoBorderNoPadding(Paragraph data)
  {
    super(data);
    super.setBorder(Rectangle.NO_BORDER);
    super.setPadding(0);
  }

}

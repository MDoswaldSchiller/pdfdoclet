/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import org.apache.log4j.Logger;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Customized version(s) of PdfPCell without a border and without padding.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CellNoBorderNoPadding extends PdfPCell
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(CellNoBorderNoPadding.class);

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

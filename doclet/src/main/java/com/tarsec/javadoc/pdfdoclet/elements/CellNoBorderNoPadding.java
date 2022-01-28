/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;


import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customized version(s) of PdfPCell without a border and without padding.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CellNoBorderNoPadding extends PdfPCell
{
  private static final Logger LOG = LoggerFactory.getLogger(CellNoBorderNoPadding.class);

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

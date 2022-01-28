/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;

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
   * Logger reference
   */
  private static Logger log = Logger.getLogger(CellBorderPadding.class);

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
    super.setBorderColor(Color.gray);
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
    super.setBorderColor(Color.gray);
  }

}

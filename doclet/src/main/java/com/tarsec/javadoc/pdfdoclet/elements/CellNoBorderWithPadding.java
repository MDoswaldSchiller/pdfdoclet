/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import org.apache.log4j.Logger;

import com.lowagie.text.List;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * Customized version(s) of PdfPCell without a border but with a given padding.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CellNoBorderWithPadding extends PdfPCell
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(CellNoBorderWithPadding.class);

  /**
   * Creates a PdfPCell with a given padding and an additional wrapping Phrase
   * for the given Phrase.
   *
   * @param padding The padding for the PdfPCell
   * @param data The content for the cell
   */
  public CellNoBorderWithPadding(int padding, Phrase data)
  {
    super(data);
    super.setBorder(Rectangle.NO_BORDER);
    super.setPadding(padding);
  }

  public CellNoBorderWithPadding(int padding, PdfPTable table)
  {
    super(table);
    super.setBorder(Rectangle.NO_BORDER);
    super.setPadding(padding);
  }

  public CellNoBorderWithPadding(int padding, List list)
  {
    super();
    super.addElement(list);
    super.setBorder(Rectangle.NO_BORDER);
    super.setPadding(padding);
  }
}

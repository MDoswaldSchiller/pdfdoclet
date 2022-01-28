/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;


import com.lowagie.text.pdf.PdfPTable;

/**
 * Customized version(s) of PdfPTable.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CustomPdfPTable extends PdfPTable
{
  /**
   * Creates a table for summaries (2 columns, border).
   */
  public CustomPdfPTable()
  {
    super(1);
    super.setWidthPercentage((float) 100);
  }
}

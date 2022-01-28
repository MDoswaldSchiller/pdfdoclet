/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import org.apache.log4j.Logger;

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
   * Logger reference
   */
  private static Logger log = Logger.getLogger(CustomPdfPTable.class);

  /**
   * Creates a table for summaries (2 columns, border).
   */
  public CustomPdfPTable()
  {
    super(1);
    super.setWidthPercentage((float) 100);
  }
}

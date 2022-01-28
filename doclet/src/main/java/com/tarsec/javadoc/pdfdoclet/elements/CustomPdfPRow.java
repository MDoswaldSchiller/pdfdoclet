/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/elements/CustomPdfPRow.java,v 1.1 2007/07/18 22:15:19 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;


import com.lowagie.text.Cell;
import com.lowagie.text.Phrase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps the PdfPRow object to provide an additional method for querying the
 * number of columns of this row.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class CustomPdfPRow extends Phrase
{
  private static final Logger LOG = LoggerFactory.getLogger(CustomPdfPRow.class);

  /**
   * Stores the number of columns in this row.
   */
  private int columns = 0;

  private Cell[] cells = new Cell[0];

  /**
   * Creates a row with a certain number of cells.
   *
   * @param cells The cells contained in this row.
   */
  public CustomPdfPRow(Cell[] cells)
  {
    super();
    LOG.debug("Create row with " + cells.length + " columns.");
    this.cells = cells;
    columns = cells.length;
  }

  /**
   * Returns the cells in this row.
   *
   * @return The cells in this row.
   */
  public Cell[] getCells()
  {
    return cells;
  }

  /**
   * Returns the number of columns of this row.
   *
   * @return The number of columns (cells).
   */
  public int getColumns()
  {
    return columns;
  }
}

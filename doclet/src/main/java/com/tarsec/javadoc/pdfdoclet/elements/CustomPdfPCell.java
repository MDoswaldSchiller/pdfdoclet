/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import java.awt.Color;

/**
 * Customized version(s) of PdfPCell.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CustomPdfPCell extends PdfPCell implements IConstants
{
  /**
   * A coloured title bar (for the "Fields", "Methods" and "Constructors"
   * titles).
   *
   * @param title The text for the title.
   */
  public CustomPdfPCell(String title)
  {
    super(new Phrase(title, Fonts.getFont(TIMES_ROMAN, 18)));
    super.setPaddingTop((float) 0.0);
    super.setPaddingBottom((float) 5.0);
    super.setPaddingLeft((float) 3.0);
    super.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
    super.setBackgroundColor(COLOR_SUMMARY_HEADER);
    super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.BOTTOM
                    + Rectangle.RIGHT);
    super.setBorderWidth(1);
    super.setBorderColor(Color.gray);
  }

  /**
   * A coloured title bar (for summary tables etc.)
   *
   * @param paragraph The text for the title.
   * @param backgroundColor Color of the cell
   */
  public CustomPdfPCell(Paragraph paragraph, Color backgroundColor)
  {
    super(paragraph);
    super.setPaddingTop((float) 0.0);
    super.setPaddingBottom((float) 5.0);
    super.setPaddingLeft((float) 3.0);
    super.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
    super.setBackgroundColor(backgroundColor);
    super.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.BOTTOM
                    + Rectangle.RIGHT);
    super.setBorderWidth(1);
    super.setBorderColor(Color.gray);
  }

  /**
   * Creates a PdfPCell with certain attributes
   *
   * @param border The border type for the cell
   * @param phrase The content for the cell.
   * @param borderWidth The border width for the cell
   * @param borderColor The color of the border
   */
  public CustomPdfPCell(int border, Phrase phrase,
                        int borderWidth, Color borderColor)
  {

    super(phrase);
    super.setBorderColor(borderColor);
    super.setBorderWidth(borderWidth);
    super.setPaddingBottom((float) 6.0);
    super.setPaddingLeft((float) 6.0);
    super.setPaddingRight((float) 6.0);

    //cell.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT);
    super.setBorder(border);

    super.setVerticalAlignment(PdfPCell.ALIGN_TOP);
    super.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
  }

}

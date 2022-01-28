/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPTable;

/**
 * Implements the PRE tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagPRE extends HTMLTag
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagPRE.class);

  private PdfPTable mainTable = new CustomPdfPTable();
  private Paragraph cellPara = null;
  private PdfPCell colorTitleCell = null;

  /**
   * Creates a PRE tag object.
   *
   * @param parent The parent HTML object.
   * @param type The type for this tag.
   */
  public TagPRE(HTMLTag parent, int type)
  {
    super(parent, type);
    isPre = true;

    cellPara = new Paragraph("", getFont());

    colorTitleCell = new PdfPCell(cellPara);
    colorTitleCell.setBorder(Rectangle.TOP + Rectangle.LEFT + Rectangle.RIGHT + Rectangle.BOTTOM);
    colorTitleCell.setPadding(6);
    colorTitleCell.setPaddingBottom(12);
    colorTitleCell.setPaddingLeft(10);
    colorTitleCell.setPaddingRight(10);
    colorTitleCell.setBorderWidth(1);
    colorTitleCell.setBorderColor(Color.gray);
    colorTitleCell.setBackgroundColor(COLOR_LIGHTER_GRAY);
    colorTitleCell.addElement(cellPara);
    mainTable.addCell(colorTitleCell);
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#toElement(java.lang.String)
   */
  public Element toElement(String text)
  {
    cellPara.add(text);
    return null;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#addNestedTagContent(com.lowagie.text.Element[])
   */
  public void addNestedTagContent(Element[] content)
  {
    for (int i = 0; i < content.length; i++) {
      cellPara.add(content[i]);
    }
  }


  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#closeTagElements()
   */
  public Element[] closeTagElements()
  {
    Element[] elements = new Element[2];
    Paragraph empty = createParagraph("");
    empty.setLeading(getFont().size() + (float) 1.0);
    elements[0] = createParagraph(" ");
    elements[1] = createParagraph("");
    return elements;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
   */
  public Element[] openTagElements()
  {
    Element[] elements = new Element[3];
    Paragraph empty = createParagraph("");
    empty.setLeading(getFont().size() + (float) 1.0);
    elements[0] = createParagraph(" ");
    elements[1] = createParagraph("");
    elements[2] = mainTable;
    return elements;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#isPre()
   */
  public boolean isPre()
  {
    return true;
  }
}

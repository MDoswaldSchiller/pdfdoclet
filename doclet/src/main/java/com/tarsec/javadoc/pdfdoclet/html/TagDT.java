/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;

/**
 * Implements the DT tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagDT extends HTMLTag
{
  /**
   * Creates a DT tag object.
   *
   * @param parent The parent HTML tag object.
   * @param type The type of this HTML tag.
   */
  public TagDT(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
   */
  @Override
  public Element[] openTagElements()
  {
    Element[] elements = new Element[1];
    Paragraph p1 = createParagraph("");
    elements[0] = p1;
    return elements;
  }
}

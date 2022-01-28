/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;

/**
 * Implements a definition list (DL) tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagDL extends HTMLTag
{
  /**
   * Creates a DL tag object.
   *
   * @param parent The parent HTML tag object.
   * @param type The type of this HTML tag.
   */
  public TagDL(HTMLTag parent, int type)
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
    Element[] elements = new Element[2];
    Paragraph p1 = createParagraph(" ");
    Paragraph p2 = createParagraph("");
    elements[0] = p1;
    elements[1] = p2;
    return elements;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#closeTagElements()
   */
  @Override
  public Element[] closeTagElements()
  {
    Element[] elements = new Element[2];
    Paragraph p1 = createParagraph(" ");
    Paragraph p2 = createParagraph("");
    elements[0] = p1;
    elements[1] = p2;
    return elements;
  }
}

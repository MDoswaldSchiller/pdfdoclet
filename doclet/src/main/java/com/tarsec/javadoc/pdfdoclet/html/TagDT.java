/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;

/**
 * Implements the DT tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagDT extends HTMLTag
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagDT.class);

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
  public Element[] openTagElements()
  {
    Element[] elements = new Element[1];
    Paragraph p1 = createParagraph("");
    elements[0] = p1;
    return elements;
  }
}

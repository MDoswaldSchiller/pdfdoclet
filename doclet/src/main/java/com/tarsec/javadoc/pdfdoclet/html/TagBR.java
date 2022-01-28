/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.lowagie.text.Element;

/**
 * Implements the BR tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagBR extends HTMLTag
{
  public TagBR(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  public Element[] openTagElements()
  {
    Element[] elements = new Element[1];
    elements[0] = createParagraph("");

    return elements;
  }
}

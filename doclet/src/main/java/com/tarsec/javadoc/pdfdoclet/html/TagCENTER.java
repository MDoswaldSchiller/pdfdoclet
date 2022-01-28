/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.lowagie.text.Element;

/**
 * Implements the CENTER tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagCENTER extends HTMLTag
{
  public TagCENTER(HTMLTag parent, int type)
  {
    super(parent, type);
    isCentered = true;
  }

  public Element[] openTagElements()
  {
    Element[] elements = new Element[1];
    elements[0] = createParagraph("");
    return elements;
  }

  public Element[] closeTagElements()
  {
    Element[] elements = new Element[1];
    elements[0] = createParagraph("\n");
    return elements;
  }
}

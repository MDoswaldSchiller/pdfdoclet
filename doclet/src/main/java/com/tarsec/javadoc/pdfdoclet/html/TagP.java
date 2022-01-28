/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;

/**
 * Implements a &lt;p&gt; tag
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagP extends HTMLTag
{
  private Paragraph open = null;

  public TagP(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  public Element[] openTagElements()
  {
    int parentType = parent.getType();
    if (parentType == TAG_BODY || parentType == TAG_CENTER) {
      Element[] elements = new Element[2];
      Paragraph p1 = createParagraph(" ");
      p1.setLeading(getLeading());
      Paragraph p2 = createParagraph("");
      p2.setLeading(parent.getLeading());
      elements[0] = p1;
      elements[1] = p2;
      return elements;
    }
    return null;
  }
}

/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.tarsec.javadoc.pdfdoclet.Fonts;

/**
 * Header tag (H1 - H6)
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagH extends HTMLTag
{
  public TagH(HTMLTag parent, int type)
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
    Paragraph p1 = createParagraph(new Chunk(" ", Fonts.getFont(TIMES_ROMAN, 16)));
    p1.setLeading(parent.getLeading());
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
    Paragraph p1 = createParagraph(new Chunk(" ", Fonts.getFont(TIMES_ROMAN, 16)));
    p1.setLeading((float) 10.0);
    Paragraph p2 = createParagraph("");
    p2.setLeading(parent.getLeading());
    elements[0] = p1;
    elements[1] = p2;
    return elements;
  }
}

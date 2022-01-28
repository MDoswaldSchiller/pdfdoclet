/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.tarsec.javadoc.pdfdoclet.Fonts;

/**
 * Header tag (H1 - H6)
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagH extends HTMLTag
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagH.class);

  public TagH(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
   */
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

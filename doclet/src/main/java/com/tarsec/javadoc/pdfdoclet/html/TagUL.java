/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.Paragraph;

/**
 * Implements an Unordered-List Tag (OL)
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagUL extends HTMLTag
{

  /**
   * Bullet symbol.
   */
  private static final String BULLET = "\u2022";

  /**
   * Stores list entries.
   */
  List list = null;

  /**
   *
   * @param parent
   * @param type
   */
  public TagUL(HTMLTag parent, int type)
  {
    super(parent, type);
    list = new List(false, 8);
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
   */
  @Override
  public Element[] openTagElements()
  {
    Font symbolFont = new Font(Font.UNDEFINED, getFont().size());
    list.setListSymbol(new Chunk(BULLET, symbolFont));
    list.setIndentationLeft(12);

    Element[] elements = new Element[1];
    elements[0] = new Paragraph(" ", getFont());
    return elements;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#closeTagElements()
   */
  @Override
  public Element[] closeTagElements()
  {
    if (this.parent.getType() == TAG_LI
        || this.parent.getType() == TAG_UL
        || this.parent.getType() == TAG_OL) {
      // If this list is nested in another ordered list, do not
      // add additional empty space at the end of if.
      Element[] entries = new Element[1];
      entries[0] = list;
      return entries;
    }
    else {
      Element[] entries = new Element[2];
      entries[0] = list;
      entries[1] = new Paragraph("");
      return entries;
    }
  }

  /*
	 *  (non-Javadoc)
	 * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#addNestedTagContent(com.lowagie.text.Element[])
   */
  @Override
  public void addNestedTagContent(Element[] content)
  {
    for (int i = 0; i < content.length; i++) {
      list.add(content[i]);
    }
  }
}

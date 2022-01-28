/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;


import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.ListItem;
import com.tarsec.javadoc.pdfdoclet.util.Util;

/**
 * Implements the list item tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagLI extends HTMLTag
{
  ListItem listEntry = new ListItem();

  public TagLI(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#toElement(java.lang.String)
   */
  @Override
  public Element toElement(String text)
  {
    listEntry.add(new Chunk(Util.stripLineFeeds(text), getFont()));
    return null;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#addNestedTagContent(com.lowagie.text.Element[])
   */
  @Override
  public void addNestedTagContent(Element[] content)
  {
    for (int i = 0; i < content.length; i++) {
      listEntry.add(content[i]);
    }
  }

  @Override
  public Element[] closeTagElements()
  {
    listEntry.setLeading(getFont().size() + (float) 1.0);
    Element[] entries = new Element[1];
    entries[0] = listEntry;
    return entries;
  }
}

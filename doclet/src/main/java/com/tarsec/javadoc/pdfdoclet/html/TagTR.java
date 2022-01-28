/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Implements the table row tag.
 *
 * @version $Revision: 1.1 $
 * @author Carl E. Lindberg
 */
public class TagTR extends HTMLTag
{

  public static final String ROW_START_ATTR = "pdfdoclet.table.row.start";

  public TagTR(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  /**
   * Override to only allow PdfPCell instances
   */
  @Override
  public void addNestedTagContent(Element[] elements)
  {
    for (int i = 0; elements != null && i < elements.length; i++) {
      if (elements[i] instanceof PdfPCell) {
        super.addNestedTagContent(new Element[]{elements[i]});
      }
    }
  }

  /**
   * Interior text is ignored between TR tags
   */
  @Override
  public Element toElement(String text)
  {
    return null;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#toPdfObjects()
   */
  @Override
  public Element[] toPdfObjects()
  {
    Element[] objs = super.toPdfObjects();
    if (objs != null && objs.length > 0 && objs[0] instanceof PdfPCell) {
      ((PdfPCell) objs[0]).setMarkupAttribute(ROW_START_ATTR, "1");
    }
    return objs;
  }
}

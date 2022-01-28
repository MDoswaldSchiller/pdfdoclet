/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Implements the THEAD tag.
 *
 * @version $Revision: 1.1 $
 * @author Carl E. Lindberg
 */
public class TagTHEAD extends HTMLTag
{

  /**
   *
   * @param parent
   * @param type
   */
  public TagTHEAD(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  /**
   * Override to only allow PdfPCell instances
   */
  public void addNestedTagContent(Element[] elements)
  {
    for (int i = 0; elements != null && i < elements.length; i++) {
      if (elements[i] instanceof PdfPCell) {
        PdfPCell cell = (PdfPCell) elements[i];
        /* Mark cell as a header */
        cell.setMarkupAttribute(TagTD.HEADER_INDICATOR_ATTR, "true");
        super.addNestedTagContent(new Element[]{cell});
      }
    }
  }

  /**
   * Interior text is ignored between THEAD tags
   */
  public Element toElement(String text)
  {
    return null;
  }

}

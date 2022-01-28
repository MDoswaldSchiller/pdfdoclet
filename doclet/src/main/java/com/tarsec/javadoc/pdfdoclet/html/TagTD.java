/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;

/**
 * TagCELL creates an iText Cell instance and returns it as the only conent from
 * getPdfObjects(), and is meant to be added to a Table instance created by the
 * TagTABLE class.
 *
 * @version $Revision: 1.1 $
 * @author Carl E. Lindberg
 */
public class TagTD extends HTMLTag
{

  /**
   * Constant value for a header-cell/row indicator attribute.
   */
  public static final String HEADER_INDICATOR_ATTR = "pdfdoclet.table.header.indicator";

  /**
   * Creates a cell tag object.
   *
   * @param parent The parent tag.
   * @param type The type of this tag.
   */
  public TagTD(HTMLTag parent, int type)
  {
    super(parent, type);
    if (type == TAG_TH) {
      isBold = true;
    }
  }

  /**
   *
   *
   * @param attrib
   * @param includeTable
   * @return
   */
  private String getInheritedAttribute(String attrib, boolean includeTable)
  {
    String value = getAttribute(attrib);
    HTMLTag currTag = parent;

    while (value == null && currTag != null) {
      int type = currTag.getType();
      if (type == TAG_TR || type == TAG_THEAD || (includeTable && type == TAG_TABLE)) {
        value = currTag.getAttribute(attrib);
      }
      if (type == TAG_TABLE) {
        break;
      }
      currTag = currTag.parent;
    }

    return value;
  }

  /**
   *
   * @param intString
   * @return
   */
  private int parseSpan(String intString)
  {
    int span = 0;
    try {
      if (intString != null) {
        span = Integer.parseInt(intString);
      }
    }
    catch (NumberFormatException e) {
      Util.error(e.toString());
    }

    return (span <= 0) ? 1 : span;
  }

  /**
   *
   * @param content
   * @return
   */
  private PdfPCell createCell(Element[] content)
  {

    int defaultAlign = (getType() == TAG_TH) ? Element.ALIGN_CENTER : Element.ALIGN_LEFT;

    String align = getInheritedAttribute("align", false);
    String valign = getInheritedAttribute("valign", false);
    String bgcolor = getInheritedAttribute("bgcolor", true);
    int alignment = HTMLTagUtil.getAlignment(align, defaultAlign);

    PdfPCell cell = PDFUtil.createElementCell(2, alignment, content);

    cell.setHorizontalAlignment(HTMLTagUtil.getAlignment(align, defaultAlign));
    cell.setVerticalAlignment(HTMLTagUtil.getVerticalAlignment(valign, Element.ALIGN_MIDDLE));
    cell.setBackgroundColor(HTMLTagUtil.getColor(bgcolor));
    cell.setColspan(parseSpan(getAttribute("colspan")));
    cell.setUseAscender(true);  // needs newer iText
    cell.setUseDescender(true); // needs newer iText
    cell.setUseBorderPadding(true); // needs newer iText
    if (getAttribute("nowrap") != null) {
      cell.setNoWrap(true);
    }
    if (getType() == TAG_TH) {
      cell.setMarkupAttribute(HEADER_INDICATOR_ATTR, "true");
    }

    return cell;
  }

  /**
   *
   */
  public Element[] toPdfObjects()
  {
    Element[] content = super.toPdfObjects();
    PdfPCell cell = createCell(content);
    return new Element[]{cell};
  }

}

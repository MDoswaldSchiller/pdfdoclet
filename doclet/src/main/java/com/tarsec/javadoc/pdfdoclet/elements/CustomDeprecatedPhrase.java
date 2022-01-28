/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import org.apache.log4j.Logger;

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;

/**
 * Custom phrase with 'deprecated' text. A phrase object is required because it
 * may contain link chunks.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CustomDeprecatedPhrase extends Phrase implements IConstants
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(CustomDeprecatedPhrase.class);

  public CustomDeprecatedPhrase(Doc doc)
  {
    Tag[] tags = doc.tags("deprecated");
    StringBuffer buffer = new StringBuffer("<i>");

    if ((tags != null) && (tags.length > 0)) {
      for (int i = 0; i < tags.length; i++) {
        buffer.append(JavadocUtil.getComment(tags[i].inlineTags()));
      }
    }
    buffer.append("</i>");

    String text = Util.stripLineFeeds(buffer.toString());
//        Phrase[] chunks = HtmlParserWrapper.createPdfObjects(text);
    Element[] chunks = HtmlParserWrapper.createPdfObjects(text);

    for (int i = 0; i < chunks.length; i++) {
      if ((chunks[i] instanceof PdfPTable) == false) {
        super.add(chunks[i]);
      }
    }
  }
}

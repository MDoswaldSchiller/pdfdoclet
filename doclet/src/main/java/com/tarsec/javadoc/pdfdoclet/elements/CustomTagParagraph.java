/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;


import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.Util;

/**
 * Customized version(s) of Paragraph.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class CustomTagParagraph extends Paragraph implements IConstants
{
  /**
   * Creates a paragraph of PDF phrases for the given tag text.
   *
   * @param isKeyValue if the text starts with a key name, e.g., the param tag.
   * @param text the text of the tag (starting with the key word, if any).
   */
  public CustomTagParagraph(boolean isKeyValue, String text)
  {
    super((float) 11.0);
    text = Util.stripLineFeeds(text).trim();

    if (isKeyValue) {
      int firstWhiteSpaceIndex = text.indexOf(" ");
      int firstTabIndex = text.indexOf("\t");

      if (firstTabIndex != -1) {
        if ((firstWhiteSpaceIndex == -1)
            || (firstTabIndex < firstWhiteSpaceIndex)) {
          firstWhiteSpaceIndex = firstTabIndex;
        }
      }

      if (firstWhiteSpaceIndex != -1) {
        // Parameter or exception with explanation
        String key = text.substring(0, firstWhiteSpaceIndex).trim();
        text = text.substring(firstWhiteSpaceIndex, text.length()).trim();

        Chunk keyChunk = new Chunk(key + " - ", Fonts.getFont(COURIER, 10));
        super.add(keyChunk);
      }
      else {
        // Parameter or exception without explanation
        Chunk keyChunk = new Chunk(text.trim(), Fonts.getFont(COURIER, 10));
        super.add(keyChunk);
        text = "";
      }
    }

    Element[] objs = HtmlParserWrapper.createPdfObjects(text);

    if (objs.length == 0) {
      super.add(new Chunk(text, Fonts.getFont(TIMES_ROMAN, 10)));
    }
    else {
      //descCell = new PdfPCell();
      for (int i = 0; i < objs.length; i++) {
        try {
          // PdfPTable objects cannot be added into a paragraph
          if (objs[i] instanceof TableParagraph) {
            TableParagraph tablePara = (TableParagraph) objs[i];
            super.add(tablePara.getTable());
          }
          else {
            super.add(objs[i]);
          }
        }
        catch (Exception e) {
          Util.error("Invalid tag text found, ignoring tag!");
        }
      }
    }
  }
}

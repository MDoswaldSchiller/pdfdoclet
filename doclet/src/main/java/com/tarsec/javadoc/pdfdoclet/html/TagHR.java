package com.tarsec.javadoc.pdfdoclet.html;

import java.awt.Color;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Graphic;
import com.lowagie.text.Paragraph;

public class TagHR extends HTMLTag
{

  public TagHR(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  /* Need a subclass to call addSpecial() */
  private static class HRParagraph extends Paragraph
  {

    private HRParagraph(Graphic hr)
    {
      super();
      add(Chunk.NEWLINE);
      addSpecial(hr);
    }
  }

  public Element[] openTagElements()
  {
    float height = Math.min(HTMLTagUtil.parseFloat(getAttribute("size"), 2.0f), 100f);
    float width;
    Color color = null;

    String widthStr = getAttribute("width");
    if (widthStr == null || !widthStr.endsWith("%")) {
      width = 100f;
    }
    else {
      width = HTMLTagUtil.parseFloat(widthStr.substring(0, widthStr.length() - 1), 100f);
    }

    String colorStr = getAttribute("color");
    if (colorStr == null) {
      colorStr = getAttribute("bgcolor");
    }
    color = HTMLTagUtil.getColor(colorStr);
    if (color == null) {
      color = HTMLTagUtil.getColor("gray");
    }

    // XXX new versions of iText support an "align" value here
    Graphic graphic = new Graphic();
    graphic.setHorizontalLine(height, width, color);

    /*
         * The returned elements here are added to Paragraph instances.
         * Since PdfPTable cannot be used this way, we need to put it
         * inside a special Paragraph instance in order for it to work.
     */
    return new Element[]{new HRParagraph(graphic)};
  }

}

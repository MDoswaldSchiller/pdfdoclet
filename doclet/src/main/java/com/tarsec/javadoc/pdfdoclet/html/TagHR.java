package com.tarsec.javadoc.pdfdoclet.html;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class TagHR extends HTMLTag
{

  public TagHR(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  /* Need a subclass to call addSpecial() */
  private static class HRParagraph extends Paragraph
  {

    private HRParagraph(Element hr)
    {
      super();
      add(Chunk.NEWLINE);
      addSpecial(hr);
    }
  }

  @Override
  public Element[] openTagElements()
  {
    float height = Math.min(HTMLTagUtil.parseFloat(getAttribute("size"), 2.0f), 100f);
    float width;
    BaseColor color = null;

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
    LineSeparator lineSeparator = new LineSeparator();
    lineSeparator.setLineWidth(height);
    lineSeparator.setPercentage(width);
    lineSeparator.setLineColor(color);
    
    /*
         * The returned elements here are added to Paragraph instances.
         * Since PdfPTable cannot be used this way, we need to put it
         * inside a special Paragraph instance in order for it to work.
     */
    return new Element[]{new HRParagraph(lineSeparator)};
  }

}

/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import java.awt.Color;
import java.util.HashMap;


import com.lowagie.text.Element;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides static utility methods for HTML and HTML tag handling.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class HTMLTagUtil implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(HTMLTagUtil.class);

  private static int UNKNOWN = -1;


  /**
   * Determines the type of a HTML tag by parsing it and gettings its type from
   * an internal table.
   *
   * @param text The HTML tag (opening tag or closing tag)
   * @return The type of the given tag.
   */
  public static int getTagType(String text)
  {
    int tagType = UNKNOWN;

    if (text.startsWith("</")) {
      text = text.substring(2, text.length());
    }

    if (text.startsWith("<")) {
      text = text.substring(1, text.length());
    }

    if (text.endsWith(">")) {
      text = text.substring(0, text.length() - 1);
    }

    if (text.indexOf(" ") != -1) {
      text = text.substring(0, text.indexOf(" "));
    }
    text = text.trim();

    for (int i = 0; (i < HTMLTag.TAGS.size()) && (tagType == UNKNOWN); i++) {
      if (text.equalsIgnoreCase(HTMLTag.TAGS.get(i))) {
        tagType = i;
      }
    }

    return tagType;
  }

  /**
   * Utility method to parse a float value, and return a default if the String
   * value is null or malformed.
   */
  public static float parseFloat(String str, float defaultValue)
  {
    float value = defaultValue;

    try {
      if (str != null) {
        value = Float.parseFloat(str);
      }
    }
    catch (NumberFormatException e) {
    }

    return value;
  }

  /**
   * Returns the Element constant associated with the specified horizontal
   * alignment (left, right, center, justified).
   */
  public static int getAlignment(String htmlAlignString, int defaultAlign)
  {
    if (htmlAlignString == null) {
      return defaultAlign;
    }
    if ("center".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_CENTER;
    }
    if ("right".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_RIGHT;
    }
    if ("left".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_LEFT;
    }
    if ("justify".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_JUSTIFIED;
    }
    return defaultAlign;
  }

  /**
   * Returns the Element constant associated with the specified vertical
   * alignment (top, middle, bottom, baseline).
   */
  public static int getVerticalAlignment(String htmlAlignString, int defaultAlign)
  {
    if (htmlAlignString == null) {
      return defaultAlign;
    }
    if ("top".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_TOP;
    }
    if ("middle".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_MIDDLE;
    }
    if ("bottom".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_BOTTOM;
    }
    if ("baseline".equalsIgnoreCase(htmlAlignString)) {
      return Element.ALIGN_BASELINE;
    }
    return defaultAlign;
  }

  /**
   * Returns the Color instance for the given HTML color string. The string can
   * be a color name or sRGB value ("#FF0000" for red, etc.)
   */
  public static Color getColor(String htmlColorString)
  {
    if (htmlColorString == null || htmlColorString.length() == 0) {
      return null;
    }

    Color color = (Color) colorMap.get(htmlColorString.toLowerCase());
    if (color == null) {
      if (htmlColorString.startsWith("#")) {
        htmlColorString = htmlColorString.substring(1);
      }
      try {
        color = new Color(Integer.parseInt(htmlColorString, 16));
      }
      catch (NumberFormatException e) {
        LOG.debug("Bad HTML color value " + htmlColorString, e);
        /* leave color as null */
      }
    }

    return color;
  }

  private static HashMap colorMap;

  static {
    colorMap = new HashMap();

    /* The 16 official HTML 4.0 colors */
    colorMap.put("aqua", new Color(0x00FFFF));
    colorMap.put("black", new Color(0x000000));
    colorMap.put("blue", new Color(0x0000FF));
    colorMap.put("fuchsia", new Color(0xFF00FF));
    colorMap.put("gray", new Color(0x808080));
    colorMap.put("green", new Color(0x008000));
    colorMap.put("lime", new Color(0x00FF00));
    colorMap.put("maroon", new Color(0x800000));
    colorMap.put("navy", new Color(0x000080));
    colorMap.put("olive", new Color(0x808000));
    colorMap.put("purple", new Color(0x800080));
    colorMap.put("red", new Color(0xFF0000));
    colorMap.put("silver", new Color(0xC0C0C0));
    colorMap.put("teal", new Color(0x008080));
    colorMap.put("white", new Color(0xFFFFFF));
    colorMap.put("yellow", new Color(0xFFFF00));

    /* Others that seem to be a more-or-less standard */
    colorMap.put("aliceblue", new Color(0xf0f8ff));
    colorMap.put("antiquewhite", new Color(0xfaebd7));
    colorMap.put("aquamarine", new Color(0x7fffd4));
    colorMap.put("azure", new Color(0xf0ffff));
    colorMap.put("beige", new Color(0xf5f5dc));
    colorMap.put("bisque", new Color(0xffe4c4));
    colorMap.put("blanchedalmond", new Color(0xffebcd));
    colorMap.put("blueviolet", new Color(0x8a2be2));
    colorMap.put("burlywood", new Color(0xdeb887));
    colorMap.put("cadetblue", new Color(0x5f9ea0));
    colorMap.put("chartreuse", new Color(0x7fff00));
    colorMap.put("chocolate", new Color(0xd2691e));
    colorMap.put("coral", new Color(0xff7f50));
    colorMap.put("cornflowerblue", new Color(0x6495ed));
    colorMap.put("cornsilk", new Color(0xfff8dc));
    colorMap.put("crimson", new Color(0xdc143c));
    colorMap.put("darkblue", new Color(0x00008b));
    colorMap.put("darkcyan", new Color(0x008b8b));
    colorMap.put("darkgoldenrod", new Color(0xb8860b));
    colorMap.put("darkgray", new Color(0xa9a9a9));
    colorMap.put("darkgreen", new Color(0x006400));
    colorMap.put("darkkhaki", new Color(0xbdb76b));
    colorMap.put("darkmagenta", new Color(0x8b008b));
    colorMap.put("darkolivegreen", new Color(0x556b2f));
    colorMap.put("darkorange", new Color(0xff8c00));
    colorMap.put("darkorchid", new Color(0x9932cc));
    colorMap.put("darkred", new Color(0x8b0000));
    colorMap.put("darksalmon", new Color(0xe9967a));
    colorMap.put("darkseagreen", new Color(0x8fbc8f));
    colorMap.put("darkslateblue", new Color(0x483d8b));
    colorMap.put("darkslategray", new Color(0x2f4f4f));
    colorMap.put("darkturquoise", new Color(0x00ced1));
    colorMap.put("darkviolet", new Color(0x9400d3));
    colorMap.put("deeppink", new Color(0xff1493));
    colorMap.put("deepskyblue", new Color(0x00bfff));
    colorMap.put("dimgray", new Color(0x696969));
    colorMap.put("dodgerblue", new Color(0x1e90ff));
    colorMap.put("firebrick", new Color(0xb22222));
    colorMap.put("floralwhite", new Color(0xfffaf0));
    colorMap.put("forestgreen", new Color(0x228b22));
    colorMap.put("gainsboro", new Color(0xdcdcdc));
    colorMap.put("ghostwhite", new Color(0xf8f8ff));
    colorMap.put("gold", new Color(0xffd700));
    colorMap.put("goldenrod", new Color(0xdaa520));
    colorMap.put("greenyellow", new Color(0xadff2f));
    colorMap.put("honeydew", new Color(0xf0fff0));
    colorMap.put("hotpink", new Color(0xff69b4));
    colorMap.put("indianred", new Color(0xcd5c5c));
    colorMap.put("indigo", new Color(0x4b0082));
    colorMap.put("ivory", new Color(0xfffff0));
    colorMap.put("khaki", new Color(0xf0e68c));
    colorMap.put("lavender", new Color(0xe6e6fa));
    colorMap.put("lavenderblush", new Color(0xfff0f5));
    colorMap.put("lawngreen", new Color(0x7cfc00));
    colorMap.put("lemonchiffon", new Color(0xfffacd));
    colorMap.put("lightblue", new Color(0xadd8e6));
    colorMap.put("lightcoral", new Color(0xf08080));
    colorMap.put("lightcyan", new Color(0xe0ffff));
    colorMap.put("lightgoldenrodyellow", new Color(0xfafad2));
    colorMap.put("lightgreen", new Color(0x90ee90));
    colorMap.put("lightgrey", new Color(0xd3d3d3));
    colorMap.put("lightpink", new Color(0xffb6c1));
    colorMap.put("lightsalmon", new Color(0xffa07a));
    colorMap.put("lightseagreen", new Color(0x20b2aa));
    colorMap.put("lightskyblue", new Color(0x87cefa));
    colorMap.put("lightslategray", new Color(0x778899));
    colorMap.put("lightsteelblue", new Color(0xb0c4de));
    colorMap.put("lightyellow", new Color(0xffffe0));
    colorMap.put("limegreen", new Color(0x32cd32));
    colorMap.put("linen", new Color(0xfaf0e6));
    colorMap.put("mediumaquamarine", new Color(0x66cdaa));
    colorMap.put("mediumblue", new Color(0x0000cd));
    colorMap.put("mediumorchid", new Color(0xba55d3));
    colorMap.put("mediumpurple", new Color(0x9370db));
    colorMap.put("mediumseagreen", new Color(0x3cb371));
    colorMap.put("mediumslateblue", new Color(0x7b68ee));
    colorMap.put("mediumspringgreen", new Color(0x00fa9a));
    colorMap.put("mediumturquoise", new Color(0x48d1cc));
    colorMap.put("mediumvioletred", new Color(0xc71585));
    colorMap.put("midnightblue", new Color(0x191970));
    colorMap.put("mintcream", new Color(0xf5fffa));
    colorMap.put("mistyrose", new Color(0xffe4e1));
    colorMap.put("moccasin", new Color(0xffe4b5));
    colorMap.put("navajowhite", new Color(0xffdead));
    colorMap.put("oldlace", new Color(0xfdf5e6));
    colorMap.put("olivedrab", new Color(0x6b8e23));
    colorMap.put("orange", new Color(0xffa500));
    colorMap.put("orangered", new Color(0xff4500));
    colorMap.put("orchid", new Color(0xda70d6));
    colorMap.put("palegoldenrod", new Color(0xeee8aa));
    colorMap.put("palegreen", new Color(0x98fb98));
    colorMap.put("paleturquoise", new Color(0xafeeee));
    colorMap.put("palevioletred", new Color(0xdb7093));
    colorMap.put("papayawhip", new Color(0xffefd5));
    colorMap.put("peachpuff", new Color(0xffdab9));
    colorMap.put("peru", new Color(0xcd853f));
    colorMap.put("pink", new Color(0xffc0cb));
    colorMap.put("plum", new Color(0xdda0dd));
    colorMap.put("powderblue", new Color(0xb0e0e6));
    colorMap.put("rosybrown", new Color(0xbc8f8f));
    colorMap.put("royalblue", new Color(0x4169e1));
    colorMap.put("saddlebrown", new Color(0x8b4513));
    colorMap.put("salmon", new Color(0xfa8072));
    colorMap.put("sandybrown", new Color(0xf4a460));
    colorMap.put("seagreen", new Color(0x2e8b57));
    colorMap.put("seashell", new Color(0xfff5ee));
    colorMap.put("sienna", new Color(0xa0522d));
    colorMap.put("skyblue", new Color(0x87ceeb));
    colorMap.put("slateblue", new Color(0x6a5acd));
    colorMap.put("slategray", new Color(0x708090));
    colorMap.put("snow", new Color(0xfffafa));
    colorMap.put("springgreen", new Color(0x00ff7f));
    colorMap.put("steelblue", new Color(0x4682b4));
    colorMap.put("tan", new Color(0xd2b48c));
    colorMap.put("thistle", new Color(0xd8bfd8));
    colorMap.put("tomato", new Color(0xff6347));
    colorMap.put("turquoise", new Color(0x40e0d0));
    colorMap.put("violet", new Color(0xee82ee));
    colorMap.put("wheat", new Color(0xf5deb3));
    colorMap.put("whitesmoke", new Color(0xf5f5f5));
    colorMap.put("yellowgreen", new Color(0x9acd32));
  }
}

/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import java.util.Map;
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
  public static BaseColor getColor(String htmlColorString)
  {
    if (htmlColorString == null || htmlColorString.length() == 0) {
      return null;
    }

    BaseColor color = colorMap.get(htmlColorString.toLowerCase());
    if (color == null) {
      if (htmlColorString.startsWith("#")) {
        htmlColorString = htmlColorString.substring(1);
      }
      try {
        color = new BaseColor(Integer.parseInt(htmlColorString, 16));
      }
      catch (NumberFormatException e) {
        LOG.debug("Bad HTML color value " + htmlColorString, e);
        /* leave color as null */
      }
    }

    return color;
  }

  private static final Map<String,BaseColor> colorMap;

  static {
    colorMap = Map.ofEntries(
        Map.entry("aqua", new BaseColor(0x00FFFF)),
        Map.entry("black", new BaseColor(0x000000)),
        Map.entry("blue", new BaseColor(0x0000FF)),
        Map.entry("fuchsia", new BaseColor(0xFF00FF)),
        Map.entry("gray", new BaseColor(0x808080)),
        Map.entry("green", new BaseColor(0x008000)),
        Map.entry("lime", new BaseColor(0x00FF00)),
        Map.entry("maroon", new BaseColor(0x800000)),
        Map.entry("navy", new BaseColor(0x000080)),
        Map.entry("olive", new BaseColor(0x808000)),
        Map.entry("purple", new BaseColor(0x800080)),
        Map.entry("red", new BaseColor(0xFF0000)),
        Map.entry("silver", new BaseColor(0xC0C0C0)),
        Map.entry("teal", new BaseColor(0x008080)),
        Map.entry("white", new BaseColor(0xFFFFFF)),
        Map.entry("yellow", new BaseColor(0xFFFF00)),

    /* Others that seem to be a more-or-less standard */
        Map.entry("aliceblue", new BaseColor(0xf0f8ff)),
        Map.entry("antiquewhite", new BaseColor(0xfaebd7)),
        Map.entry("aquamarine", new BaseColor(0x7fffd4)),
        Map.entry("azure", new BaseColor(0xf0ffff)),
        Map.entry("beige", new BaseColor(0xf5f5dc)),
        Map.entry("bisque", new BaseColor(0xffe4c4)),
        Map.entry("blanchedalmond", new BaseColor(0xffebcd)),
        Map.entry("blueviolet", new BaseColor(0x8a2be2)),
        Map.entry("burlywood", new BaseColor(0xdeb887)),
        Map.entry("cadetblue", new BaseColor(0x5f9ea0)),
        Map.entry("chartreuse", new BaseColor(0x7fff00)),
        Map.entry("chocolate", new BaseColor(0xd2691e)),
        Map.entry("coral", new BaseColor(0xff7f50)),
        Map.entry("cornflowerblue", new BaseColor(0x6495ed)),
        Map.entry("cornsilk", new BaseColor(0xfff8dc)),
        Map.entry("crimson", new BaseColor(0xdc143c)),
        Map.entry("darkblue", new BaseColor(0x00008b)),
        Map.entry("darkcyan", new BaseColor(0x008b8b)),
        Map.entry("darkgoldenrod", new BaseColor(0xb8860b)),
        Map.entry("darkgray", new BaseColor(0xa9a9a9)),
        Map.entry("darkgreen", new BaseColor(0x006400)),
        Map.entry("darkkhaki", new BaseColor(0xbdb76b)),
        Map.entry("darkmagenta", new BaseColor(0x8b008b)),
        Map.entry("darkolivegreen", new BaseColor(0x556b2f)),
        Map.entry("darkorange", new BaseColor(0xff8c00)),
        Map.entry("darkorchid", new BaseColor(0x9932cc)),
        Map.entry("darkred", new BaseColor(0x8b0000)),
        Map.entry("darksalmon", new BaseColor(0xe9967a)),
        Map.entry("darkseagreen", new BaseColor(0x8fbc8f)),
        Map.entry("darkslateblue", new BaseColor(0x483d8b)),
        Map.entry("darkslategray", new BaseColor(0x2f4f4f)),
        Map.entry("darkturquoise", new BaseColor(0x00ced1)),
        Map.entry("darkviolet", new BaseColor(0x9400d3)),
        Map.entry("deeppink", new BaseColor(0xff1493)),
        Map.entry("deepskyblue", new BaseColor(0x00bfff)),
        Map.entry("dimgray", new BaseColor(0x696969)),
        Map.entry("dodgerblue", new BaseColor(0x1e90ff)),
        Map.entry("firebrick", new BaseColor(0xb22222)),
        Map.entry("floralwhite", new BaseColor(0xfffaf0)),
        Map.entry("forestgreen", new BaseColor(0x228b22)),
        Map.entry("gainsboro", new BaseColor(0xdcdcdc)),
        Map.entry("ghostwhite", new BaseColor(0xf8f8ff)),
        Map.entry("gold", new BaseColor(0xffd700)),
        Map.entry("goldenrod", new BaseColor(0xdaa520)),
        Map.entry("greenyellow", new BaseColor(0xadff2f)),
        Map.entry("honeydew", new BaseColor(0xf0fff0)),
        Map.entry("hotpink", new BaseColor(0xff69b4)),
        Map.entry("indianred", new BaseColor(0xcd5c5c)),
        Map.entry("indigo", new BaseColor(0x4b0082)),
        Map.entry("ivory", new BaseColor(0xfffff0)),
        Map.entry("khaki", new BaseColor(0xf0e68c)),
        Map.entry("lavender", new BaseColor(0xe6e6fa)),
        Map.entry("lavenderblush", new BaseColor(0xfff0f5)),
        Map.entry("lawngreen", new BaseColor(0x7cfc00)),
        Map.entry("lemonchiffon", new BaseColor(0xfffacd)),
        Map.entry("lightblue", new BaseColor(0xadd8e6)),
        Map.entry("lightcoral", new BaseColor(0xf08080)),
        Map.entry("lightcyan", new BaseColor(0xe0ffff)),
        Map.entry("lightgoldenrodyellow", new BaseColor(0xfafad2)),
        Map.entry("lightgreen", new BaseColor(0x90ee90)),
        Map.entry("lightgrey", new BaseColor(0xd3d3d3)),
        Map.entry("lightpink", new BaseColor(0xffb6c1)),
        Map.entry("lightsalmon", new BaseColor(0xffa07a)),
        Map.entry("lightseagreen", new BaseColor(0x20b2aa)),
        Map.entry("lightskyblue", new BaseColor(0x87cefa)),
        Map.entry("lightslategray", new BaseColor(0x778899)),
        Map.entry("lightsteelblue", new BaseColor(0xb0c4de)),
        Map.entry("lightyellow", new BaseColor(0xffffe0)),
        Map.entry("limegreen", new BaseColor(0x32cd32)),
        Map.entry("linen", new BaseColor(0xfaf0e6)),
        Map.entry("mediumaquamarine", new BaseColor(0x66cdaa)),
        Map.entry("mediumblue", new BaseColor(0x0000cd)),
        Map.entry("mediumorchid", new BaseColor(0xba55d3)),
        Map.entry("mediumpurple", new BaseColor(0x9370db)),
        Map.entry("mediumseagreen", new BaseColor(0x3cb371)),
        Map.entry("mediumslateblue", new BaseColor(0x7b68ee)),
        Map.entry("mediumspringgreen", new BaseColor(0x00fa9a)),
        Map.entry("mediumturquoise", new BaseColor(0x48d1cc)),
        Map.entry("mediumvioletred", new BaseColor(0xc71585)),
        Map.entry("midnightblue", new BaseColor(0x191970)),
        Map.entry("mintcream", new BaseColor(0xf5fffa)),
        Map.entry("mistyrose", new BaseColor(0xffe4e1)),
        Map.entry("moccasin", new BaseColor(0xffe4b5)),
        Map.entry("navajowhite", new BaseColor(0xffdead)),
        Map.entry("oldlace", new BaseColor(0xfdf5e6)),
        Map.entry("olivedrab", new BaseColor(0x6b8e23)),
        Map.entry("orange", new BaseColor(0xffa500)),
        Map.entry("orangered", new BaseColor(0xff4500)),
        Map.entry("orchid", new BaseColor(0xda70d6)),
        Map.entry("palegoldenrod", new BaseColor(0xeee8aa)),
        Map.entry("palegreen", new BaseColor(0x98fb98)),
        Map.entry("paleturquoise", new BaseColor(0xafeeee)),
        Map.entry("palevioletred", new BaseColor(0xdb7093)),
        Map.entry("papayawhip", new BaseColor(0xffefd5)),
        Map.entry("peachpuff", new BaseColor(0xffdab9)),
        Map.entry("peru", new BaseColor(0xcd853f)),
        Map.entry("pink", new BaseColor(0xffc0cb)),
        Map.entry("plum", new BaseColor(0xdda0dd)),
        Map.entry("powderblue", new BaseColor(0xb0e0e6)),
        Map.entry("rosybrown", new BaseColor(0xbc8f8f)),
        Map.entry("royalblue", new BaseColor(0x4169e1)),
        Map.entry("saddlebrown", new BaseColor(0x8b4513)),
        Map.entry("salmon", new BaseColor(0xfa8072)),
        Map.entry("sandybrown", new BaseColor(0xf4a460)),
        Map.entry("seagreen", new BaseColor(0x2e8b57)),
        Map.entry("seashell", new BaseColor(0xfff5ee)),
        Map.entry("sienna", new BaseColor(0xa0522d)),
        Map.entry("skyblue", new BaseColor(0x87ceeb)),
        Map.entry("slateblue", new BaseColor(0x6a5acd)),
        Map.entry("slategray", new BaseColor(0x708090)),
        Map.entry("snow", new BaseColor(0xfffafa)),
        Map.entry("springgreen", new BaseColor(0x00ff7f)),
        Map.entry("steelblue", new BaseColor(0x4682b4)),
        Map.entry("tan", new BaseColor(0xd2b48c)),
        Map.entry("thistle", new BaseColor(0xd8bfd8)),
        Map.entry("tomato", new BaseColor(0xff6347)),
        Map.entry("turquoise", new BaseColor(0x40e0d0)),
        Map.entry("violet", new BaseColor(0xee82ee)),
        Map.entry("wheat", new BaseColor(0xf5deb3)),
        Map.entry("whitesmoke", new BaseColor(0xf5f5f5)),
        Map.entry("yellowgreen", new BaseColor(0x9acd32))
    );
  }
}

/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.awt.Color;
import java.io.File;
import java.util.Properties;


import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles fonts loading and creation.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Fonts implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(Fonts.class);

  /**
   * Stores mapping of font face types to font files
   */
  private static Properties fontTable = new Properties();

  /**
   * Stores encoding of every font
   */
  private static Properties encTable = new Properties();

  static {
    /*
> Chinese simplified
> STSong-Light with the encodings UniGB-UCS2-H and UniGB-UCS2-V
> 
> Chinese traditional
> MHei-Medium and MSung-Light with the encodings UniCNS-UCS2-H and
> UniCNS-UCS2-V
> 
> Japanese
> HeiseiKakuGo-W5 and HeiseiMin-W3 with the encodings
> UniJIS-UCS2-H,UniJIS-UCS2-V,UniJIS-UCS2-HW-H and UniJIS-UCS2-HW-V
> 
> Korean
> HYGoThic-Medium and HYSMyeongJo-Medium with the encodings UniKS-UCS2-H and
> UniKS-UCS2-V
> 
> "HeiseiMin-W3",
> "UniJIS-UCS2-HW-H",
     */
 /*        
        fontTable.setProperty(String.valueOf(COURIER),
        	"fonts/Abduction.ttf");
     */
  }

  /**
   *
   * @param filename
   * @param encoding
   * @param type
   */
  public static void mapFont(String filename, String encoding, int type)
  {
    if (type != COURIER && type != TIMES_ROMAN) {
      throw new IllegalArgumentException("Invalid font type: " + type);
    }
    fontTable.put(String.valueOf(type), filename);
    if (encoding != null) {
      encTable.put(String.valueOf(type), encoding);
    }
  }

  /**
   * Returns a font of a certain face family and a certain size.
   *
   * @param faceType The face (TIMES_ROMAN, COURIER).
   * @param size The size in points.
   * @return The font object.
   * @see com.tarsec.javadoc.pdfdoclet.IConstants
   */
  public static Font getFont(int faceType, int size)
  {
    return getFont(faceType, PLAIN, size);
  }

  /**
   *
   * @param faceType
   * @param style
   * @param size
   * @return
   */
  public static Font getFont(int faceType, int style, int size)
  {
    Font font = null;
    String lookup = String.valueOf(faceType);
    String fontFile = fontTable.getProperty(lookup);
    int fontStyle = Font.NORMAL;
    Color color = COLOR_BLACK;

    if ((style & LINK) != 0) {
      fontStyle += Font.UNDERLINE;
      color = COLOR_LINK;
    }
    else if ((style & UNDERLINE) != 0) {
      fontStyle += Font.UNDERLINE;
    }
    if ((style & STRIKETHROUGH) != 0) {
      fontStyle += Font.STRIKETHRU;
    }

    if (fontFile != null) {

      File file = new File(Configuration.getWorkDir(), fontFile);
      if (file.exists() && file.isFile()) {

        try {
          String encoding = encTable.getProperty(lookup, BaseFont.CP1252);
          BaseFont bfComic = BaseFont.createFont(file.getAbsolutePath(), encoding, BaseFont.EMBEDDED);

          if ((style & IConstants.ITALIC) > 0) {
            if ((style & IConstants.BOLD) > 0) {
              fontStyle += Font.BOLDITALIC;
            }
            else {
              fontStyle += Font.ITALIC;
            }
          }
          else if ((style & IConstants.BOLD) > 0) {
            fontStyle += Font.BOLD;
          }

          if (fontStyle != Font.NORMAL) {
            font = new Font(bfComic, size, fontStyle, color);
          }
          else {
            font = new Font(bfComic, size);
          }
          if (font == null) {
            throw new IllegalArgumentException("FONT NULL: " + fontFile);
          }

        }
        catch (Exception e) {

          e.printStackTrace();
          throw new IllegalArgumentException("FONT UNUSABLE");

        }

      }
      else {

        Util.error("Font file not found: " + fontFile);
        LOG.error("Cancelling processing of Javadoc, deleting PDF file.");
        PDFDoclet.getPdfFile().delete();
        System.exit(-1);

      }

    }
    else {

      // Use predefined font
      String face = "";

      if (faceType == TIMES_ROMAN) {

        face = FontFactory.TIMES_ROMAN;
        if ((style & IConstants.ITALIC) > 0) {
          if ((style & IConstants.BOLD) > 0) {
            face = FontFactory.TIMES_BOLDITALIC;
          }
          else {
            face = FontFactory.TIMES_ITALIC;
          }
        }
        else if ((style & IConstants.BOLD) > 0) {
          face = FontFactory.TIMES_BOLD;
        }

      }
      else {
        face = FontFactory.COURIER;
        if ((style & ITALIC) > 0) {
          if ((style & BOLD) > 0) {
            face = FontFactory.COURIER_BOLDOBLIQUE;
          }
          else {
            face = FontFactory.COURIER_OBLIQUE;
          }
        }
        else if ((style & BOLD) > 0) {
          face = FontFactory.COURIER_BOLD;
        }
      }

      if (fontStyle != Font.NORMAL) {
        font = FontFactory.getFont(face, size, fontStyle, color);
      }
      else {
        font = FontFactory.getFont(face, size);
      }
    }

    return font;
  }
}

/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.tarsec.javadoc.pdfdoclet.Configuration;
import com.tarsec.javadoc.pdfdoclet.Destinations;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Chunk with internal hyperlink, if possible. For instance, if the target is
 * outside the packages of the current javadoc, it is obviously impossible to
 * create a link. In such cases, the Chunk will just be plain text.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class LinkPhrase extends Phrase implements IConstants
{
  /**
   * Creates hyperlink chunk where the font is defined after the following
   * rules:
   * <ol>
   * <li>If it's a link, its courier, underlined</li>
   * <li>If the parameter "isTimesRoman" is true, it is regular TimesRoman</li>
   * <li>Otherwise, it's regular courier</li>
   * </ol>
   * <p>
   * The size defaults to 10 if a value less or equal than zero is given.
   *
   * @param destination
   * @param label
   * @param size
   * @param isTimesRoman
   */
  public LinkPhrase(String destination, String label, int size, boolean isTimesRoman)
  {
    super("");

    Font font = null;

    if (!Configuration.isLinksCreationActive()) {
      destination = "";
    }

    if (size < 9) {
      throw new RuntimeException("INVALID SIZE");
    }
    if (size == 0) {
      size = 9;
    }

    destination = normalizeDestination(destination);

    if (Destinations.isValid(destination)) {
      if (isTimesRoman) {
        font = Fonts.getFont(TIMES_ROMAN, LINK, size);
      }
      else {
        font = Fonts.getFont(COURIER, LINK, size);
      }
    }
    else if (isTimesRoman) {
      font = Fonts.getFont(TIMES_ROMAN, size);
    }
    else {
      font = Fonts.getFont(COURIER, size);
    }
    init(destination, label, font);
  }

  /**
   * Creates a hyperlink chunk.
   *
   * @param destination The original destination as defined in the javadoc.
   * @param label The text label for the link
   * @param font The base font for the link (for example, could be a bold italic
   * font in case of a "deprecated" tag).
   */
  public LinkPhrase(String destination, String label, Font font)
  {
    super("");
    Font newFont = null;
    float size = font.getSize();
    if (size == 0) {
      size = 9;
    }

    if (!Configuration.isLinksCreationActive()) {
      destination = "";
    }

    destination = normalizeDestination(destination);

    if (Destinations.isValid(destination)) {
      if (font.getFamily() == Font.FontFamily.TIMES_ROMAN) {
        newFont = Fonts.getFont(TIMES_ROMAN, LINK, (int) size);
      }
      else {
        newFont = Fonts.getFont(COURIER, LINK, (int) size);
      }
    }
    else if (font.getFamily()== Font.FontFamily.TIMES_ROMAN) {
      newFont = Fonts.getFont(TIMES_ROMAN, (int) size);
    }
    else {
      newFont = Fonts.getFont(COURIER, (int) size);
    }

    init(destination, label, newFont);
  }

  private static String normalizeDestination(String destination)
  {
    if (destination == null) {
      return "";
    }

    // Tidy can encode spaces etc. in the URLs... (i.e. %20)
    try {
      return URLDecoder.decode(destination, "UTF-8").trim();
    }
    catch (UnsupportedEncodingException e) {
      return destination;
    }
  }

  /**
   * Initializes the link chunk with given values.
   *
   * @param destination The original destination as defined in the javadoc.
   * @param label The text label for the link
   * @param font The base font for the link (for example, could be a bold italic
   * font in case of a "deprecated" tag).
   */
  private void init(String destination, String label, Font font)
  {

    if (label == null) {
      label = destination;
    }

    String createLinksProp = Configuration.getProperty(ARG_CREATE_LINKS, ARG_VAL_NO);
    if (createLinksProp.equalsIgnoreCase(ARG_VAL_NO)) {
      destination = null;
    }

    super.font = font;
    Chunk chunk = new Chunk("");
    chunk.append(label);
    if (destination != null && Destinations.isValid(destination)) {
      chunk.setLocalGoto(destination);
    }

    add(chunk);
  }
}

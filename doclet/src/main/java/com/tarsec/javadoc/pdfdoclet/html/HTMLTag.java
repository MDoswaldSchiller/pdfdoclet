/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for HTML tags. This class also includes the HTML parsing code.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public abstract class HTMLTag implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(HTMLTag.class);

  /**
   * Array with HTML tag names. NOTE: The contents of this table relate directly
   * to the HTML tag constants defined in the {
   *
   * @see com.tarsec.javadoc.pdfdoclet.IConstants IConstants} interface.
   */
  protected static final java.util.List<String> TAGS = java.util.List.of(
        "body",
        "p",
        "br",
        "code",
        "pre",
        "blockquote",
        "center",
        "table",
        "tr",
        "td",
        "i",
        "b",
        "tt",
        "ul",
        "ol",
        "li",
        "em",
        "a",
        "h1",
        "h2",
        "h3",
        "h4",
        "h5",
        "h6",
        "img",
        "u",
        "newpage",
        "th",
        "thead",
        "hr",
        "s",
        "strike",
        "del",
        "ins",
        "dl",
        "dt",
        "dd",
        "strong");
  private Map attributes = new HashMap();
  protected HTMLTag parent = null;

  // List of String or (nested) HTMLTag objects
  private ArrayList contentTags = new ArrayList(100);
  private ArrayList contentPdf = null;
  private int currentEntry = 0;
  protected boolean isItalic = false;
  protected boolean isBold = false;
  protected boolean isCode = false;
  protected boolean isUnderline = false;
  protected boolean isStrikethrough = false;
  protected boolean isPre = false;
  protected boolean isLink = false;
  protected boolean isCentered = false;
  protected boolean isRight = false;
  protected int fontSize = 10;
  private int type = -1;

  /**
   * Creates a HTML tag object.
   *
   * @param parent The parent HTML tag object (or null).
   * @param type
   * @param node
   */
  public HTMLTag(HTMLTag parent, int type)
  {
    if (parent != null) {
      setParent(parent);
    }

    this.type = type;
  }

  /**
   * Sets the parent tag for this HTML tag. Which means that this tag is nested
   * into the specified parent tag. This method also ensures that this tag
   * inherits certain attributes from the parent tag (bold or italic text,
   * link).
   *
   * @param parent The parent tag.
   */
  public void setParent(HTMLTag parent)
  {
    this.parent = parent;

    if (parent == null) {
      return;
    }

    if (parent.isCentered()) {
      this.isCentered = true;
    }

    if (parent.isItalic()) {
      this.isItalic = true;
    }

    if (parent.isBold()) {
      this.isBold = true;
    }

    if (parent.isUnderline()) {
      this.isUnderline = true;
    }

    if (parent.isStrikethrough()) {
      this.isStrikethrough = true;
    }

    if (parent.isCode()) {
      this.isCode = true;
    }

    if (parent.isLink()) {
      this.isLink = true;
    }

    if (parent.isPre()) {
      this.isPre = true;
    }
  }

  /**
   * Returns one or several PDF objects which best represent this HTML tag.
   *
   * @return The PDF element(s) for this HTML tag. May be null if there was a
   * problem.
   */
  public Element[] toPdfObjects()
  {

    LOG.debug("> MARK: HTML tag type: " + getType());

    try {
      contentPdf = new ArrayList(100);

      Element[] openTagStuff = openTagElements();
      if (openTagStuff != null && openTagStuff.length > 0) {
        for (int i = 0; i < openTagStuff.length; i++) {
          contentPdf.add(openTagStuff[i]);
        }
      }

      for (int i = 0; i < contentTags.size(); i++) {
        Object obj = contentTags.get(i);

        if (obj instanceof java.lang.String) {

          String text = (String) obj;
          if (text.length() > 0) {
            if (!isPre()) {
              // For all tag types other than PRE, remove hard-coded
              // line breaks and encode additional blanks between 
              // nested tag borders where necessary
              text = Util.stripLineFeeds(text);
            }
            Element elem = toElement(text);
            if (elem != null) {
              contentPdf.add(elem);
            }
          }

        }
        else {

          HTMLTag tag = (HTMLTag) obj;
          Element[] subElements = tag.toPdfObjects();
          if (subElements != null && subElements.length > 0) {
            addNestedTagContent(subElements);
          }

        }
      }

      Element[] closeTagStuff = closeTagElements();
      if (closeTagStuff != null && closeTagStuff.length > 0) {
        for (int i = 0; i < closeTagStuff.length; i++) {
          contentPdf.add(closeTagStuff[i]);
        }
      }

      Element[] elements = new Element[contentPdf.size()];
      elements = (Element[]) contentPdf.toArray(elements);

      return elements;

    }
    catch (RuntimeException e) {

      Util.error("Failed to create PDF objects", e);

    }

    LOG.debug("<");

    return null;
  }

  /**
   * Adds elements of a nested tag to the contents of this HTML tag.
   *
   * @param elements The PDF elements of the nexted tag.
   */
  public void addNestedTagContent(Element[] elements)
  {
    // special hack for list elements without specified
    // list type (<li> tags without <ul> or <ol>)
    boolean missingList = false;
    for (int i = 0; i < elements.length; i++) {
      if (elements[i] instanceof ListItem) {
        // In this method, there should never by any
        // elements of type ListItem to be added, 
        // because those should be added to the tags
        // UL and OL. And those classes override this
        // method with a custom implementation.
        // If there are ListItem objects to add here,
        // it means that in the text there were some
        // <li> </li> tags without any surrounding
        // <ul> or <ol> list definition.
        missingList = true;
      }
    }

    List list = null;
    if (missingList) {
      list = new List(false, 8);
    }

    for (int i = 0; i < elements.length; i++) {
      if (missingList && elements[i] instanceof ListItem) {
        list.add(elements[i]);
      }
      else {
        contentPdf.add(elements[i]);
      }
    }

    if (missingList && list != null) {
      contentPdf.add(list);
    }
  }

  /**
   * This method must be implemented by subclasses in order to provide a
   * mechanism to convert an HTML tag into a PDF document object.
   *
   * @param text The text with HTML tags.
   * @return The PDF Element representint that HTML code.
   */
  public Element toElement(String text)
  {
    if (!isPre()) {
      text = Util.stripLineFeeds(text);
    }
    Phrase result = new Phrase();
    result.add(new Chunk(text, getFont()));
    return result;
  }

  /**
   * Returns any number of PDF Elements preceeding a given HTML tag. For a
   * H1-H6, for example, there will always be a preceeding Paragraph in order to
   * provide some leading space. This default method returns no objects.
   *
   * @return Any number of PDF Elements (may be null).
   */
  public Element[] openTagElements()
  {
    return null;
  }

  /**
   * Returns any number of PDF Elements following a given HTML tag. For a H1-H6,
   * for example, there will always be a Paragraph following in order to provide
   * some additional space. This default method returns no objects.
   *
   * @return Any number of PDF Elements (may be null).
   */
  public Element[] closeTagElements()
  {
    return null;
  }

  /**
   * Returns the leading for this type of HTML tag in the PDF document. For most
   * tags it's just about the font size.
   *
   * @return The leading for this tag.
   */
  public float getLeading()
  {
    Font font = getFont();
    float leading = (float) font.getSize() + (float) 1.0;
    return leading;
  }

  /**
   * Creates a PDF Paragraph with the appropriate alignment and the default
   * leading and correct font.
   *
   * @param content The text that goes into the Paragraph.
   * @return The resulting PDF Paragraph object.
   */
  public Paragraph createParagraph(String content)
  {
    return createParagraph(new Chunk(content, getFont()));
  }

  /**
   * Creates a PDF Paragraph with the appropriate alignment and the default
   * leading and correct font.
   *
   * @param content The Chunk that goes into the Paragraph.
   * @return The resulting PDF Paragraph object.
   */
  public Paragraph createParagraph(Chunk content)
  {
    Paragraph result = new Paragraph(getLeading(), content);

    if (isCentered()) {
      result.setAlignment(Element.ALIGN_CENTER);
    }

    if (isRight()) {
      result.setAlignment(Element.ALIGN_RIGHT);
    }

    return result;
  }

  /**
   * Returns the appropriate PDF font for this HTML tag. The font is created
   * based on the type and attributes of the tag.
   *
   * @return The PDF document font.
   */
  public Font getFont()
  {
    int face = TIMES_ROMAN;
    boolean parentIsFixedFont = false;

    if (isCode || isPre) {
      face = COURIER;
    }

    if (parent != null) {
      fontSize = parent.fontSize;
      parentIsFixedFont = parent.isCode() || parent.isPre();
    }

    // Pre-formatted text parts tend to appear 
    // bigger, so make them 1 point smaller
    if ((isCode || isPre) && !parentIsFixedFont) {
      fontSize--;
    }

    if (type == TAG_I) {
      isItalic = true;
    }

    if (type == TAG_B) {
      isBold = true;
    }

    if (type == TAG_U) {
      isUnderline = true;
    }

    if (type == TAG_H1) {
      fontSize = 26;
    }

    if (type == TAG_H2) {
      fontSize = 22;
    }

    if (type == TAG_H3) {
      fontSize = 19;
    }

    if (type == TAG_H4) {
      fontSize = 16;
    }

    if (type == TAG_H5) {
      fontSize = 13;
    }

    if (type == TAG_H6) {
      fontSize = 10;
    }

    int style = 0;

    if (isBold && isItalic) {
      style = BOLD + ITALIC;
    }
    else if (isBold) {
      style = BOLD;
    }
    else if (isItalic) {
      style = ITALIC;
    }

    BaseColor color = null;

    if (isLink) {
      style = style + LINK;
    }

    if (isUnderline) {
      style = style + UNDERLINE;
    }
    if (isStrikethrough) {
      style = style + STRIKETHROUGH;
    }

    if (type == TAG_BODY
        && java.util.Locale.getDefault().getLanguage().equals(
            java.util.Locale.JAPANESE.getLanguage())) {
      return FontFactory.getFont(
          "HeiseiMin-W3",
          "UniJIS-UCS2-HW-H",
          fontSize,
          style,
          color);
    }
    else {
      return Fonts.getFont(face, style, fontSize);
    }
  }

  /**
   * Determines if the content text of this HTML tag is italic.
   *
   * @return True if it is italic, false if it's not.
   */
  public boolean isItalic()
  {
    return isItalic;
  }

  /**
   * Determines if the content text of this HTML tag is bold.
   *
   * @return True if it is bold, false if it's not.
   */
  public boolean isBold()
  {
    return isBold;
  }

  /**
   * Determines if the content text of this HTML tag is preformatted.
   *
   * @return True if it is preformatted, false if it's not.
   */
  public boolean isPre()
  {
    return isPre;
  }

  /**
   * Determines if the content text of this HTML tag is a link.
   *
   * @return True if it is a link, false if it's not.
   */
  public boolean isLink()
  {
    return isLink;
  }

  /**
   * Determines if the content text of this HTML tag is underlined.
   *
   * @return True if it is underlined, false if it's not.
   */
  public boolean isUnderline()
  {
    return isUnderline;
  }

  /**
   * Determines if the content text of this HTML tag is strikethrough.
   *
   * @return True if it is strikethrough, false if it's not.
   */
  public boolean isStrikethrough()
  {
    return isStrikethrough;
  }

  /**
   * Determines if the content text of this HTML tag is code.
   *
   * @return True if it is code, false if it's not.
   */
  public boolean isCode()
  {
    return isCode;
  }

  /**
   * Determines if the content text of this HTML tag is aligned centered.
   *
   * @return True if it is aligned centered, false if it's not.
   */
  public boolean isCentered()
  {
    return isCentered;
  }

  /**
   * Determines if the content text of this HTML tag is aligned right.
   *
   * @return True if it is aligned right, false if it's not.
   */
  public boolean isRight()
  {
    return isRight;
  }

  /**
   * Returns an attribute of this HTML tag. Attributes are optional values, like
   * "border=.." for the "<table>" tag.
   *
   * @param key The name of the attribute.
   * @return The value of the attribute or null if no such attribute was found.
   */
  protected String getAttribute(String key)
  {
    if (attributes.get(key) == null) {
      return null;
    }

    return (String) attributes.get(key);
  }

  /**
   * Returns the tags contained within this HTML tag as an ArrayList.
   *
   * @return The contained tags.
   */
  public ArrayList getContentTags()
  {
    return contentTags;
  }

  /**
   * Returns the type of this HTML tag.
   *
   * @return The HTML tag type.
   */
  public int getType()
  {
    return type;
  }

  /**
   * Sets an attribute for this HTML tag object.
   *
   * @param key The name of the attribute.
   * @param value The value of the attribute.
   */
  protected void setAttribute(String key, String value)
  {

    if (value == null) {
      // Some attributes might not have a value, so 
      // create an empty value to avoid NullPointerException
      value = "";
    }
    attributes.put(key, value);

    // set common attributes like alignmenent just after reading attributes
    String align = getAttribute("align");

    if (align != null) {
      if (align.equalsIgnoreCase("center")) {
        isCentered = true;
      }

      if (align.equalsIgnoreCase("right")) {
        isRight = true;
      }
    }
  }
}

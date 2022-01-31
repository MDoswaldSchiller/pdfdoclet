/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import com.itextpdf.text.Element;
import com.itextpdf.text.List;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.State;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * Wrapper for the 3rd-party HTML parser. The purpose of this class is to
 * isolate all references to the HTML parser code in one place. This way it will
 * be easier to change to a different parser if necessary.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class HtmlParserWrapper implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(HtmlParserWrapper.class);

  private static Tidy parser;

  /**
   * Parses an explanation text which may have HTML tags embedded, and create
   * appropriate Pdf objects (this is somewhat similar to the rendering process
   * of a webbrowser).
   *
   * @param text The explanation text (of a package, class or member).
   * @return An array of Phrase objects.
   */
  public static Element[] createPdfObjects(String text)
  {

    LOG.debug(">");

    Element[] result = new Element[0];

    try {
      initializeParser();

      String content = preProcessHtmlContent(text);

      // TODO: Make HTML encoding configurable
//			InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
      InputStream in = new ByteArrayInputStream(content.getBytes());

      // Parse the HTML document and return the root node
      LOG.debug("Parse DOM...");
      Node node = parser.parseDOM(in, null);

      // Now build HTML object tree
      HTMLTag bodyTag = TagFactory.createTag(null, HTMLTag.TAG_BODY);
      HtmlParserWrapper.processTree(bodyTag, node);

      // Now let every HTML tag object convert itself
      // into one or more PDF objects
      Element[] elements = bodyTag.toPdfObjects();

      // Now compress everything into a list of paragraphs
      ArrayList arrayList = new ArrayList(result.length);
      Paragraph currentParagraph = new Paragraph((float) 11.0);
      arrayList.add(currentParagraph);

      for (int i = 0; i < elements.length; i++) {
        if (elements[i] instanceof Paragraph) {
          currentParagraph = (Paragraph) elements[i];
          arrayList.add(elements[i]);

        }
        else if (elements[i] instanceof PdfPTable) {
          arrayList.add(elements[i]);
          currentParagraph = new Paragraph((float) 11.0);
          arrayList.add(currentParagraph);

        }
        else if (elements[i] instanceof List) {
          arrayList.add(elements[i]);
          currentParagraph = new Paragraph((float) 11.0);
          arrayList.add(currentParagraph);
        }
        else {
          currentParagraph.add(elements[i]);
        }
      }

      Object[] obj = arrayList.toArray();
      result = new Element[obj.length];

      for (int i = 0; i < obj.length; i++) {
        result[i] = (Element) obj[i];
      }

    }
    catch (Exception e) {

      // If HTML parsing fails, print warning and 
      // return a null result.
      e.printStackTrace();
      LOG.error("**");
      LOG.error("** WARNING: HTML parsing failed with exception: " + e);
      LOG.error("** - package: " + State.getCurrentPackage());
      LOG.error("**   class  : " + State.getCurrentClass());
      LOG.error("**   member : " + State.getCurrentMember());
      LOG.error("**");
    }

    LOG.debug("<");

    return result;
  }

  /**
   * Prints the specified node, recursively.
   *
   * @param node The node to start with.
   */
  public static void processTree(HTMLTag htmlTag, Node node)
  {

    if (node == null) {
      return;
    }

    int nodeType = node.getNodeType();

    switch (nodeType) {
      case Node.DOCUMENT_NODE:

        // Parse node
        processTree(htmlTag, ((Document) node).getDocumentElement());

        break;

      case Node.ELEMENT_NODE:

        NamedNodeMap attrs = node.getAttributes();

        if (attrs.getLength() > 0) {
          String attributes = "";

          for (int i = 0; i < attrs.getLength(); i++) {
            String attrName = attrs.item(i).getNodeName();
            String attrValue = attrs.item(i).getNodeValue();
            attributes = attributes + attrName;
            attributes = attributes + "=";
            attributes = attributes + attrValue;
            attributes = attributes + " ";
            htmlTag.setAttribute(attrName, attrValue);
          }
        }

        NodeList children = node.getChildNodes();

        if (children != null) {
          int len = children.getLength();

          for (int i = 0; i < len; i++) {
            String nestedTagName = children.item(i).getNodeName();
            int tagType = HTMLTagUtil.getTagType(nestedTagName);

            if (tagType != HTMLTag.TAG_UNSUPPORTED) {
              HTMLTag nestedTag = TagFactory.createTag(htmlTag, tagType);
              processTree(nestedTag, children.item(i));
              htmlTag.getContentTags().add(nestedTag);
            }
            else {
              // Probably content child (actual text)
              HtmlParserWrapper.processTree(htmlTag, children.item(i));
            }
          }
        }

        break;

      case Node.TEXT_NODE:
        htmlTag.getContentTags().add(node.getNodeValue());

        break;
    }

    if (htmlTag.getType() == Node.ELEMENT_NODE) {
      // end-tag
    }
  }

  /**
   * Prepares the HTML content for parsing.
   *
   * @param text The HTML content.
   * @param doc The optional javadoc Doc object.
   * @return The preprocessed result.
   */
  private static String preProcessHtmlContent(String text) throws Exception
  {

    LOG.debug(">");

    InputStream in = new ByteArrayInputStream(text.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    parser.parse(in, out);
    String content = out.toString();

    // Cut off any <html> tag
    if (Util.indexOfIgnoreCase(content, "<html>") == 0) {
      content = content.substring(6, content.length());
    }
    if (Util.indexOfIgnoreCase(content, "</html>") == (content.length() - 7)) {
      content = content.substring(0, content.length() - 7);
    }

    // Insert NEWPAGE tags for any NEWPAGE comments
    while (content.indexOf("<!-- NEWPAGE -->") != -1) {
      int pos = content.indexOf("<!-- NEWPAGE -->");
      String leftText = content.substring(0, pos);
      String rightText = content.substring(pos + 16);
      content = leftText + "<a href=\"newpage\" />" + rightText;
    }
    LOG.debug("<");

    return content;
  }

  /**
   * Initializes the HTML parser implementation.
   *
   * @throws Exception If the initialization failed.
   */
  private static void initializeParser() throws Exception
  {

    LOG.debug(">");

    parser = new Tidy();
    parser.setXmlTags(false);
    // TODO: Apparently, as soon as a specific encoding is set,
    // there are errors with special characters depending on
    // which platform the pdfdoclet runs. Must look into this
    // further. No specifying any encoding seems to work fine
    // on linux and windows.
//        parser.setCharEncoding(Configuration.LATIN1);
//	      parser.setCharEncoding(Configuration.UTF8);
    parser.setQuiet(true);
    parser.setShowWarnings(false);
    parser.setDropEmptyParas(false);
    parser.setMakeClean(false);  // ***  "true" changes CENTER to DIV  ***
    parser.setTrimEmptyElements(false);
    parser.setFixComments(false);
    parser.setForceOutput(true);
    parser.setHideEndTags(false);

    LOG.debug("<");
  }
}

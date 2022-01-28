/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Handler for PDF document page events. This class creates headers and footers
 * and the navigation frame (outline).
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class PageEventHandler
    extends PdfPageEventHelper
    implements IConstants
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(PageEventHandler.class);

  /**
   * Flag which ensures that the link target for the Overview page is created
   * only once.
   */
  private static boolean overview_printed = false;

  private static String leftHeader = "";
  private static String centerHeader = "";
  private static String rightHeader = "";

  private BaseFont bf = null;
  private PdfContentByte cb = null;
  private int currentPage = 0;
  private int PAGE_NUMBER_SIMPLE = 1;
  private int PAGE_NUMBER_FULL = 2;
  private int pageNumberType = PAGE_NUMBER_FULL;
  private int PAGE_NUMBER_ALIGN_LEFT = 1;
  private int PAGE_NUMBER_ALIGN_CENTER = 2;
  private int PAGE_NUMBER_ALIGN_RIGHT = 3;
  private int PAGE_NUMBER_ALIGN_SWITCH = 4;
  private int pageNumberAlign = PAGE_NUMBER_ALIGN_CENTER;

  // we will put the final number of pages in a template
  private PdfTemplate template;

  /**
   * Constructs an event handler for a given PDF writer.
   *
   * @param pdfWriter The writer used to create the document.
   * @throws Exception
   */
  public PageEventHandler(PdfWriter pdfWriter) throws Exception
  {
    log.debug(">");
    cb = pdfWriter.getDirectContent();
    bf
        = BaseFont.createFont(
            BaseFont.TIMES_ROMAN,
            BaseFont.CP1252,
            BaseFont.NOT_EMBEDDED);
    template
        = cb.createTemplate(bf.getWidthPoint("Page 999 of 999", 8), 250);

    leftHeader
        = Configuration.getProperty(ARG_HEADER_LEFT, "");
    centerHeader
        = Configuration.getProperty(ARG_HEADER_CENTER, "");
    rightHeader
        = Configuration.getProperty(ARG_HEADER_RIGHT, "");

    String pageNumberTypeValue
        = Configuration.getProperty(
            ARG_PGN_TYPE,
            ARG_VAL_FULL);

    if (pageNumberTypeValue.equalsIgnoreCase(ARG_VAL_FULL)) {
      pageNumberType = PAGE_NUMBER_FULL;
    }
    else {
      pageNumberType = PAGE_NUMBER_SIMPLE;
    }

    String pageNumberAlignValue
        = Configuration.getProperty(
            ARG_PGN_ALIGNMENT,
            ARG_VAL_SWITCH);

    if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_LEFT)) {
      pageNumberAlign = PAGE_NUMBER_ALIGN_LEFT;
    }

    if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_CENTER)) {
      pageNumberAlign = PAGE_NUMBER_ALIGN_CENTER;
    }

    if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_RIGHT)) {
      pageNumberAlign = PAGE_NUMBER_ALIGN_RIGHT;
    }

    if (pageNumberAlignValue.equalsIgnoreCase(ARG_VAL_SWITCH)) {
      pageNumberAlign = PAGE_NUMBER_ALIGN_SWITCH;
    }
    log.debug("<");
  }

  /**
   * At the end of each page, index information is collected and footer and
   * headers are inserted.
   *
   * @param document The current PDF document.
   * @param writer The writer used to create the document.
   */
  public void onEndPage(PdfWriter writer, Document document)
  {
    log.debug(">");
    currentPage = document.getPageNumber();
    State.setCurrentPage(currentPage);

    if (State.getCurrentHeaderType() != HEADER_DEFAULT) {
      float len;

      if (State.isContinued() && !State.isLastMethod()) {
        String cont = "(continued on next page)";
        len = bf.getWidthPoint(cont, 7);
        cb.beginText();
        cb.setFontAndSize(bf, 7);
        cb.setTextMatrix(300 - (len / 2), 56);
        cb.showText(cont);
        cb.endText();
      }
      else {

        // create bookmark entry for current method
//                Bookmarks.createMethodBookmark(writer);
      }

      // add lines				
      cb.setLineWidth(1f);
      cb.moveTo(LEFT_MARGIN, 812);
      cb.lineTo(RIGHT_MARGIN, 812);

      cb.moveTo(LEFT_MARGIN, 42);
      cb.lineTo(RIGHT_MARGIN, 42);
      cb.stroke();

      // page footer with number of pages
      float textX = (float) 0.0;
      float templateX = (float) 0.0;
      float textWidth = (float) 0.0;
      float numWidth = (float) 0.0;
      int currPageNumberAlign;

      if (pageNumberAlign == PAGE_NUMBER_ALIGN_SWITCH) {
        if ((currentPage % 2) == 0) {
          currPageNumberAlign = PAGE_NUMBER_ALIGN_LEFT;
        }
        else {
          currPageNumberAlign = PAGE_NUMBER_ALIGN_RIGHT;
        }
      }
      else {
        currPageNumberAlign = pageNumberAlign;
      }

      String text = Configuration.getProperty(ARG_PGN_PREFIX, "Page ")
                    + currentPage;

      if (pageNumberType == PAGE_NUMBER_FULL) {
        text = text + " of ";
      }

      textWidth = bf.getWidthPoint(text, 8);
      numWidth = bf.getWidthPoint("999", 8);

      if (currPageNumberAlign == PAGE_NUMBER_ALIGN_LEFT) {
        textX = LEFT_MARGIN;
      }

      if (currPageNumberAlign == PAGE_NUMBER_ALIGN_CENTER) {
        textX = (float) (DOCUMENT_WIDTH / 2) - (textWidth / 2);
      }

      if (currPageNumberAlign == PAGE_NUMBER_ALIGN_RIGHT) {
        textX = RIGHT_MARGIN - textWidth - numWidth;
      }

      templateX = textX + textWidth;

      cb.beginText();
      cb.setFontAndSize(bf, 8);

      if (currPageNumberAlign == PAGE_NUMBER_ALIGN_LEFT) {
        cb.showTextAligned(
            PdfContentByte.ALIGN_LEFT,
            text,
            textX,
            FOOTER_BASELINE,
            0);
      }

      if (currPageNumberAlign == PAGE_NUMBER_ALIGN_CENTER) {
        cb.showTextAligned(
            PdfContentByte.ALIGN_LEFT,
            text,
            textX,
            FOOTER_BASELINE,
            0);
      }

      if (currPageNumberAlign == PAGE_NUMBER_ALIGN_RIGHT) {
        cb.showTextAligned(
            PdfContentByte.ALIGN_LEFT,
            text,
            textX,
            FOOTER_BASELINE,
            0);
      }

      cb.endText();

      if (pageNumberType == PAGE_NUMBER_FULL) {
        // add template for total page number
        cb.addTemplate(template, templateX, FOOTER_BASELINE);
      }

      // headers (left, right, center)
      // temporary solution: handling of first page of package
      // not correct yet, so use fix heading configuration now
      if (State.getCurrentHeaderType() == HEADER_API) {
        leftHeader = "";
        centerHeader = "$CLASS";
        rightHeader = "";
      }

      if (State.getCurrentHeaderType() == HEADER_INDEX) {
        leftHeader = "";
        centerHeader = "Index";
        rightHeader = "";
      }

      cb.beginText();
      cb.setFontAndSize(bf, 8);
      cb.showTextAligned(
          PdfContentByte.ALIGN_CENTER,
          parseHeader(centerHeader),
          DOCUMENT_WIDTH / 2,
          HEADER_BASELINE,
          0);
      cb.endText();

      cb.beginText();
      cb.setFontAndSize(bf, 8);
      cb.showTextAligned(
          PdfContentByte.ALIGN_LEFT,
          parseHeader(leftHeader),
          LEFT_MARGIN,
          HEADER_BASELINE,
          0);
      cb.endText();

      cb.beginText();
      cb.setFontAndSize(bf, 8);
      cb.showTextAligned(
          PdfContentByte.ALIGN_RIGHT,
          parseHeader(rightHeader),
          RIGHT_MARGIN,
          HEADER_BASELINE,
          0);
      cb.endText();
    }
    log.debug("<");
  }

  /**
   *
   * @param text
   * @return
   */
  private String parseHeader(String text)
  {
    log.debug(">");
    if (text.equalsIgnoreCase("$SHORTCLASS")) {
      String name = State.getCurrentClass();

      if (name.indexOf(".") != -1) {
        name = name.substring(name.lastIndexOf(".") + 1, name.length());
      }

      return name;
    }

    if (text.equalsIgnoreCase("$CLASS")) {
      return State.getCurrentClass();
    }

    if (text.equalsIgnoreCase("$PACKAGE")) {
      return State.getCurrentPackage();
    }

    log.debug("<");
    return text;
  }

  /**
   * This method is called every time a new paragraph begins. It is used to
   * create the frame (or outline) with all classes in the PDF document.
   */
  public void onParagraph(
      PdfWriter writer,
      Document document,
      float position)
  {

    log.debug(">");

    // Create the navigation frame (outline)
    if (Configuration.isCreateFrameActive()) {

      // Add links to classes etc.
//            Bookmarks.createClassesBookmark(writer);
      // Creates bookmarks entry for packages / index
//            Bookmarks.createPackageBookmark(writer);
    }
    log.debug("<");
  }

  /**
   * Creates bookmarks (navigation tree) entry for method
   */
  public void onParagraphEnd(
      PdfWriter writer,
      Document document,
      float position)
  {
    log.debug(">");
    log.debug("<");
  }

  /**
   * This method is called when a new page is started. It is used to print the
   * "(continued from last page)" header if necessary.
   */
  public void onStartPage(PdfWriter writer, Document document)
  {
    log.debug(">");
    if (State.isContinued()) {
      try {
        if (State.getCurrentHeaderType() != HEADER_DEFAULT) {
          float len;
          String cont = "(continued from last page)";
          len = bf.getWidthPoint(cont, 7);
          cb.beginText();
          cb.setFontAndSize(bf, 7);
          cb.setTextMatrix(300 - (len / 2), 796);
          cb.showText(cont);
          cb.endText();
        }
      }
      catch (Exception e) {
      }
    }
    log.debug("<");
  }

  // we override the onCloseDocument method
  public void onCloseDocument(PdfWriter writer, Document document)
  {
    log.debug(">");
    if (pageNumberType == PAGE_NUMBER_FULL) {
      template.beginText();
      template.setFontAndSize(bf, 8);
      template.showText(String.valueOf(writer.getPageNumber() - 1));
      template.endText();
    }
    log.debug("<");
  }
}

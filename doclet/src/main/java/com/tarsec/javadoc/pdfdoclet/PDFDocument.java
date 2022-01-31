/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Graphic;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates a PDF document. Handles initialization of and access to the
 * document instance.
 *
 * @version $Revision: 1.2 $
 * @author Marcel Schoen
 */
public class PDFDocument implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(PDFDocument.class);

  /**
   * Document instance.
   */
  private static Document pdfDocument = null;

  /**
   * Stores a reference to the PdfWriter instance.
   */
  private static PdfWriter pdfWriter = null;

  public static void setInstance(Document pdfDocument)
  {
    PDFDocument.pdfDocument = pdfDocument;
  }
  
  /**
   * Creates the document instance and initializes it.
   */
  public static PdfWriter initialize() throws Exception
  {

    // step 1: creation of a document-object
    pdfDocument = new Document();
    pdfDocument.setPageSize(Configuration.getPageSize());

    // set left-, right-, top- and bottom-margins
    pdfDocument.setMargins(LEFT_MARGIN_WIDTH, RIGHT_MARGIN_WIDTH,
                           TOP_MARGIN_WIDTH, BOTTOM_MARGIN_WIDTH);

    // step 2:
    // we create a writer that listens to the document
    // and directs a PDF-stream to a file
    pdfWriter = PdfWriter.getInstance(pdfDocument,
                                      new FileOutputStream(PDFDoclet.getPdfFile()));

    if (Configuration.getProperty(ARG_ENCRYPTED, ARG_VAL_NO).equalsIgnoreCase(ARG_VAL_YES)) {
      if (Configuration.getProperty(ARG_ALLOW_PRINTING, ARG_VAL_NO)
          .equalsIgnoreCase(ARG_VAL_YES)) {
        pdfWriter.setEncryption(PdfWriter.STRENGTH40BITS, null, null,
                                PdfWriter.AllowPrinting);
      }
      else {
        pdfWriter.setEncryption(PdfWriter.STRENGTH40BITS, null, null, 0);
      }
    }

    pdfDocument.open();
    pdfWriter.setPageEvent(new PageEventHandler(pdfWriter));

    return pdfWriter;
  }

  /**
   * Returns a reference to the PdfWriter instance.
   *
   * @return The PdfWriter object.
   */
  public static PdfWriter getWriter()
  {
    return pdfWriter;
  }

  /**
   * Returns a reference to the PDF document object.
   *
   * @return The PDF document object.
   */
  public static Document instance()
  {
    return pdfDocument;
  }

  /**
   * Conveniency method
   */
  public static void add(PdfPTable table) throws DocumentException
  {
    pdfDocument.add(table);
  }

  /**
   * Conveniency method
   */
  public static void add(Paragraph label) throws DocumentException
  {
    pdfDocument.add(label);
  }

  /**
   * Conveniency method
   */
  public static void add(Graphic graphic) throws DocumentException
  {
    pdfDocument.add(graphic);
  }

  /**
   * Conveniency method
   */
  public static void newPage() throws DocumentException
  {
    pdfDocument.newPage();
  }

  /**
   * Conveniency method
   */
  public static void close()
  {
    pdfDocument.close();
  }
}

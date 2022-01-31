/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints (optionally) a title page for the API documentation based on the
 * configuration properties.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TitlePage implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(TitlePage.class);

  private static boolean titlePrinted = false;

  private Document pdfDocument = null;

  /**
   * Constructs a TitlePage object.
   *
   * @param pdfDocument The document into which the title page will be inserted.
   */
  public TitlePage(Document pdfDocument)
  {
    this.pdfDocument = pdfDocument;
  }

  /**
   * Prints the title page.
   *
   * @throws Exception
   */
  public void print() throws Exception
  {
    String apiTitlePageProp = Configuration.getProperty(ARG_API_TITLE_PAGE, ARG_VAL_NO)
        .toLowerCase();

    if (apiTitlePageProp.equalsIgnoreCase(ARG_VAL_YES)) {
      String apiFileProp = Configuration.getConfiguration().getProperty(ARG_API_TITLE_FILE,
                                                                        "");

      // If the (pdf) filename contains page information, remove it,
      // because for the title page only 1 page can be imported
      if (apiFileProp.indexOf(",") != -1) {
        apiFileProp = apiFileProp.substring(0, apiFileProp.indexOf(","));
      }

      String label = Configuration.getProperty(ARG_LB_OUTLINE_TITLE, LB_TITLE);
      String titleDest = "TITLEPAGE:";

      if (apiFileProp.length() > 0) {
        File apiFile = new File(Configuration.getWorkDir(), apiFileProp);

        if (apiFile.exists() && apiFile.isFile()) {
          Destinations.addValidDestinationFile(apiFile);
          State.setCurrentFile(apiFile);

          pdfDocument.newPage();
          pdfDocument.add(PDFUtil.createAnchor(titleDest));
          Bookmarks.addRootBookmark(label, titleDest);

          if (apiFile.getName().toLowerCase().endsWith(".pdf")) {

            LOG.debug("Add PDF page as title page.");
            // Always use 1st page of document as title page
            PDFUtil.insertPdfPages(apiFile, "1");

          }
          else {

            LOG.debug("Use HTML file as title page.");
            String html = Util.getHTMLBodyContent(apiFile);
            Element[] objs = HtmlParserWrapper.createPdfObjects(html);
            PDFUtil.printPdfElements(objs);

          }

        }
        else {
          LOG.error(
              "** WARNING: Title page file not found or invalid: "
              + apiFileProp);
        }
      }
      else {
        String apiTitleProp = Configuration.getConfiguration().getProperty(ARG_API_TITLE,
                                                                           "");
        String apiCopyrightProp = Configuration.getConfiguration()
            .getProperty(ARG_API_COPYRIGHT,
                         "");
        String apiAuthorProp = Configuration.getConfiguration().getProperty(ARG_API_AUTHOR,
                                                                            "");
        pdfDocument.newPage();
        pdfDocument.add(PDFUtil.createAnchor(titleDest));

        Paragraph p1 = new Paragraph((float) 100.0,
                                     new Chunk(apiTitleProp, Fonts.getFont(TIMES_ROMAN, BOLD, 42)));
        Paragraph p2 = new Paragraph((float) 140.0,
                                     new Chunk(apiAuthorProp, Fonts.getFont(TIMES_ROMAN, BOLD, 18)));
        Paragraph p3 = new Paragraph((float) 20.0,
                                     new Chunk(apiCopyrightProp, Fonts.getFont(TIMES_ROMAN, 12)));
        p1.setAlignment(Element.ALIGN_CENTER);
        p2.setAlignment(Element.ALIGN_CENTER);
        p3.setAlignment(Element.ALIGN_CENTER);
        pdfDocument.add(p1);
        pdfDocument.add(p2);
        pdfDocument.add(p3);
      }
    }
  }
}

/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.io.File;

import org.apache.log4j.Logger;

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.sun.javadoc.RootDoc;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;

/**
 * Prints the overview.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Overview implements IConstants {

    /** Logger reference */
    private static Logger log = Logger.getLogger(Overview.class);

	/**
	 * Processes overview.
	 *
	 * @param rootDoc The javadoc information for the API.
	 * @throws Exception
	 */
	public static void print(RootDoc rootDoc) throws Exception {

		String overview = JavadocUtil.getComment(rootDoc);
		log.debug("Overview text: " + overview);

        // Check if PDF file has been specified
        String[] overviewFileNames = 
            Configuration.findNumberedProperties(ARG_OVERVIEW_PDF_FILE);

        // Only do something if either standard overview or PDF file has been specified
		if ((overview == null) || (overview.length() == 0) && overviewFileNames == null) {
			return;
		}
        
		State.setOverview(true);
		State.setCurrentDoc(rootDoc);
		
		PDFDocument.newPage();

		String bmLabel = Configuration.getProperty(ARG_LB_OUTLINE_OVERVIEW, LB_OVERVIEW);
        String dest = Destinations.createAnchorDestination(State.getCurrentFile(), bmLabel);
		Bookmarks.addRootBookmark(bmLabel, dest);
        PDFDocument.instance().add(PDFUtil.createAnchor(dest));
        if (State.getCurrentFile() != null) {
            String packageAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
            PDFDocument.instance().add(PDFUtil.createAnchor(packageAnchor));
        }

        // If the (pdf) filename contains page information, extract it
        boolean pdfPagesInserted = false;
        
        if(overviewFileNames != null) {
            for(int i = 0; i < overviewFileNames.length; i++) {
                String overviewFileName = overviewFileNames[i];
                String pages = "";
                if(overviewFileName.indexOf(",") != -1) {
                    pages = overviewFileName.substring(overviewFileName.indexOf(",") + 1, 
                            overviewFileName.length());
                    overviewFileName = overviewFileName.substring(0, overviewFileName.indexOf(","));
                }
                log.debug("MARK Overview Filename: " + overviewFileName);
                
                if(overviewFileName.endsWith(".pdf")) {
                    if(pages.length() == 0) {
                        pages = "1";
                    }
                    log.debug("Add PDF pages from " + overviewFileName + " to the overview: " + pages);
                    File overviewFile = new File(Configuration.getWorkDir(), overviewFileName);
                    if (overviewFile.exists() && overviewFile.isFile()) {
                        State.setContinued(false);
                        Destinations.addValidDestinationFile(overviewFile);
                        State.setCurrentFile(overviewFile);
                        PDFUtil.insertPdfPages(overviewFile, pages);
                        pdfPagesInserted = true;
                    }
                } 
                if(overview != null && overview.length() > 0) {
                    
                    log.debug("Create regular overview from doc root.");

                    if(pdfPagesInserted) {
                        PDFDocument.newPage();
                    }
                }
            }
        }

        if(overview != null && overview.length() > 0) {
            
            log.debug("Create regular overview from doc root.");
/*
            if(pdfPagesInserted) {
                PDFDocument.newPage();
            }
            */
            State.setContinued(true);
            
            Paragraph label = new Paragraph((float) 22.0, "",
                    Fonts.getFont(TIMES_ROMAN, BOLD, 18));
            label.add(bmLabel);
            PDFDocument.instance().add(label);
    
            // Some empty space
            PDFDocument.instance().add(new Paragraph((float) 20.0, " "));
            
            Util.debug("> overview");
    
            Element[] objs = HtmlParserWrapper.createPdfObjects(overview);
    
            if ((objs == null) || (objs.length == 0)) {
                String rootDesc = Util.stripLineFeeds(overview);
                PDFDocument.instance().add(new Paragraph((float) 11.0, rootDesc,
                        Fonts.getFont(TIMES_ROMAN, 10)));
            } else {
                PDFUtil.printPdfElements(objs);
            }
        }
        
		State.setOverview(false);
		State.setContinued(false);
	}

}

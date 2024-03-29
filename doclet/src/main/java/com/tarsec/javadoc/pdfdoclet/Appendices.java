
package com.tarsec.javadoc.pdfdoclet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;

/**
 * Prints extra appendices
 */
public class Appendices implements IConstants
{
    /** Logger reference */
    private static Logger log = Logger.getLogger(Appendices.class);

    private static ArrayList appendices = new ArrayList();
        
    /**
     * Initializes the appendix creation.
     */
    public static void initialize() {
        
        int prefixLen = ARG_APPENDIX_PREFIX.length();
        int suffixLen = ARG_APPENDIX_FILE_SUFFIX.length();
        Properties props = Configuration.getConfiguration();
        Enumeration names = props.keys();

        while(names.hasMoreElements()) {
            String key = (String)names.nextElement();

            if (key.startsWith(ARG_APPENDIX_PREFIX) &&
                key.endsWith(ARG_APPENDIX_FILE_SUFFIX)) {
                
                File file = null;
                String fileName = props.getProperty(key);
                // If the (pdf) filename contains page information, extract it
                String pages = "";
                if(fileName.indexOf(",") != -1) {
                    pages = fileName.substring(fileName.indexOf(",") + 1, fileName.length());
                    fileName = fileName.substring(0, fileName.indexOf(","));
                }
                
                String appendixNum = key.substring(prefixLen, key.length() - suffixLen);
                file = new File(fileName);
                if (!file.exists()) {
                    file = new File(Configuration.getWorkDir(), fileName);
                }
                if (file.exists() && file.isFile() && file.canRead()) {
                    try {
                        int index = Integer.parseInt(appendixNum);
                        AppendixInfo info = new AppendixInfo(index, file, pages);
                        appendices.add(info);
                        Destinations.addValidDestinationFile(file);
                        log.debug("Adding appendix "+info.name+": "+file);
                    } catch (RuntimeException e) {
                        log.debug("Error processing appendix argument "+key);
                    }
                    
                } else {
                    log.debug("Could not find appendix file "+fileName);
                }
            }
        }
        
        Collections.sort(appendices);
    }
    
    /**
     * 
     * 
     * @throws Exception
     */
    public static void print() throws Exception {
        if (appendices.isEmpty()) {
            return;
        }

        String bmLabel = Configuration.getProperty(ARG_LB_OUTLINE_APPENDICES, LB_APPENDICES);
        BookmarkEntry bookmark = Bookmarks.addStaticRootBookmark(bmLabel);

        for (Iterator iter = appendices.iterator(); iter.hasNext();) {
            printAppendix((AppendixInfo)iter.next(), bookmark);
        }
    }

    /**
     * 
     * @param info
     * @param parentBookmark
     * @throws Exception
     */
    private static void printAppendix(AppendixInfo info, BookmarkEntry entry) 
        throws Exception {
        
        File file = info.file;
        State.setCurrentDoc(null);
        State.setCurrentPackage(null);
        State.setCurrentFile(file);
        State.increasePackageChapter();
        State.setCurrentHeaderType(HEADER_APPENDIX);
        State.setContinued(false);
        String label = Configuration.getProperty(ARG_LB_APPENDIX, LB_APPENDIX);
        String fullTitle = label + " " + info.name;
        if (info.title != null) {
            fullTitle += ": " + info.title;
        }

        log.debug("Printing appendix "+info.name);
        PDFDocument.newPage();
        State.setContinued(true);
        State.setCurrentClass(fullTitle);
        String appendixAnchor = Destinations.createAnchorDestination(file, null);
        Bookmarks.addSubBookmark(entry, fullTitle, appendixAnchor);
        
        if(file.getName().toLowerCase().endsWith(".pdf")) {

            log.debug("Add PDF document as appendix.");
            Chunk anchorChunk = PDFUtil.createAnchor(appendixAnchor);
            PDFDocument.instance().add(anchorChunk);
            PDFUtil.insertPdfPages(file, info.pages);
            
        } else {
            
            log.debug("Add HTML file as appendix.");
            String html = Util.getHTMLBodyContent(file);
            Chunk titleChunk = new Chunk(fullTitle, Fonts.getFont(TIMES_ROMAN, BOLD, 22));
            titleChunk.setLocalDestination(appendixAnchor);
            Paragraph titleParagraph = new Paragraph((float) 22.0, titleChunk);
            PDFDocument.add(titleParagraph);

            Element[] objs = HtmlParserWrapper.createPdfObjects(html);
            PDFUtil.printPdfElements(objs);
            
        }
        
        State.setContinued(false);
        State.setCurrentFile(null);
    }

    /**
     * 
     */
    private static class AppendixInfo implements Comparable {
        int index;
        String name;
        String title;
        File file;
        String pages;

        /**
         * 
         * @param index
         * @param file
         */
        private AppendixInfo(int index, File file, String pages) {
            Properties props = Configuration.getConfiguration();
            this.index = index;
            this.file = file;
            this.pages = pages;
            this.name = props.getProperty(ARG_APPENDIX_PREFIX+index+ARG_APPENDIX_NAME_SUFFIX);
            this.title = props.getProperty(ARG_APPENDIX_PREFIX+index+ARG_APPENDIX_TITLE_SUFFIX);
            
            if (this.name == null) {
                this.name = String.valueOf((char)('A' + ((index-1) % 26)));
            }
        }

        /**
         * 
         */
        public int compareTo(Object other) {
            return index - ((AppendixInfo)other).index;
        }
    }
}

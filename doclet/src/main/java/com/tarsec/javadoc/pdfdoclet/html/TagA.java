/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.tarsec.javadoc.pdfdoclet.Bookmarks;
import com.tarsec.javadoc.pdfdoclet.Configuration;
import com.tarsec.javadoc.pdfdoclet.Destinations;
import com.tarsec.javadoc.pdfdoclet.State;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;

/**
 * Implements external links (http://..)
 * 
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagA extends HTMLTag {

    /** Logger reference */
    private static Logger log = Logger.getLogger(TagA.class);

    /**
     * Create a link tag instance.
     * 
     * @param parent The parent HTML tag.
     * @param type The type of this tag.
     */
    public TagA(HTMLTag parent, int type) {
        super(parent, type);
        isLink = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
     */
    public Element[] openTagElements() {

        // Support the "hack" for triggering page-breaks in
        // a document by inserting html comments <!-- NEWPAGE -->
        String addr = getAttribute("href");
        if (addr != null && addr.equalsIgnoreCase("newpage")) {
            Chunk newpage = new Chunk("");
            newpage.setNewPage();
            return new Element[] { newpage };
        }

        // Support the "hack" for triggering bookmark entries in
        // a document by inserting <a name="pdfbookmark"> tags
        String name = getAttribute("name");
        if (name != null && name.equalsIgnoreCase("pdfbookmark")) {
            String label = getAttribute("label");
            String dest = Destinations.createAnchorDestination(State
                    .getCurrentFile(), label);
            Chunk anchor = PDFUtil.createAnchor(dest);
            Bookmarks.addRootBookmark(label, dest);
            return new Element[] { anchor };
        }

        if (name != null && name.length() > 0) {
            String dest = Destinations.createAnchorDestination(State
                    .getCurrentFile(), name);
            isLink = false;
            Phrase phr = new Phrase(PDFUtil.createAnchor(dest, getFont()));
            return new Element[] { phr };
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#toElement(java.lang.String)
     */
    public Element toElement(String text) {
        String addr = getAttribute("href");
        Element result = null;

        if (addr == null || Configuration.isLinksCreationActive() == false) {
            addr = "";
        }

        if (!isPre()) { 
            text = Util.stripLineFeeds(text);
        }

        if (addr.startsWith("locallink")) {
            boolean plainText = addr.startsWith("locallinkplain");
            String dest = addr.substring(addr.indexOf(':') + 1).trim();
            isCode = !plainText; // so getFont() adjusts the font correctly
            return new LinkPhrase(dest, text, Math.max(9, (int) getFont()
                    .size()), plainText);
        } else if (addr.equalsIgnoreCase("newpage")) {
            return super.toElement(text);
        } else if (addr.startsWith("http://") || addr.startsWith("https://")) {
            try {
                URL url = new URL(addr);
                return new Chunk(text, getFont()).setAnchor(url);
            } catch (MalformedURLException e) {
                log.error("** Malformed URL: " + addr);
            }
        } else {
            String fileName = addr.trim();
            String anchorName = "";
            int hashIndex = addr.indexOf('#');

            if (hashIndex >= 0) {
                fileName = addr.substring(0, hashIndex).trim();
                anchorName = addr.substring(hashIndex + 1).trim();
            }

            boolean isLocalAnchor = (fileName.length() == 0 && anchorName
                    .length() > 0);
            File file = null;
            try {
                if (fileName.length() > 0) {
                    file = new File(Util.getFilePath(fileName));
                } else {
                    file = State.getCurrentFile();
                }
            } catch (FileNotFoundException e) {
                log.debug("Could not find linked file " + fileName);
            }

            if (isLocalAnchor || Destinations.isValidDestinationFile(file)) {
                String fullAnchor = Destinations.createAnchorDestination(file, anchorName);
                log.debug("Adding link to " + fullAnchor);
                PdfAction action = new PdfAction("", "");
                action.remove(PdfName.F);
                action.put(PdfName.S, PdfName.GOTO);
                action.put(PdfName.D, new PdfString(fullAnchor));
                Phrase phr = new Phrase();
                phr.add(new Chunk(text, getFont()).setAction(action));
                return phr;
            }
        }

        if (getAttribute("name") != null) {
            isLink = false; // no underline for anchors
        }
        
        Font font = getFont();
        font.setColor(0, 0, 0);
        result = (Element) new Chunk(text, font);
        isLink = false;

        return result;
    }
}

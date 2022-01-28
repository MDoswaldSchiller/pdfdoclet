/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;


/**
 * Creates and alphabetical index at the end of the
 * API document.
 * 
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Index implements IConstants, Comparator {

    /** Logger reference */
    private static Logger log = Logger.getLogger(Index.class);
    
    /**
     * Container for member index information
     */
    private static Hashtable memberIndex = new Hashtable();

	private PageNumberSorter pageNumberSorter = new PageNumberSorter();

    /**
     * Holds a list of all classes, methods, constructors and fields.
     */
    private static TreeMap memberList = new TreeMap();
    private Document pdfDocument = null;
    private PdfWriter pdfWriter = null;

    /**
     * Constructs an Index object for a given document.
     *
     * @param writer The PdfWriter used to create the document.
     * @param document The PDF document.
     */
    public Index(PdfWriter writer, Document document) {
        this.pdfWriter = writer;
        this.pdfDocument = document;
    }

    /**
    * Adds a member (field or method) to the internal
    * list used for the index creation.
    *
    * @param memberName The fully qualified name of the 
    *                   member of the current class.
    */
    public void addToMemberList(String memberName) {
        log.debug(">");
		// Separate name of unqualified member only
        String memberShortName = memberName.substring(memberName.lastIndexOf(
                    ".") + 1, memberName.length());
		addPageNoForMember(memberShortName);
		memberList.put(memberShortName, memberShortName);
        log.debug("<");
    }

	/**
	 * Creates a list for all page numbers of a given
	 * member if necessary. The current page number is
	 * then added to that list.
	 * 
	 * @param memberName The short, unqualified member name.
	 */	
	private void addPageNoForMember(String memberName) {
        log.debug(">");
		if(memberIndex.get(memberName) == null) {
			memberIndex.put(memberName, new TreeSet());
		}
		TreeSet list = (TreeSet) memberIndex.get(memberName);
		list.add(new Integer(State.getCurrentPage()));
        log.debug("<");
	}

	/**
	 * Returns an Iterator for iterating through a sorted
	 * list of all page numbers for a given member name.
	 * 
	 * @param memberName The short, unqualified member name.
	 * @return The iterator for the sorted page numbers (Integer objects).
	 */	
	private Iterator getSortedPageNumbers(String memberName) {
        log.debug(">");
		TreeSet list = (TreeSet) memberIndex.get(memberName);
		Collections.synchronizedSet(list); 
        log.debug("<");
		return list.iterator();	
	}
	
    /**
     * Creates a simple alphabetical index of all
     * classes and members of the API.
     *
     * @throws Exception If the Index could not be created.
     */
    public void create() throws Exception {
        log.debug(">");
    	
    	if(!Configuration.getBooleanConfigValue(ARG_CREATE_INDEX, false)) {
            log.debug("Index creation disabled.");
    		return;
    	}

        log.debug("** Start creating Index...");

        State.setCurrentHeaderType(HEADER_INDEX);
        State.increasePackageChapter();
        
        // Name of the package (large font)
        pdfDocument.newPage();

        // Create "Index" bookmark
        String label = Configuration.getProperty(ARG_LB_OUTLINE_INDEX, LB_INDEX);
        String dest = "INDEX:";
        Bookmarks.addRootBookmark(label, dest);
        Chunk indexChunk = new Chunk(label, Fonts.getFont(TIMES_ROMAN, BOLD, 30));
        indexChunk.setLocalDestination(dest);

        Paragraph indexParagraph = new Paragraph((float) 30.0, indexChunk);

        pdfDocument.add(indexParagraph);

        // we grab the ContentByte and do some stuff with it
        PdfContentByte cb = pdfWriter.getDirectContent();
        ColumnText ct = new ColumnText(cb);
        ct.setLeading((float) 9.0);

        float[] right = { 70, 320 };
        float[] left = { 300, 550 };

        // fill index columns with text
        String letter = "";
        Set keys = memberList.keySet();

        // keys must be sorted case unsensitive
        ArrayList sortedKeys = new ArrayList(keys.size());

		// Build sorted list of all entries
        Iterator keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            sortedKeys.add(keysIterator.next());
        }
        Collections.sort(sortedKeys, this);

        Iterator realNames = sortedKeys.iterator();

        while (realNames.hasNext()) {
            String memberName = (String) realNames.next();
            String currentLetter = memberName.substring(0, 1).toUpperCase();
            log.debug("Create index entry for " + memberName);

			// Check if next letter in alphabet is reached
            if (currentLetter.equalsIgnoreCase(letter) == false) {
            	// If yes, switch to new letter and print it
                letter = currentLetter.toUpperCase();
                Paragraph lphrase = new Paragraph((float) 13.0);
                lphrase.add(new Chunk("\n\n" + letter + "\n",
                        Fonts.getFont(TIMES_ROMAN, 12)));
                ct.addText(lphrase);
            }

			// Print member name
			Paragraph phrase = new Paragraph((float) 10.0);
			phrase.add(new Chunk("\n" + memberName + "  ", Fonts.getFont(TIMES_ROMAN, 9)));

			Iterator sortedPages = getSortedPageNumbers(memberName);
			boolean firstNo = true;
			while(sortedPages.hasNext()) {
				Integer pageNo = (Integer)sortedPages.next();
				// Always add 1 to the stored value, because the pages were
				// counted beginning with 0 internally, but their visible
				// numbering starts with 1
				String pageNumberText = String.valueOf(pageNo.intValue() + 1);
				if(!firstNo) {
					phrase.add(new Chunk(", ", Fonts.getFont(TIMES_ROMAN, 9)));
				}
				phrase.add(new Chunk(pageNumberText, Fonts.getFont(TIMES_ROMAN, 9)));
				firstNo = false;
			}

            ct.addText(phrase);
        }

        // Now print index by printing columns into document
        int status = 0;
        int column = 0;

        while ((status & ColumnText.NO_MORE_TEXT) == 0) {
            ct.setSimpleColumn(right[column], 60, left[column], 790, 16,
                Element.ALIGN_LEFT);
            status = ct.go();

            if ((status & ColumnText.NO_MORE_COLUMN) != 0) {
                column++;

                if (column > 1) {
                    pdfDocument.newPage();
                    column = 0;
                }
            }
        }
        
        log.debug("** Index created.");
        
        log.debug("<");
    }

	/**
	 * Implements the Comparator interface. Makes sure
	 * that the member names are sorted alphabetically,
	 * but WITHOUT regard to upper-/lowercase letters.
	 * 
	 * @param o1 The first entry for the comparison.
	 * @param o2 The second entry for the comparison.
	 * @return A value defining the order.
	 */
    public int compare(Object o1, Object o2) {
        return ((String) o1).compareToIgnoreCase((String) o2);
    }

	/**
	 * Inner class used to sort member page numbers.
	 */    
    class PageNumberSorter implements Comparator {

		/**
		 * Implements the Comparator interface. Makes sure
		 * that the page numbers of a member are sorted 
		 * in ascending order.
		 * 
		 * @param o1 The first entry for the comparison.
		 * @param o2 The second entry for the comparison.
		 * @return A value defining the order.
		 */
		public int compare(Object o1, Object o2) {
			Integer int1 = (Integer)o1;
			Integer int2 = (Integer)o2;
			int result = -1;
			if(int1.intValue() == int2.intValue()) {
				result = 0;
			}
			if(int1.intValue() > int2.intValue()) {
				result = 1;
			}
			return result;
		}
    }
}

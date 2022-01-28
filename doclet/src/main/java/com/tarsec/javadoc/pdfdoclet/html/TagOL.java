/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.List;
import com.lowagie.text.Paragraph;
import com.lowagie.text.RomanList;

/**
 * Implements an Ordered-List Tag (OL)
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagOL extends HTMLTag {

    /** Logger reference */
    private static Logger log = Logger.getLogger(TagOL.class);
    
    /** Stores list entries. */
    List list = null;

	/**
	 * 
	 * @param parent
	 * @param type
	 */
    public TagOL(HTMLTag parent, int type) {
        super(parent, type);
    }
    
    /**
     * 
     * @return
     */
    protected char getTypeChar() {
        String listType = getAttribute("type");
        if (listType != null && listType.length() > 0) {
            return listType.charAt(0);
        }
        return '1';
    }
    
    /**
     * 
     * @return
     */
    protected int getFirstListIndex() {
        String firstAttr = getAttribute("start");
        try {
            if (firstAttr != null) {
                return Integer.parseInt(firstAttr);
            }
        } catch (NumberFormatException e) {
            log.debug("Invalid OL start value '"+firstAttr+"'", e);
        }
        return 1;
    }

    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
     */
    public Element[] openTagElements() {
        char typeChar = getTypeChar();
        int first = getFirstListIndex();
        
        switch (typeChar) {
            case 'a':
            case 'A':
                list = new List(false, true, 20);
                if (first > 0 && first <= 26) {
                    list.setFirst((char)(typeChar + (first-1)));
                }
                break;
            case 'i':
            case 'I':
                list = new RomanList(typeChar == 'i', 20);
                if (first > 0) {
                    list.setFirst(first);
                }
                break;
            case '1':
            default:
                list = new List(true, 20);
                if (first > 0) {
                    list.setFirst(first);
                }
        }
        list.setListSymbol(new Chunk("", getFont()));
        
        Element[] elements = new Element[1];
        elements[0] = new Paragraph((float) 8.0, " ", getFont());
        return elements;
    }
    
    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#closeTagElements()
     */
	public Element[] closeTagElements() {
	    if(this.parent.getType() == TAG_LI ||
	        this.parent.getType() == TAG_UL || 
	        this.parent.getType() == TAG_OL) {
	        // If this list is nested in another ordered list, do not
	        // add additional empty space at the end of if.
			Element[] entries = new Element[1];
			entries[0] = list;
			return entries;
	    } else {
			Element[] entries = new Element[2];
			entries[0] = list;
	        entries[1] = new Paragraph("");
			return entries;
	    }
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#addNestedTagContent(com.lowagie.text.Element[])
	 */
	public void addNestedTagContent(Element[] content) {
        for (int i = 0; i < content.length; i++) {
            list.add(content[i]);
        }
    }
}

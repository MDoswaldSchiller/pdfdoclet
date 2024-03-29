/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;


/**
 * Implements the DD tag.
 * 
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagDD extends HTMLTag {

    /** Logger reference */
    private static Logger log = Logger.getLogger(TagDD.class);

    /** Stores a definition. */
    private Paragraph definition = null;

    /**
     * Creates a DD tag object.
     * 
     * @param parent The parent HTML tag object.
     * @param type The type of this HTML tag.
     */
    public TagDD(HTMLTag parent, int type) {
        super(parent, type);
    }

    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
     */
    public Element[] openTagElements() {
        Element[] elements = new Element[1];
        Paragraph p1 = createParagraph("");
        p1.setIndentationLeft((float)20.0);
        elements[0] = p1;
        return elements;
    }

}

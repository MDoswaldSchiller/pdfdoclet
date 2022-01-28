/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;


/**
 * Represents the underline tag
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagU extends HTMLTag {

    /** Logger reference */
    private static Logger log = Logger.getLogger(TagU.class);

    public TagU(HTMLTag parent, int type) {
        super(parent, type);
        isUnderline = true;
    }

    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#isUnderline()
     */
    public boolean isUnderline() {
        return true;
    }
}

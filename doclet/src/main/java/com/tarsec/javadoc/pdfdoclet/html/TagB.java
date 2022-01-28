/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;



/**
 * Implements the bold HTML tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagB extends HTMLTag {

    /** Logger reference */
    private static Logger log = Logger.getLogger(TagB.class);

    public TagB(HTMLTag parent, int type) {
        super(parent, type);
        isBold = true;
    }

    public boolean isBold() {
        return true;
    }
}

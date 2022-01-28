/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

/**
 * Implements the BODY tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagBODY extends HTMLTag {

    /** Logger reference */
    private static Logger log = Logger.getLogger(TagBODY.class);

	boolean paragraphCreated = false;

	public TagBODY(HTMLTag parent, int type) {
		super(parent, type);
	}
    /*
    public Element toElement(String text) {
		// Otherwise, just create a phrase.
		Phrase result = new Phrase();
		Phrase[] links = PDFUtil.inlineLinks(Util.stripLineFeeds(text), getFont());
		for (int i = 0; i < links.length; i++) {
			result.add(links[i]);
		}
		return result;
    }
    */
}

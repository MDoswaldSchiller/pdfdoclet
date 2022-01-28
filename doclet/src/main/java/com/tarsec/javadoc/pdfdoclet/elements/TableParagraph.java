/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.elements;

import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;

/**
 * A Paragraph that contains a single PdfPTable instance (or at least starts with one).
 * The normal add() method does not work for these; the protected addSpecial() method
 * must be used instead which requires a subclass.
 * 
 * @version $Revision: 1.1 $
 * @author Carl E. Lindberg
 */
public class TableParagraph extends Paragraph {
    
    /**
     * 
     * @param table
     */
    public TableParagraph(PdfPTable table) {
        super();
        addSpecial(table);
    }

    /**
     * 
     * @return
     */
    public PdfPTable getTable() {
        return (PdfPTable)get(0);
    }
}

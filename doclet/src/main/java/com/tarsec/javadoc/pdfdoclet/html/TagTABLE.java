/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */

package com.tarsec.javadoc.pdfdoclet.html;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.elements.TableParagraph;

/** 
 * TagTABLE creates an iText PdfPTable instance and returns it as the only
 * content.  The content is made up of PdfPCell instances
 * created by TagCELL, and put in rows by TagROW.
 * 
 * @version $Revision: 1.1 $
 * @author Carl A. Lindberg
 */
public class TagTABLE extends HTMLTag {
    
    /** Logger reference */
    private static Logger log = Logger.getLogger(TagTABLE.class);

    /** Reference to the PdfPTable instance. */
    private PdfPTable table;
    
    /** Flag which defines if the table has a header row. */
    private boolean hasHeaders = false;

    /**
     * Creats a HTML table object.
     * 
     * @param parent The parent HTML tag.
     * @param type The tag type.
     */
    public TagTABLE(HTMLTag parent, int type) {
        super(parent, type);
    }

    /**
     * 
     * @param elements
     * @return
     */
    private PdfPCell[] getRow(Element[] elements) {
        if (elements == null || elements.length == 0)
            return null;

        PdfPCell[] row = new PdfPCell[elements.length];
        for (int i=0; i<elements.length; i++) {
            if (!(elements[i] instanceof PdfPCell)) {
                return null;
            }
            row[i] = (PdfPCell)elements[i];
        }
        
        return row;
    }

    /**
     * 
     * @param cell
     * @param attr
     * @return
     */
    private boolean cellHasAttribute(PdfPCell cell, String attr) {
        Properties props = cell.getMarkupAttributes();
        return (props == null)? false : props.containsKey(attr);
    }

    /**
     * 
     * @param row
     * @return
     */
    private int getNumColumns(PdfPCell[] row) {
        int cols = 0;
        for (int i=0; i<row.length; i++) {
            /* THEAD tag may cause multiple rows to be combined; check for TagROW indicator */
            if (i>0 && cellHasAttribute(row[i], TagTR.ROW_START_ATTR)) {
                break;
            }
            cols += row[i].getColspan();
        }
        return cols;
    }

    /**
     * 
     * @param numcols
     */
    private void createTable(int numcols) {
        log.debug("Creating table with "+numcols+" columns");

        table = new PdfPTable(numcols);

        String width = getAttribute("width");
        if (width == null) {
            table.setWidthPercentage(100);
        }
        else if (width.endsWith("%")) {
            table.setWidthPercentage(
                    HTMLTagUtil.parseFloat(width.substring(0, width.length() - 1), 100f));
        }
        else {
            table.setTotalWidth(HTMLTagUtil.parseFloat(width, 400f));
            // table.setLockedWidth(true);  //newer iText version 
        }

        table.getDefaultCell().setPadding(HTMLTagUtil.parseFloat(getAttribute("cellpadding"), 2.0f));
        table.getDefaultCell().setBackgroundColor(HTMLTagUtil.getColor(getAttribute("bgcolor")));
        table.setHorizontalAlignment(HTMLTagUtil.getAlignment(getAttribute("align"), Element.ALIGN_CENTER));
//        table.setSplitRows(false); //hm, can cause rows larger than page to be omitted completely

        /* Border doesn't have to have a value set */
        if (getAttribute("border") != null) {
            table.getDefaultCell().setBorder(Rectangle.BOX);
            table.getDefaultCell().setBorderWidth(HTMLTagUtil.parseFloat(getAttribute("border"), 1.0f));
            table.getDefaultCell().setBorderColor(HTMLTagUtil.getColor("gray"));
        }
        else {
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            table.getDefaultCell().setBorderWidth(0.0f);
        }
    }

    /**
     * 
     * @param cell
     * @return
     */
    private boolean isHeader(PdfPCell cell) {
        return cellHasAttribute(cell, TagTD.HEADER_INDICATOR_ATTR);
    }

    /**
     * 
     * @param table
     * @return
     */
    private int getCurrCol(PdfPTable table) {
        return new IndexAccess(table).getNextColumnIndex();
    }

    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#addNestedTagContent(com.lowagie.text.Element[])
     */
    public void addNestedTagContent(Element[] elements) {
        PdfPCell[] row = getRow(elements);
       
        // If elements is a row of Cells, add it to the table
        
        if (row != null) {
            if (table == null) {
                createTable(getNumColumns(row));
                hasHeaders = isHeader(row[0]);
            }
            else if (!isHeader(row[0]) && hasHeaders) {
                table.setHeaderRows(table.size());
                hasHeaders = false;
            }

            for (int i=0; i<row.length; i++) {
                row[i].setBorderWidth(table.getDefaultCell().borderWidth());
                row[i].setBorderColor(table.getDefaultCell().borderColor());
                row[i].setBorder(table.getDefaultCell().border());
                row[i].setPadding(table.getDefaultCell().getPaddingLeft());
                table.addCell(row[i]);
            }

            /* If we haven't finished a row, fill it out.  Not sure this is a good idea. */
            int col = getCurrCol(table);
            int expectedCols = table.getAbsoluteWidths().length;
            if (col > 0) {
                for (int i=col; i<expectedCols; i++) {
                    table.addCell(new PdfPCell(new Phrase()));
                }
            }
        }
    }

    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#toElement(java.lang.String)
     */
    public Element toElement(String text) {
        // Interior text is ignored... though I think Tidy does that for us 
        return null;
    }

    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#openTagElements()
     */
    public Element[] openTagElements() {
        table = null;
        Element[] elements = new Element[1];
        elements[0] = new Paragraph((float) 8.0, " ");
        return elements;
//        return super.openTagElements();
    }

    /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#closeTagElements()
     */
    public Element[] closeTagElements() {
        if (table == null) {
            createTable(1);
        }

        /*
         * The returned elements here are added to Paragraph instances.
         * Since PdfPTable cannot be used this way, we need to put it
         * inside a special Paragraph instance in order for it to work.
         */
        return new Element[] {new TableParagraph(table)};
    }

    // *************************************************************
    // Inner classes
    // *************************************************************

    /**
     * ...
     * 
     * @author Carl A. Lindberg
     * @version $Revision: 1.1 $
     */
    private class IndexAccess extends PdfPTable {
        IndexAccess(PdfPTable table) {
            super(table);
        }
        int getNextColumnIndex() { return this.currentRowIdx; }
    }
}

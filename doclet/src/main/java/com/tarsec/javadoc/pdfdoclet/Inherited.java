/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.awt.Color;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.tarsec.javadoc.pdfdoclet.elements.CellBorderPadding;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPCell;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPTable;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;

/**
 * Prints the inherited tables.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Inherited implements IConstants {

    /** Logger reference */
    private static Logger log = Logger.getLogger(Inherited.class);

	/**
	 * Prints inherited methods and fields from superclasses
	 *
	 * @param cls class source to get inherited fields and methods for.
	 * @param show SHOW_METHODS or SHOW_FIELDS
	 * @throws Exception
	 */
	public static void print(ClassDoc supercls, int show)
		throws Exception {
		String type;
        
		FieldDoc[] fields = supercls.fields();

		Arrays.sort(fields);
        log.debug("Fields: " + fields.length);

		if (supercls.isInterface()) {
			type = "interface";
		} else {
			type = "class";
		}

		// Create cell for additional spacing below
		PdfPCell spacingCell = new PdfPCell();
		spacingCell.addElement(new Chunk(" "));
		spacingCell.setFixedHeight((float)4.0);
		spacingCell.setBorder(Rectangle.BOTTOM + Rectangle.LEFT + Rectangle.RIGHT);
		spacingCell.setBorderColor(Color.gray);
		
		if ((fields.length > 0) && (show == SHOW_FIELDS)) {
			PDFDocument.instance().add(new Paragraph((float) 6.0, " "));

			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage((float) 100);

			Paragraph newLine = new Paragraph();
			newLine.add(new Chunk("Fields inherited from " + type + " ",
			        Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
			newLine.add(new LinkPhrase(supercls.qualifiedTypeName(),
					null, 10, false));

			table.addCell(new CustomPdfPCell(newLine, COLOR_INHERITED_SUMMARY));

			Paragraph paraList = new Paragraph();

			for (int i = 0; i < fields.length; i++) {
				paraList.add(new LinkPhrase(
						fields[i].qualifiedName(), 
						fields[i].name(), 10,
						false));

				if (i != (fields.length - 1)) {
					paraList.add(new Chunk(", ", Fonts.getFont(TIMES_ROMAN, 9)));
				}
			}

			PdfPCell contentCell = new CellBorderPadding(paraList);
			float leading = (float)contentCell.getLeading() + (float)1.1;
			contentCell.setLeading(leading, leading);
			table.addCell(contentCell);
			table.addCell(spacingCell);

			PDFDocument.instance().add(table);
		}

		MethodDoc[] meth = supercls.methods();

		Arrays.sort(meth);
        log.debug("Methods: " + meth.length);

		if ((meth.length > 0) && (show == SHOW_METHODS)) {
			PDFDocument.instance().add(new Paragraph((float) 6.0, " "));

			PdfPTable table = new CustomPdfPTable();

			Paragraph newLine = new Paragraph();
			newLine.add(new Chunk("Methods inherited from " + type + " ",
			        Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
			newLine.add(new LinkPhrase(supercls.qualifiedTypeName(),
					null, 10, false));
            
            table.addCell(new CustomPdfPCell(newLine, COLOR_INHERITED_SUMMARY));
			Paragraph paraList = new Paragraph();

			for (int i = 0; i < meth.length; i++) {
				String methodLabel = meth[i].name();
                
				// Do not list static initializers like "<clinit>"
				if (!methodLabel.startsWith("<")) {
					paraList.add(new LinkPhrase(
							supercls.qualifiedTypeName() + "." + meth[i].name(), 
							meth[i].name(), 10,
							false));

					if (i != (meth.length - 1)) {
						paraList.add(new Chunk(", ", Fonts.getFont(COURIER, 10)));
					}
				}
			}

			PdfPCell contentCell = new CellBorderPadding(paraList);
			float leading = (float)contentCell.getLeading() + (float)1.1;
			contentCell.setLeading(leading, leading);
			table.addCell(contentCell);
			table.addCell(spacingCell);
			    
			PDFDocument.instance().add(table);
		}

		// Print inherited interfaces / class methods and fields recursively
		ClassDoc supersupercls = null;
        
		if(supercls.isClass()) {
			supersupercls = supercls.superclass();
		}
        
		if(supersupercls != null) {
		    String className = supersupercls.qualifiedName();
		    if(ifClassMustBePrinted(className)) {
		        Inherited.print( supersupercls, show );
		    }
		}
        
        ClassDoc[] interfaces = supercls.interfaces();
        log.debug("Interfaces: " + interfaces.length);
        for(int i = 0; i < interfaces.length; i++) {
            supersupercls = interfaces[i];
            String className = supersupercls.qualifiedName();
            if(ifClassMustBePrinted(className)) {
                Inherited.print( supersupercls, show );
            }
        }
	}

    /**
     * 
     * @param className
     * @return
     */
	public static boolean ifClassMustBePrinted(String className) {
	    boolean printClass = true;
	    if(!Destinations.isValid(className) && 
	            !Configuration.isShowExternalInheritedSummaryActive()) {
	        log.debug("Do not print inherited table for external element " + className);
		    printClass = false;
	    }
	    return printClass;
	}
}

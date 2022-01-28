/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/Members.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */

package com.tarsec.javadoc.pdfdoclet;

import org.apache.log4j.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.tarsec.javadoc.pdfdoclet.elements.CellNoBorderNoPadding;
import com.tarsec.javadoc.pdfdoclet.elements.CustomDeprecatedPhrase;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPCell;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;


/**
 * Prints member (method, variable) information.
 * 
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class Members implements IConstants {

    /** Logger reference */
    private static Logger log = Logger.getLogger(Members.class);
    

    /**
     * Prints all methods of a class or interface.
     *
     * @param classDoc The class or interface whose methods should be printed.
     * @throws Exception
     */
    public static void printMembers(ClassDoc classDoc) throws Exception {
        Phrase deprecatedPhrase = null;
        
        log.debug(">");

        // test if class is deprecated
        boolean allDeprecated = false;

        if (classDoc.tags(DOC_TAGS_DEPRECATED).length > 0) {
            allDeprecated = true;
        }

        log.debug("Print fields...");
        State.setTypeOfCurrentMember(State.TYPE_FIELD);
        FieldDoc[] fields = classDoc.fields();

        if ((fields != null) && (fields.length > 0)) {
            for (int i = 0; i < fields.length; i++) {
                boolean isFirst = false;

                if (i == 0) {
                    isFirst = true;
                }

                // test if field is deprecated
                boolean isDeprecated = false;

                if (allDeprecated ||
                        (fields[i].tags(DOC_TAGS_DEPRECATED).length > 0)) {
                    isDeprecated = true;
                    deprecatedPhrase = new CustomDeprecatedPhrase(fields[i]);
                }

                String declaration = JavadocUtil.getFieldModifiers(fields[i]) +
                    fields[i].type().qualifiedTypeName() + " ";

                Members.printMember(
                    declaration, null, fields[i], null, null,
                    isFirst, true, false, isDeprecated, deprecatedPhrase,
                    fields[i].constantValue());
                
                TagLists.printMemberTags(fields[i]);
                State.setContinued(false);

                if (i < (fields.length - 1)) {
                    PDFUtil.printLine();
                }
            }
        }

        log.debug("Print constructors...");
        State.setTypeOfCurrentMember(State.TYPE_CONSTRUCTOR);
        ConstructorDoc[] constructors = classDoc.constructors();

        if ((constructors != null) && (constructors.length > 0)) {
            for (int i = 0; i < constructors.length; i++) {
                boolean isFirst = false;

                if (i == 0) {
                    isFirst = true;
                }

                // test if constructor is deprecated
                boolean isDeprecated = false;

                if (allDeprecated ||
                        (constructors[i].tags(DOC_TAGS_DEPRECATED).length > 0)) {
                    isDeprecated = true;
                    deprecatedPhrase = new CustomDeprecatedPhrase(constructors[i]);
                }

                String declaration = JavadocUtil.getConstructorModifiers(constructors[i]);
                Members.printMember( 
                        declaration, null,
                    constructors[i],
                    constructors[i].parameters(), null, 
                    isFirst, false, true, isDeprecated, deprecatedPhrase, null);
                
                TagLists.printMemberTags(constructors[i]);
                
                State.setContinued(false);

                if (i < (constructors.length - 1)) {
                    PDFUtil.printLine();
                }
            }
        }

        log.debug("Print methods...");
        State.setTypeOfCurrentMember(State.TYPE_METHOD);
        MethodDoc[] methods = classDoc.methods();

        if ((methods != null) && (methods.length > 0)) {
            for (int i = 0; i < methods.length; i++) {
                boolean isFirst = false;

                if (i == 0) {
                    isFirst = true;
                }

                // test if method is deprecated
                boolean isDeprecated = false;

                if (allDeprecated ||
                        (methods[i].tags(DOC_TAGS_DEPRECATED).length > 0)) {
                    isDeprecated = true;
                    deprecatedPhrase = new CustomDeprecatedPhrase(methods[i]);
                }

                String declaration = JavadocUtil.getMethodModifiers(methods[i]);

                if (i == (methods.length - 1)) {
                    State.setLastMethod(true);
                }

                State.increasePackageMethod();
                State.setCurrentMethod(methods[i].name());

                Phrase returnType = PDFUtil.getReturnType(methods[i], 10);
                Members.printMember(
                    declaration, returnType, methods[i],
                    methods[i].parameters(), methods[i].thrownExceptions(),
                    isFirst, false, false, isDeprecated,
                    deprecatedPhrase, null);
                
                TagLists.printMemberTags(methods[i]);

                State.setContinued(false);

                if (i < (methods.length - 1)) {
                    PDFUtil.printLine();
                }
            }
        }
        
        State.setTypeOfCurrentMember(State.TYPE_NONE);
        log.debug("<");
    }

    
    /**
     * Prints member information.
     *
     * @param declaration The modifiers ("public static final..").
     * @param returnType Phrase with the return type text (might be
     *                   a hyperlink)
     * @param commentText The explanation text (might have HTML embedded).
     * @param parms Parameters of a method or constructor, null for a field.
     * @param thrownExceptions Exceptions of a method, null for a field or constructor.
     * @param isFirst True if it is the first field/method/constructor in the list.
     * @param isField True if it is a field.
     * @param isConstructor True if it is a constructor.
     * @throws Exception
     */
    public static void printMember(
            String declaration, Phrase returnType, ProgramElementDoc commentDoc,
            Parameter[] parms, ClassDoc[] thrownExceptions,
            boolean isFirst, boolean isField, boolean isConstructor,
            boolean isDeprecated, Phrase deprecatedPhrase, Object constantValue)
            throws Exception {
        
        log.debug(">");

        String name = commentDoc.name();

        State.setCurrentMember(State.getCurrentClass() + "." + name);
        State.setCurrentDoc(commentDoc);
        
        log.info("....> " + State.getCurrentMember());

        // Create bookmark anchors
        
        // Returns the text, resolving any "inheritDoc" inline tags
        String commentText = JavadocUtil.getComment(commentDoc);
        
        // TODO: The following line may set the wrong page number
        //      in the index, when the member gets printed on a
        //      new page completely (because it is in one table).
        // Solution unknown yet. Probably split up table.
        PDFDoclet.getIndex().addToMemberList(State.getCurrentMember());

        // Prepare list of exceptions (if it throws any)
        String throwsText = "throws";
        int parmsColumn = declaration.length() +
            (name.length() - throwsText.length());

        // First output text line (declaration of method and first parameter or "()" ).
        // This first line is a special case because the class name is bold,
        // while the rest is regular plain text, so it must be built using three Chunks.
        Paragraph declarationParagraph = new Paragraph((float) 10.0);

        // left part / declaration ("public static..")
        Chunk leftPart = new Chunk(declaration, Fonts.getFont(COURIER, 10));

        declarationParagraph.add(leftPart);

        if (returnType != null) {
            // left middle part / declaration ("public static..")
            declarationParagraph.add(returnType);
            declarationParagraph.add(new Chunk(" ", Fonts.getFont(COURIER, 10)));
            parmsColumn = 2;
        }

        // right middle part / bold class name
        declarationParagraph.add(new Chunk(name, Fonts.getFont(COURIER, BOLD, 10)));

        if (!isField) {
            // 1st parameter or empty brackets

            if ((parms != null) && (parms.length > 0)) {
                Phrase wholePhrase = new Phrase("(", Fonts.getFont(COURIER, 10));
                // create link for parameter type
                wholePhrase.add(PDFUtil.getParameterTypePhrase(parms[0], 10));
                // then normal text for parameter name
                wholePhrase.add(" " + parms[0].name());
                if (parms.length > 1) {
                    wholePhrase.add(",");
                } else {
                    wholePhrase.add(")");
                }

                // In order to have the parameter types in the bookmark,
                // make the current state text more detailled
                String txt = State.getCurrentMethod() + "(";
                for(int i = 0; i < parms.length; i++) {
                    if(i > 0) {
                        txt = txt + ",";
                    }
                    txt = txt + JavadocUtil.getParameterType(parms[i]);
                }
                txt = txt + ")";
                State.setCurrentMethod(txt);

                // right part / parameter and brackets
                declarationParagraph.add(wholePhrase);
                
            } else {
            
                String lastPart = "()";
                State.setCurrentMethod(State.getCurrentMethod() + lastPart);
                // right part / parameter and brackets
                declarationParagraph.add(new Chunk(lastPart, Fonts.getFont(COURIER, 10)));
            }

        }

        int rows = 2;

        if (isFirst) {
            rows++;
        }

        float[] widths = { (float) 6.0, (float) 94.0 };
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage((float) 100);

        // Before the first constructor or method, create a coloured title bar
        if (isFirst) {
            PdfPCell colorTitleCell = null;

            // Some empty space...
            PDFDocument.add(new Paragraph((float) 6.0, " "));

            if (isConstructor) {
                colorTitleCell = new CustomPdfPCell("Constructors");
            } else if (isField) {
                colorTitleCell = new CustomPdfPCell("Fields");
            } else {
                colorTitleCell = new CustomPdfPCell("Methods");
            }

            colorTitleCell.setColspan(2);
            table.addCell(colorTitleCell);
        }

        // Method name (large, first line of a method description block)
        Phrase linkPhrase = Destinations.createDestination(commentDoc.name(), 
                commentDoc, Fonts.getFont(TIMES_ROMAN, BOLD, 14));
        Paragraph nameTitle = new Paragraph(linkPhrase);
        PdfPCell nameCell = new CellNoBorderNoPadding(nameTitle);

        if (isFirst) {
            nameCell.setPaddingTop(10);
        } else {
            nameCell.setPaddingTop(0);
        }

        nameCell.setPaddingBottom(8);
        nameCell.setColspan(1);

        // Create nested table in order to try to prevent the stuff inside
        // this table from being ripped appart over a page break. The method
        // name and the declaration/parm/exception line(s) should always be
        // together, because everything else just looks bad
        PdfPTable linesTable = new PdfPTable(1);
        linesTable.addCell(nameCell);
        linesTable.addCell(new CellNoBorderNoPadding(declarationParagraph));

        if (!isField) {
            // Set up following declaration lines
            Paragraph[] params = PDFUtil.createParameters(parmsColumn, parms);
            Paragraph[] exceps = PDFUtil.createExceptions(parmsColumn,
                    thrownExceptions);

            for (int i = 0; i < params.length; i++) {
                linesTable.addCell(new CellNoBorderNoPadding(params[i]));
            }

            for (int i = 0; i < exceps.length; i++) {
                linesTable.addCell(new CellNoBorderNoPadding(exceps[i]));
            }
        }

        // Create cell for inserting the nested table into the outer table
        PdfPCell cell = new PdfPCell(linesTable);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(2);
        table.addCell(cell);

        // The empty, left cell (the invisible indentation column)
        State.setContinued(true);

        PdfPCell leftCell = PDFUtil.createElementCell(5, new Phrase("", Fonts.getFont(TIMES_ROMAN, BOLD, 6)));
        PdfPCell spacingCell = new PdfPCell();
        spacingCell.setFixedHeight((float)8.0);
        spacingCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(spacingCell);
        table.addCell(spacingCell);

        // The descriptive method explanation text

        if (isDeprecated) {
            Phrase commentPhrase = new Phrase();
            commentPhrase.add(new Phrase(IConstants.LB_DEPRECATED_TAG,
                    Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
            commentPhrase.add(deprecatedPhrase);
            table.addCell(leftCell);
            table.addCell(PDFUtil.createElementCell(0, commentPhrase));
            
            commentPhrase = new Phrase();
            commentPhrase.add(Chunk.NEWLINE);
            table.addCell(leftCell);
            table.addCell(PDFUtil.createElementCell(0, commentPhrase));
        }

        Element[] objs = HtmlParserWrapper.createPdfObjects(commentText);

        if (objs.length == 1) {
            table.addCell(leftCell);
            table.addCell(PDFUtil.createElementCell(0, objs[0]));
        } else {
            table.addCell(leftCell);
            table.addCell(PDFUtil.createElementCell(0, Element.ALIGN_LEFT, objs));
        }

        // TODO: FORMAT THIS CONSTANT VALUE OUTPUT CORRECTLY

        if(isField) {
            if (constantValue != null) {
                // Add 2nd comment line (left cell empty, right cell text)
                Chunk valueTextChunk = new Chunk("Constant value: ", Fonts.getFont(
                        TIMES_ROMAN, PLAIN, 10));
                Chunk valueContentChunk = new Chunk(constantValue.toString(), Fonts
                        .getFont(COURIER, BOLD, 10));
                Phrase constantValuePhrase = new Phrase("");
                constantValuePhrase.add(valueTextChunk);
                constantValuePhrase.add(valueContentChunk);
                table.addCell(leftCell);
                table.addCell(PDFUtil.createElementCell(0, constantValuePhrase));
            }
        }
      
        // Add whole method block to document
        PDFDocument.add(table);
        
        log.debug("<");
   }
}

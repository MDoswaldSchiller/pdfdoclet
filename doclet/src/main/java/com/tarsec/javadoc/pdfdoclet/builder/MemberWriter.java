/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tarsec.javadoc.pdfdoclet.builder;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.Destinations;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.State;
import com.tarsec.javadoc.pdfdoclet.elements.CellNoBorderNoPadding;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPCell;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Utils;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.*;

/**
 *
 * @author mdo
 */
public class MemberWriter
{

  private static final Logger LOG = LoggerFactory.getLogger(MemberWriter.class);

  private final DocletEnvironment environment;
  private final Document pdfDocument;

  public MemberWriter(DocletEnvironment environment, Document pdfDocument)
  {
    this.environment = Objects.requireNonNull(environment);
    this.pdfDocument = Objects.requireNonNull(pdfDocument);
  }

  public void appendMemberDetails(TypeElement type) throws DocumentException
  {
    LOG.debug("Print fields...");
    
    //State.setTypeOfCurrentMember(State.TYPE_FIELD);
    List<VariableElement> fields = ElementFilter.fieldsIn(type.getEnclosedElements());

    if (!fields.isEmpty()) {
      addFieldsDetails(type, fields);
    }
  }

  private void addFieldsDetails(TypeElement type, List<VariableElement> fields) throws DocumentException
  {
    boolean first = true;

    
    PdfPTable fieldsTable = createTable("Fields");
    
    for (VariableElement field : fields) {
      if (!first) {
        PDFUtil.printLine();
      }
      
      // test if field is deprecated
      boolean isDeprecated = Utils.isDeprecated(environment.getDocTrees(), type) ||
                             Utils.isDeprecated(environment.getDocTrees(), field);
      Phrase deprecatedPhrase = null;

      if (isDeprecated) {
        //deprecatedPhrase = new CustomDeprecatedPhrase(field);
      }

      addFieldDetail(fieldsTable, type, field, isDeprecated, deprecatedPhrase, first);

      //TagLists.printMemberTags(fields[i]);
      //State.setContinued(false);
      first = false;
    }
    
    // Some empty space + table
    pdfDocument.add(new Paragraph((float) 6.0, " "));
    pdfDocument.add(fieldsTable);
  }
  
  public void addFieldDetail(PdfPTable table, TypeElement type, VariableElement field, boolean isDeprecated, Phrase deprecatedPhrase, boolean isFirst) throws DocumentException
  {
    LOG.debug(">");

    //String declaration = JavadocUtil.getFieldModifiers(fields[i]) + fields[i].type().qualifiedTypeName() + " ";
    String declaration = Utils.getMemberModifiers(field) + field.asType().toString() + " ";
    String name = field.getSimpleName().toString();

    //State.setCurrentMember(State.getCurrentClass() + "." + name);
//    State.setCurrentDoc(commentDoc);

    LOG.info("....> {}.{}", type.getQualifiedName(), field.getSimpleName());

    // Create bookmark anchors
    // Returns the text, resolving any "inheritDoc" inline tags
    String commentText = Utils.getComment(environment.getDocTrees(), field);

    // TODO: The following line may set the wrong page number
    //      in the index, when the member gets printed on a
    //      new page completely (because it is in one table).
    // Solution unknown yet. Probably split up table.
   // PDFDoclet.getIndex().addToMemberList(State.getCurrentMember());

    // First output text line (declaration of method and first parameter or "()" ).
    // This first line is a special case because the class name is bold,
    // while the rest is regular plain text, so it must be built using three Chunks.
    Paragraph declarationParagraph = new Paragraph((float) 10.0);

    // left part / declaration ("public static..")
    Chunk leftPart = new Chunk(declaration, Fonts.getFont(COURIER, 10));

    declarationParagraph.add(leftPart);

    // right middle part / bold class name
    declarationParagraph.add(new Chunk(name, Fonts.getFont(COURIER, BOLD, 10)));

    // Method name (large, first line of a method description block)
    Phrase linkPhrase = Destinations.createFieldDestination(type, field, Fonts.getFont(TIMES_ROMAN, BOLD, 14));
    Paragraph nameTitle = new Paragraph(linkPhrase);
    PdfPCell nameCell = new CellNoBorderNoPadding(nameTitle);

    if (isFirst) {
      nameCell.setPaddingTop(10);
    }
    else {
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
    spacingCell.setFixedHeight((float) 8.0);
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
    }
    else {
      table.addCell(leftCell);
      table.addCell(PDFUtil.createElementCell(0, Element.ALIGN_LEFT, objs));
    }

    // TODO: FORMAT THIS CONSTANT VALUE OUTPUT CORRECTLY
    Object constantValue = field.getConstantValue();

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

    LOG.debug("<");
  }
  
  private PdfPTable createTable(String title)
  {
    float[] widths = {(float) 6.0, (float) 94.0};
    PdfPTable table = new PdfPTable(widths);
    table.setWidthPercentage((float) 100);

    // Before the first constructor or method, create a coloured title bar
    PdfPCell colorTitleCell = new CustomPdfPCell(title);
    colorTitleCell.setColspan(2);
    table.addCell(colorTitleCell);
    
    return table;
  }
  
}

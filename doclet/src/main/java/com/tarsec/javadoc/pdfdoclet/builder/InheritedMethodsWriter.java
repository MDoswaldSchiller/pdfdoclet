package com.tarsec.javadoc.pdfdoclet.builder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.elements.CellBorderPadding;
import com.tarsec.javadoc.pdfdoclet.elements.CustomPdfPCell;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.*;

/**
 *
 * @author mdo
 */
public class InheritedMethodsWriter
{
  private static final Logger LOG = LoggerFactory.getLogger(InheritedMethodsWriter.class);
  
  protected final DocletEnvironment environment;
  protected Document pdfDocument;

  public InheritedMethodsWriter(DocletEnvironment environment, Document pdfDocument)
  {
    this.environment = Objects.requireNonNull(environment);
    this.pdfDocument = Objects.requireNonNull(pdfDocument);
  }  
  
  public void addInheritedMethodsInfo(TypeElement type) throws DocumentException
  {
    Set<TypeElement> superTypes = new LinkedHashSet<>();
    Set<TypeElement> interfaces = new LinkedHashSet<>();
    fillSuperTypes(type, superTypes, interfaces);
    
    for (TypeElement typeElement : Stream.concat(superTypes.stream(), interfaces.stream()).toList()) {
      List<ExecutableElement> methods = ElementFilter.methodsIn(typeElement.getEnclosedElements());
      if (!methods.isEmpty()) {
        addInheritedMethodsBox(typeElement, methods);
      }
    }
  }
  
  private void addInheritedMethodsBox(TypeElement typeElement, List<ExecutableElement> methods) throws DocumentException
  {
    methods.sort(Comparator.comparing(method -> method.getSimpleName().toString()));
    LOG.debug("Methods: " + methods.size());

    // Create cell for additional spacing below
    PdfPCell spacingCell = new PdfPCell();
    spacingCell.addElement(new Chunk(" "));
    spacingCell.setFixedHeight((float) 4.0);
    spacingCell.setBorder(Rectangle.BOTTOM + Rectangle.LEFT + Rectangle.RIGHT);
    spacingCell.setBorderColor(BaseColor.GRAY);

    pdfDocument.add(new Paragraph((float) 6.0, " "));

    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage((float) 100);

    Paragraph newLine = new Paragraph();
    String type = (typeElement.getKind() == ElementKind.INTERFACE ? "interface" : "class");
    newLine.add(new Chunk("Methods inherited from " + type + " ",
                          Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
    newLine.add(new LinkPhrase(typeElement.getQualifiedName().toString(),
                               null, 10, false));

    table.addCell(new CustomPdfPCell(newLine, COLOR_INHERITED_SUMMARY));

    Paragraph paraList = new Paragraph();

    for (int i = 0; i < methods.size(); i++) {
      String name = methods.get(i).getSimpleName().toString();
      String destination = String.format("%s.%s", typeElement.getQualifiedName(), methods.get(i).getSimpleName());

      paraList.add(new LinkPhrase(destination, name, 10, false));

      if (i != (methods.size() - 1)) {
        paraList.add(new Chunk(", ", Fonts.getFont(TIMES_ROMAN, 9)));
      }
    }

    PdfPCell contentCell = new CellBorderPadding(paraList);
    float leading = (float) contentCell.getLeading() + (float) 1.1;
    contentCell.setLeading(leading, leading);
    table.addCell(contentCell);
    table.addCell(spacingCell);

    pdfDocument.add(table);
  }
  
  
  private void fillSuperTypes(TypeElement type, Set<TypeElement> superTypes, Set<TypeElement> interfaces)
  {
    if (type.getSuperclass().getKind() != TypeKind.NONE) {
      TypeElement superClass = (TypeElement) environment.getTypeUtils().asElement(type.getSuperclass());
      fillSuperTypes(superClass, superTypes, interfaces);
      superTypes.add(superClass);
    }

    for (TypeMirror iface : type.getInterfaces()) {
      TypeElement ifaceClass = (TypeElement) environment.getTypeUtils().asElement(iface);
      interfaces.add(ifaceClass);
    }
  }
}
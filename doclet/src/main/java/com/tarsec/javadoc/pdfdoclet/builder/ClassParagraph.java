package com.tarsec.javadoc.pdfdoclet.builder;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.tarsec.javadoc.pdfdoclet.Destinations;
import com.tarsec.javadoc.pdfdoclet.Fonts;
import com.tarsec.javadoc.pdfdoclet.Implementors;
import com.tarsec.javadoc.pdfdoclet.PDFDocument;
import com.tarsec.javadoc.pdfdoclet.State;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import jdk.javadoc.doclet.DocletEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.*;

/**
 *
 * @author mdo
 */
public class ClassParagraph
{
  private static final Logger LOG = LoggerFactory.getLogger(ClassParagraph.class);
  
  
  private final DocletEnvironment environment;
  private final TypeElement classElement;

  public ClassParagraph(DocletEnvironment environment, TypeElement classElement)
  {
    this.environment = Objects.requireNonNull(environment);
    this.classElement = Objects.requireNonNull(classElement);
  }
  
  public void appendTo(Document pdfDocument) throws DocumentException
  {
    LOG.debug(">");
    Types typeUtils = environment.getTypeUtils();

    pdfDocument.newPage();
    State.increasePackageSection();

    State.setCurrentClass(classElement.getQualifiedName());
    //State.setCurrentDoc(classElement);
    LOG.info("..> " + State.getCurrentClass());

    // Simulate javadoc HTML layout
    // package (small) and class (large) name header
    PackageElement pktElement = Utils.getPackage(classElement);
    Paragraph namePara = new Paragraph(pktElement.getQualifiedName().toString(),
                                       Fonts.getFont(TIMES_ROMAN, BOLD, 16));
    pdfDocument.add(namePara);

    Phrase linkPhrase = null;
    if (!isInterface()) {
      linkPhrase = Destinations.createDestination("Class "
                                                  + classElement.getSimpleName(), classElement,
                                                  Fonts.getFont(TIMES_ROMAN, BOLD, 16));
    }
    else {
      linkPhrase = Destinations.createDestination("Interface "
                                                  + classElement.getSimpleName(), classElement,
                                                  Fonts.getFont(TIMES_ROMAN, BOLD, 16));
    }

    Paragraph titlePara = new Paragraph((float) 16.0, "");
    String classFileAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
    titlePara.add(PDFUtil.createAnchor(classFileAnchor, titlePara.getFont()));
    titlePara.add(linkPhrase);

    PDFDocument.instance().add(titlePara);

    // class derivation tree - build list first
    Map<TypeElement,TypeElement> parentMap = new HashMap<>();
    TypeElement currentTreeClass = classElement;
    TypeElement superClass = null;
    TypeElement subClass = null;

    List<TypeElement> interfacesList = new ArrayList<>();

    while ((superClass = (TypeElement)typeUtils.asElement(currentTreeClass.getSuperclass())) != null) {
      if (!isInterface()) {
        // Store interfaces implemented by superclasses
        // because the current class also implements all
        // interfaces of its superclass (by inheritance)
        for (TypeMirror iface : superClass.getInterfaces()) {
          interfacesList.add((TypeElement)typeUtils.asElement(iface));
        }
      }

      parentMap.put(superClass, currentTreeClass);
      currentTreeClass = superClass;
    }

    // First line of derivation tree must NOT be printed, if it's
    // the only line, and it's an interface (not a class). This is
    // because a class ALWAYS has a superclass (if only java.lang.Object),
    // but an interface does not necessarily have a super instance.
    boolean firstLine = true;

    if (isInterface() && (parentMap.get(currentTreeClass) == null)) {
      firstLine = false;
    }

    // top-level-class
    String blanks = "";

    if (firstLine) {
      PDFDocument.add(new Paragraph((float) 24.0,
                                    currentTreeClass.getQualifiedName().toString(), Fonts.getFont(COURIER, 10)));
    }

    while ((subClass = (TypeElement) parentMap.get(currentTreeClass)) != null) {
      blanks = blanks + "   ";
      PDFDocument.add(new Paragraph((float) 10.0, blanks + "|",
                                    Fonts.getFont(COURIER, 10)));

      if (parentMap.get(subClass) == null) {
        // it's last in list, so use bold font
        PDFDocument.add(new Paragraph((float) 8.0,
                                      blanks + "+-" + subClass.getQualifiedName(),
                                      Fonts.getFont(COURIER, BOLD, 10)));
      }
      else {
        // If it's not last, it's a superclass. Create link to
        // it, if it's in same API.
        Paragraph newLine = new Paragraph((float) 8.0);
        newLine.add(new Chunk(blanks + "+-", Fonts.getFont(COURIER, 10)));
        newLine.add(new LinkPhrase(
            subClass.getQualifiedName().toString(), null, 10, false));
        PDFDocument.add(newLine);
      }

      currentTreeClass = subClass;
    }


    // Now, for classes only, print implemented interfaces
    // and known subclasses
    if (!isInterface()) {
      List<? extends TypeMirror> interfaces = classElement.getInterfaces();

      // List All Implemented Interfaces
      for (TypeMirror ifaceMirror : classElement.getInterfaces()) {
        interfacesList.add((TypeElement)typeUtils.asElement(ifaceMirror));
      }

      String[] interfacesNames = new String[interfacesList.size()];
      for (int i = 0; i < interfacesNames.length; i++) {
        interfacesNames[i] = interfacesList.get(i).getQualifiedName().toString();
      }
      if (interfacesNames.length > 0) {
        Implementors.print("All Implemented Interfaces:", interfacesNames);
      }

      // Known subclasses
//      String[] knownSubclasses = ImplementorsInformation.getDirectSubclasses(State.getCurrentClass().toString());
//
//      if ((knownSubclasses != null) && (knownSubclasses.length > 0)) {
//        Implementors.print("Direct Known Subclasses:", knownSubclasses);
//      }
    }
    else {
      // For interfaces, print superinterfaces and all subinterfaces
      // Known super-interfaces
//      String[] knownSuperInterfaces = ImplementorsInformation.getKnownSuperclasses(State.getCurrentClass().toString());
//
//      if ((knownSuperInterfaces != null)
//          && (knownSuperInterfaces.length > 0)) {
//        Implementors.print("All Superinterfaces:", knownSuperInterfaces);
//      }
//
//      // Known sub-interfaces
//      String[] knownSubInterfaces = ImplementorsInformation.getKnownSubclasses(State.getCurrentClass().toString());
//
//      if ((knownSubInterfaces != null)
//          && (knownSubInterfaces.length > 0)) {
//        Implementors.print("All Subinterfaces:", knownSubInterfaces);
//      }
//
//      // Known Implementing Classes
//      String[] knownImplementingClasses = ImplementorsInformation.getImplementingClasses(State.getCurrentClass().toString());
//
//      if ((knownImplementingClasses != null)
//          && (knownImplementingClasses.length > 0)) {
//        Implementors.print("All Known Implementing Classes:", knownImplementingClasses);
//      }
    }

    // Horizontal line
    PDFUtil.printLine();

    // Class type / declaration
    LOG.debug("Print class type / declaration");
    String info = "";

//    Tag[] deprecatedTags = classElement.tags(DOC_TAGS_DEPRECATED);
//
//    if (deprecatedTags.length > 0) {
//      Paragraph classDeprecatedParagraph = new Paragraph((float) 20);
//
//      Chunk deprecatedClassText = new Chunk(LB_DEPRECATED_TAG,
//                                            Fonts.getFont(TIMES_ROMAN, BOLD, 12));
//      classDeprecatedParagraph.add(deprecatedClassText);
//
//      String depText = JavadocUtil.getComment(deprecatedTags[0].inlineTags());
//      Element[] deprecatedInfoText = HtmlParserWrapper.createPdfObjects("<i>" + depText + "</i>");
//      for (int n = 0; n < deprecatedInfoText.length; n++) {
//        // Only phrases can be supported here (but no tables)
//        if (deprecatedInfoText[n] instanceof Phrase) {
//          classDeprecatedParagraph.add(deprecatedInfoText[n]);
//        }
//      }
//
//      PDFDocument.add(classDeprecatedParagraph);
//    }

    info = Utils.getClassModifiers(classElement);

    Paragraph infoParagraph = new Paragraph((float) 20, info,
                                            Fonts.getFont(TIMES_ROMAN, 12));
    infoParagraph.add(new Chunk(classElement.getSimpleName().toString(),
                                Fonts.getFont(TIMES_ROMAN, BOLD, 12)));
    pdfDocument.add(infoParagraph);

    // extends ...
    List<? extends TypeMirror> superClassOrInterface = null;

    if (isInterface()) {
      superClassOrInterface = classElement.getInterfaces();
    }
    else {
      superClassOrInterface = List.of(classElement.getSuperclass());
    }

    if (superClassOrInterface != null) {
      Paragraph extendsPara = new Paragraph((float) 14.0);
      extendsPara.add(new Chunk("extends ", Fonts.getFont(TIMES_ROMAN, 12)));
      // Contrary to classes, interfaces DO support multiple inheritance,
      // so there could be more than one entry we're currently processing
      // an interface.
      for (int i = 0; i < superClassOrInterface.size(); i++) {
        TypeElement typeElement = (TypeElement)typeUtils.asElement(superClassOrInterface.get(i));
        
        if (typeElement != null) {
          String superClassName = Utils.getQualifiedNameIfNecessary(typeElement);
          extendsPara.add(new LinkPhrase(typeElement.getQualifiedName().toString(), superClassName, 12, true));
          // Add a comma if there are more interfaces to come
          if (i + 1 < superClassOrInterface.size()) {
            extendsPara.add(new Chunk(", "));
          }
        }
      }

      pdfDocument.add(extendsPara);
    }

    if (!isInterface() && !classElement.getInterfaces().isEmpty()) {
      //implements
      String[] interfacesNames = new String[interfacesList.size()];

      for (int i = 0; i < interfacesNames.length; i++) {
        interfacesNames[i] = interfacesList.get(i).getQualifiedName().toString();
      }

      Paragraph extendsPara = new Paragraph((float) 14.0);
      extendsPara.add(new Chunk("implements ", Fonts.getFont(TIMES_ROMAN, 12)));

      Paragraph descPg = new Paragraph((float) 24.0);

      for (int i = 0; i < interfacesNames.length; i++) {
        String subclassName = Utils.getQualifiedNameIfNecessary(interfacesNames[i]);
        Phrase subclassPhrase = new LinkPhrase(interfacesNames[i],
                                               subclassName, 12, true);
        descPg.add(subclassPhrase);

        if (i < (interfacesNames.length - 1)) {
          descPg.add(new Chunk(", ", Fonts.getFont(COURIER, 10)));
        }
      }

      extendsPara.add(descPg);

      pdfDocument.add(extendsPara);
    }

    pdfDocument.add(new Paragraph((float) 20.0, " "));

    // Description
//    String classText = JavadocUtil.getComment(classDoc);
//    Element[] objs = HtmlParserWrapper.createPdfObjects(classText);
//
//    if (objs.length == 0) {
//      String desc = Util.stripLineFeeds(classText);
//      PDFDocument.add(new Paragraph((float) 14.0, desc,
//                                    Fonts.getFont(TIMES_ROMAN, 12)));
//    }
//    else {
//      PDFUtil.printPdfElements(objs);
//    }
//
//    TagLists.printClassTags(classDoc);
//    // Horizontal line
//    PDFUtil.printLine();
//
//    if (Configuration.isShowSummaryActive()) {
//      Summary.printAll(classDoc);
//      PDFUtil.printLine();
//    }
//
//    float[] widths = {(float) 1.0, (float) 94.0};
//    PdfPTable table = new PdfPTable(widths);
//    table.setWidthPercentage((float) 100);
//
//    // Some empty space...
//    PDFDocument.add(new Paragraph((float) 6.0, " "));
//    Members.printMembers(classDoc);

    LOG.debug("<");    
  }
  
  private boolean isInterface()
  {
    return classElement.getKind() == ElementKind.INTERFACE;
  }
}

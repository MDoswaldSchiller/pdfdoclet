/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/Classes.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;
import com.tarsec.javadoc.pdfdoclet.elements.LinkPhrase;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import java.util.Hashtable;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints class information.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class Classes implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(Classes.class);

  /**
   * Prints javadoc of one given class.
   *
   * @param classDoc The javadoc information about the class.
   * @param packageDoc The package which the class is part of.
   * @throws Exception
   */
  public static void printClass(ClassDoc classDoc, PackageDoc packageDoc)
      throws Exception
  {

    LOG.debug(">");

    PDFDocument.newPage();
    State.increasePackageSection();

    State.setCurrentClass(classDoc.qualifiedName());
    State.setCurrentDoc(classDoc);
    LOG.info("..> " + State.getCurrentClass());

    // Simulate javadoc HTML layout
    // package (small) and class (large) name header
    Paragraph namePara = new Paragraph(packageDoc.name(),
                                       Fonts.getFont(TIMES_ROMAN, BOLD, 16));
    PDFDocument.add(namePara);

    Phrase linkPhrase = null;
    if (!classDoc.isInterface()) {
      linkPhrase = Destinations.createDestination("Class "
                                                  + classDoc.name(), classDoc,
                                                  Fonts.getFont(TIMES_ROMAN, BOLD, 16));
    }
    else {
      linkPhrase = Destinations.createDestination("Interface "
                                                  + classDoc.name(), classDoc,
                                                  Fonts.getFont(TIMES_ROMAN, BOLD, 16));
    }

    Paragraph titlePara = new Paragraph((float) 16.0, "");
    String classFileAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
    titlePara.add(PDFUtil.createAnchor(classFileAnchor, titlePara.getFont()));
    titlePara.add(linkPhrase);

    PDFDocument.instance().add(titlePara);

    // class derivation tree - build list first
    Hashtable list = new Hashtable();
    ClassDoc currentTreeClass = classDoc;
    ClassDoc superClass = null;
    ClassDoc subClass = null;

    Vector interfacesList = new Vector();

    while ((superClass = currentTreeClass.superclass()) != null) {
      if (!classDoc.isInterface()) {
        // Store interfaces implemented by superclasses
        // because the current class also implements all
        // interfaces of its superclass (by inheritance)
        ClassDoc[] interfaces = superClass.interfaces();

        for (int u = 0; u < interfaces.length; u++) {
          interfacesList.addElement(interfaces[u]);
        }
      }

      list.put(superClass, currentTreeClass);
      currentTreeClass = superClass;
    }

    // First line of derivation tree must NOT be printed, if it's
    // the only line, and it's an interface (not a class). This is
    // because a class ALWAYS has a superclass (if only java.lang.Object),
    // but an interface does not necessarily have a super instance.
    boolean firstLine = true;

    if (classDoc.isInterface() && (list.get(currentTreeClass) == null)) {
      firstLine = false;
    }

    // top-level-class
    String blanks = "";

    if (firstLine) {
      PDFDocument.add(new Paragraph((float) 24.0,
                                    currentTreeClass.qualifiedTypeName(), Fonts.getFont(COURIER, 10)));
    }

    while ((subClass = (ClassDoc) list.get(currentTreeClass)) != null) {
      blanks = blanks + "   ";
      PDFDocument.add(new Paragraph((float) 10.0, blanks + "|",
                                    Fonts.getFont(COURIER, 10)));

      if (list.get(subClass) == null) {
        // it's last in list, so use bold font
        PDFDocument.add(new Paragraph((float) 8.0,
                                      blanks + "+-" + subClass.qualifiedTypeName(),
                                      Fonts.getFont(COURIER, BOLD, 10)));
      }
      else {
        // If it's not last, it's a superclass. Create link to
        // it, if it's in same API.
        Paragraph newLine = new Paragraph((float) 8.0);
        newLine.add(new Chunk(blanks + "+-", Fonts.getFont(COURIER, 10)));
        newLine.add(new LinkPhrase(
            subClass.qualifiedTypeName(), null, 10, false));
        PDFDocument.add(newLine);
      }

      currentTreeClass = subClass;
    }

    ClassDoc[] interfaces = classDoc.interfaces();

    // Now, for classes only, print implemented interfaces
    // and known subclasses
    if (!classDoc.isInterface()) {
      // List All Implemented Interfaces
      if ((interfaces != null) && (interfaces.length > 0)) {
        for (int i = 0; i < interfaces.length; i++) {
          interfacesList.addElement(interfaces[i]);
        }
      }

      String[] interfacesNames = new String[interfacesList.size()];
      for (int i = 0; i < interfacesNames.length; i++) {
        interfacesNames[i] = ((ClassDoc) interfacesList.elementAt(i)).qualifiedTypeName();
      }
      if (interfacesNames.length > 0) {
        Implementors.print("All Implemented Interfaces:", interfacesNames);
      }

      // Known subclasses
      String[] knownSubclasses = ImplementorsInformation.getDirectSubclasses(State.getCurrentClass().toString());

      if ((knownSubclasses != null) && (knownSubclasses.length > 0)) {
        Implementors.print("Direct Known Subclasses:", knownSubclasses);
      }
    }
    else {
      // For interfaces, print superinterfaces and all subinterfaces
      // Known super-interfaces
      String[] knownSuperInterfaces = ImplementorsInformation.getKnownSuperclasses(State.getCurrentClass().toString());

      if ((knownSuperInterfaces != null)
          && (knownSuperInterfaces.length > 0)) {
        Implementors.print("All Superinterfaces:", knownSuperInterfaces);
      }

      // Known sub-interfaces
      String[] knownSubInterfaces = ImplementorsInformation.getKnownSubclasses(State.getCurrentClass().toString());

      if ((knownSubInterfaces != null)
          && (knownSubInterfaces.length > 0)) {
        Implementors.print("All Subinterfaces:", knownSubInterfaces);
      }

      // Known Implementing Classes
      String[] knownImplementingClasses = ImplementorsInformation.getImplementingClasses(State.getCurrentClass().toString());

      if ((knownImplementingClasses != null)
          && (knownImplementingClasses.length > 0)) {
        Implementors.print("All Known Implementing Classes:", knownImplementingClasses);
      }
    }

    // Horizontal line
    PDFUtil.printLine();

    // Class type / declaration
    LOG.debug("Print class type / declaration");
    String info = "";

    Tag[] deprecatedTags = classDoc.tags(DOC_TAGS_DEPRECATED);

    if (deprecatedTags.length > 0) {
      Paragraph classDeprecatedParagraph = new Paragraph((float) 20);

      Chunk deprecatedClassText = new Chunk(LB_DEPRECATED_TAG,
                                            Fonts.getFont(TIMES_ROMAN, BOLD, 12));
      classDeprecatedParagraph.add(deprecatedClassText);

      String depText = JavadocUtil.getComment(deprecatedTags[0].inlineTags());
      Element[] deprecatedInfoText = HtmlParserWrapper.createPdfObjects("<i>" + depText + "</i>");
      for (int n = 0; n < deprecatedInfoText.length; n++) {
        // Only phrases can be supported here (but no tables)
        if (deprecatedInfoText[n] instanceof Phrase) {
          classDeprecatedParagraph.add(deprecatedInfoText[n]);
        }
      }

      PDFDocument.add(classDeprecatedParagraph);
    }

    info = JavadocUtil.getClassModifiers(classDoc);

    Paragraph infoParagraph = new Paragraph((float) 20, info,
                                            Fonts.getFont(TIMES_ROMAN, 12));
    infoParagraph.add(new Chunk(classDoc.name(),
                                Fonts.getFont(TIMES_ROMAN, BOLD, 12)));
    PDFDocument.add(infoParagraph);

    // extends ...
    ClassDoc[] superClassOrInterface = null;

    if (classDoc.isInterface()) {
      superClassOrInterface = classDoc.interfaces();
    }
    else {
      superClassOrInterface = new ClassDoc[]{classDoc.superclass()};
    }

    if (superClassOrInterface != null) {
      Paragraph extendsPara = new Paragraph((float) 14.0);
      extendsPara.add(new Chunk("extends ", Fonts.getFont(TIMES_ROMAN, 12)));
      // Contrary to classes, interfaces DO support multiple inheritance,
      // so there could be more than one entry we're currently processing
      // an interface.
      for (int i = 0; i < superClassOrInterface.length; i++) {
        String superClassName
            = JavadocUtil.getQualifiedNameIfNecessary(superClassOrInterface[i]);
        extendsPara.add(new LinkPhrase(
            superClassOrInterface[i].qualifiedName(), superClassName, 12,
            true));
        // Add a comma if there are more interfaces to come
        if (i + 1 < superClassOrInterface.length) {
          extendsPara.add(new Chunk(", "));
        }
      }

      PDFDocument.add(extendsPara);
    }

    if (!classDoc.isInterface()) {
      //implements
      if ((interfaces != null) && (interfaces.length > 0)) {
        String[] interfacesNames = new String[interfacesList.size()];

        for (int i = 0; i < interfacesNames.length; i++) {
          interfacesNames[i] = ((ClassDoc) interfacesList.elementAt(i)).qualifiedTypeName();
        }

        Paragraph extendsPara = new Paragraph((float) 14.0);
        extendsPara.add(new Chunk("implements ", Fonts.getFont(TIMES_ROMAN, 12)));

        Paragraph descPg = new Paragraph((float) 24.0);

        for (int i = 0; i < interfacesNames.length; i++) {
          String subclassName = JavadocUtil.getQualifiedNameIfNecessary(interfacesNames[i]);
          Phrase subclassPhrase = new LinkPhrase(interfacesNames[i],
                                                 subclassName, 12, true);
          descPg.add(subclassPhrase);

          if (i < (interfacesNames.length - 1)) {
            descPg.add(new Chunk(", ", Fonts.getFont(COURIER, 10)));
          }
        }

        extendsPara.add(descPg);

        PDFDocument.add(extendsPara);
      }
    }

    PDFDocument.add(new Paragraph((float) 20.0, " "));

    // Description
    String classText = JavadocUtil.getComment(classDoc);
    Element[] objs = HtmlParserWrapper.createPdfObjects(classText);

    if (objs.length == 0) {
      String desc = Util.stripLineFeeds(classText);
      PDFDocument.add(new Paragraph((float) 14.0, desc,
                                    Fonts.getFont(TIMES_ROMAN, 12)));
    }
    else {
      PDFUtil.printPdfElements(objs);
    }

    TagLists.printClassTags(classDoc);
    // Horizontal line
    PDFUtil.printLine();

    if (Configuration.isShowSummaryActive()) {
      Summary.printAll(classDoc);
      PDFUtil.printLine();
    }

    float[] widths = {(float) 1.0, (float) 94.0};
    PdfPTable table = new PdfPTable(widths);
    table.setWidthPercentage((float) 100);

    // Some empty space...
    PDFDocument.add(new Paragraph((float) 6.0, " "));
    Members.printMembers(classDoc);

    LOG.debug("<");
  }

}

/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.tarsec.javadoc.pdfdoclet.filter.Filter;
import com.tarsec.javadoc.pdfdoclet.filter.FilteredRootDoc;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This javadoc doclet creates PDF output for an API documentation.
 * <p>
 * Please note that this doclet is a very old-fashioned, straightforward
 * batch-process application. It holds the current state of the process in
 * static variables which also means that it is definitely NOT thread-safe.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class PDFDoclet implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(PDFDoclet.class);

  /**
   * Default name for output file.
   */
  private final static String DEFAULT_FILENAME = "api.pdf";

  /**
   * Index generation class reference.
   */
  private static Index index = null;

  /**
   * Reference to the PDF file.
   */
  private static File file = null;

  // Stores list of inner classes
  private static Hashtable innerClassesList = new Hashtable();

  /**
   * Constructor for PDFDoclet.
   */
  public PDFDoclet()
  {
    super();
    LOG.debug("Creating PDFDoclet");
  }

  /**
   * Constructs a PDFDoclet object.
   *
   * @param filename The filename of the target otuput PDF file.
   */
  public PDFDoclet(String filename)
  {
    LOG.debug("Creating PDFDoclet with output file " + filename);

    String workDir = Configuration.getWorkDir();
    boolean relative = false;
    if ((filename.startsWith(".") || filename.startsWith("..")) && (workDir != null)) {
      relative = true;
    }
    if (filename.indexOf(File.separator) != -1) {
      String dirname = filename.substring(0,
                                          filename.lastIndexOf(File.separator));
      File dir = (!relative ? new File(dirname) : new File(workDir, dirname));
      dir.mkdirs();
    }

    file = (!relative ? new File(filename) : new File(workDir, filename));

    LOG.debug("Filename set to: " + file.getAbsolutePath());
  }

  /**
   * Processes all packages of a given javadoc root.
   *
   * @param root The javadoc root.
   * @throws Exception
   */
  private void listClasses(RootDoc root) throws Exception
  {

    LOG.debug(">");

    PdfWriter pdfWriter = null;
    try {
      LOG.debug("Initializing PDF document...");
      pdfWriter = PDFDocument.initialize();
      Bookmarks.init();
    }
    catch (IOException e) {
      LOG.error("**** ERROR: FAILED TO OPEN PDF FILE FOR WRITING! *****");
      LOG.error("**** PLEASE CHECK IF THE FILE IS CURRENTLY USED  *****");
      LOG.error("**** BY ANOTHER PROGRAM (ACROBAT READER ETC.     *****");
      return;
    }

    // Prepare index
    index = new Index(pdfWriter, PDFDocument.instance());

    // Print title page
    LOG.debug("print Title page...");
    TitlePage title = new TitlePage(PDFDocument.instance());
    title.print();
    PDFDocument.instance().newPage();

    // Print javadoc overview
    LOG.debug("print Overview...");
    State.setCurrentHeaderType(HEADER_OVERVIEW);
    Overview.print(root);

    State.setCurrentHeaderType(HEADER_API);

    LOG.debug("print classes...");
    ClassDoc[] classes = root.classes();
    LOG.debug("Number of classes: " + classes.length);

    Map pkgMap = null;
    if (Configuration.getPackageOrder() != null) {

      // Use a custom comparator to sort the list
      // of packages in a custom way 
      Comparator cmp = new Comparator()
      {

        @Override
        public int compare(Object packageName1, Object packageName2)
        {
          String packageOrder = Configuration.getPackageOrder();
          PackageDoc package1 = (PackageDoc) packageName1;
          PackageDoc package2 = (PackageDoc) packageName2;
          int retval1 = packageOrder.indexOf((String) package1.name());
          int retval2 = packageOrder.indexOf((String) package2.name());
          if (retval1 < retval2) {
            return -1;
          }
          if (retval1 > retval2) {
            return 1;
          }
          return 0;
        }
      };
      pkgMap = new TreeMap(cmp);
      LOG.debug("Custom package order specified.");

    }
    else {
      // Use a treemap to create an alphabetically
      // sorted list of all packages
      pkgMap = new TreeMap();
      LOG.debug("No package order specified, use alphabetical order.");
    }

    // Iterate through all single, separately specified classes
    LOG.debug("Iterating through single classes...");
    for (int i = 0; i < classes.length; i++) {
      // Fetch the classes list for the package of this class
      List classList = (List) pkgMap.get(classes[i].containingPackage());
      if (classList == null) {
        // If there's no list for this package yet, create one
        classList = new ArrayList();
        pkgMap.put(classes[i].containingPackage(), classList);
      }
      // Store class in the list for this package
      classList.add(classes[i]);
    }

    // At this point we have a collection of packages. For each package,
    // we have a list of all classes that should be processed.
    LOG.debug("Size of package collection: " + pkgMap.size());

    // Prepare alphabetically sorted list of all classes for bookmarks
    Bookmarks.prepareBookmarkEntries(pkgMap);

    // Now process all packages and classes
    LOG.debug("Iterating through package list...");
    for (Iterator i = pkgMap.entrySet().iterator(); i.hasNext();) {
      // Get package..
      Map.Entry entry = (Map.Entry) i.next();
      PackageDoc pkgDoc = (PackageDoc) entry.getKey();
      List pkgList = (List) entry.getValue();
      // Get list of classes in package...
      ClassDoc[] pkgClasses = (ClassDoc[]) pkgList.toArray(new ClassDoc[pkgList.size()]);
      State.increasePackageChapter();
      // Print package info (includes printing classes info)
      printPackage(pkgDoc, pkgClasses);
    }

    LOG.debug("Adding appendices");
    Appendices.print();

    LOG.debug("Creating index");
    index.create();

    LOG.debug("Content completed, creating bookmark outline tree");
    Bookmarks.createBookmarkOutline();

    String endMessage = "PDF completed: " + file.getPath();
    String line = Util.getLine(endMessage.length());
    LOG.info(line);
    LOG.info(endMessage);
    LOG.info(line);

    // step 5: we close the document
    PDFDocument.close();
  }

  /**
   * Processes one Java package of the whole API.
   *
   * @param packageDoc The javadoc information for the package.
   * @throws Exception
   */
  private void printPackage(PackageDoc packageDoc, ClassDoc[] packageClasses)
      throws Exception
  {

    LOG.debug(">");

    State.setCurrentPackage(packageDoc.name());
    State.setCurrentDoc(packageDoc);

    PDFDocument.newPage();

    String packageName = State.getCurrentPackage().toString();

    // Text "package"
    //State.setCurrentClass("");

    Paragraph label = new Paragraph((float) 22.0, LB_PACKAGE,
                                    Fonts.getFont(TIMES_ROMAN, BOLD, 18));
    PDFDocument.add(label);

    Paragraph titlePara = new Paragraph((float) 30.0, "");
    // Name of the package (large font)
    Chunk titleChunk = new Chunk(packageName, Fonts.getFont(TIMES_ROMAN, BOLD, 30));
    titleChunk.setLocalDestination(packageName);
    if (State.getCurrentFile() != null) {
      String packageAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
      titlePara.add(PDFUtil.createAnchor(packageAnchor, titleChunk.getFont()));
    }
    titlePara.add(titleChunk);
    PDFDocument.add(titlePara);

    // Some empty space
    PDFDocument.add(new Paragraph((float) 20.0, " "));

    State.setContinued(true);

    String packageText = JavadocUtil.getComment(packageDoc);
    Element[] objs = HtmlParserWrapper.createPdfObjects(packageText);

    if (objs.length == 0) {
      String packageDesc = Util.stripLineFeeds(packageText);
      PDFDocument.add(new Paragraph((float) 11.0, packageDesc,
                                    Fonts.getFont(TIMES_ROMAN, 10)));
    }
    else {
      PDFUtil.printPdfElements(objs);
    }

    State.setContinued(false);

    State.increasePackageSection();

    printClasses(JavadocUtil.sort(packageClasses), packageDoc);

    LOG.debug("<");
  }

  /**
   * Processes all classes of one Java package..
   *
   * @param classDocs The javadoc information list for the classes.
   * @param packageDoc The javadoc information for the package which the classes
   * belong to.
   * @throws Exception
   */
  private void printClasses(ClassDoc[] classDocs, PackageDoc packageDoc)
      throws Exception
  {
    LOG.debug(">");
    for (int i = 0; i < classDocs.length; i++) {

      // Avoid processing a class which has already been
      // processed as an inner class
      if (innerClassesList.get(classDocs[i]) == null) {
        ClassDoc doc = (ClassDoc) classDocs[i];
        printClassWithInnerClasses(doc, packageDoc, false);
      }
    }
    LOG.debug("<");
  }

  /**
   * This method prints the doc of a class and of all its inner classes. It
   * calls itself recursively to make sure that also nested inner classes are
   * printed correctly.
   *
   * @param classDoc The doc of the class to print.
   * @param packageDoc The packagedoc of the containing package.
   * @throws Exception If something failed.
   */
  private void printClassWithInnerClasses(ClassDoc doc, PackageDoc packageDoc,
                                          boolean isInnerClass)
      throws Exception
  {

    if (isInnerClass) {
      LOG.debug("Print inner class: " + doc.name());
    }
    else {
      LOG.debug("Print class: " + doc.name());
    }
    //Classes.printClass(doc, packageDoc);

    ClassDoc[] innerClasses = doc.innerClasses();
    if (innerClasses != null && innerClasses.length > 0) {
      for (int u = 0; u < innerClasses.length; u++) {
        // Check if this inner class has not yet been handled
        if (innerClassesList.get(innerClasses[u]) == null) {
          innerClassesList.put(innerClasses[u], "x");
          State.setInnerClass(true);
          ClassDoc innerDoc = (ClassDoc) innerClasses[u];
          printClassWithInnerClasses(innerDoc, packageDoc, true);
          State.setInnerClass(false);
        }
      }
    }
  }

  /**
   * Main doclet method.
   *
   * @param root The root of the javadoc information.
   * @return True if the javadoc generation was successful, false if it failed.
   */
  public static boolean start(RootDoc rootDoc)
  {
    try {

      FilteredRootDoc root = new FilteredRootDoc(rootDoc);

      Configuration.start(root);
      Filter.initialize();
      TagList.initialize();
      Appendices.initialize();
      String outputFilename = Configuration.getProperty(ARG_PDF, ARG_VAL_PDF);

      // Prepare list of classes and packages
      JavadocUtil.buildPackageList(root);

      // Do some pre-processing first (building class
      // derivation trees)
      ImplementorsInformation.initialize(root);
      ImplementorsInformation.collectInformation();

      PDFDoclet doclet = new PDFDoclet(outputFilename);

      if (root.classes() != null) {
        doclet.listClasses(root);
      }
      else {
        Util.error("** NO CLASSES AVAILABLE **");
      }

      return true;

    }
    catch (Throwable e) {

      Util.error("Exception: " + e);
      e.printStackTrace();

      return false;
    }
  }

  /**
   * Doclet method called by Javadoc to recognize custom parameters.
   *
   * @param option The parameter found in the command line
   * @return Zero (0) if the parameter is unknown, or the number of parts that
   * make up the whole parameter.
   */
  public static int optionLength(String option)
  {
    System.out.println("OPTION: " + option);

    if (option.equals("-" + ARG_WORKDIR)) {
      return 2;
    }

    if (option.equals("-" + ARG_SOURCEPATH)) {
      return 2;
    }

    if (option.equals("-" + ARG_PDF)) {
      return 2;
    }

    if (option.equals("-" + ARG_CONFIG)) {
      return 2;
    }

    if (option.equals("-" + ARG_DEBUG)) {
      return 1;
    }

    if (option.equals("-" + ARG_AUTHOR)) {
      return 1;
    }

    if (option.equals("-" + ARG_VERSION)) {
      return 1;
    }

    if (option.equals("-" + ARG_GROUP)) {
      LOG.debug("GROUP PARAMETER");
      return 3;
    }

    if (option.equals("-" + ARG_DONTSPEC)) {
      return 2;
    }

    if (option.equals("-" + ARG_SORT)) {
      return 2;
    }

    if (option.equals("-" + ARG_SINCE)) {
      return 1;
    }

    if (option.equals("-" + ARG_SUMMARY_TABLE)) {
      return 1;
    }

    if (option.equals("-" + ARG_CREATE_LINKS)) {
      return 1;
    }

    if (option.equals("-" + ARG_ENCRYPTED)) {
      return 1;
    }

    if (option.equals("-" + ARG_ALLOW_PRINTING)) {
      return 1;
    }

    if (option.equals("-" + ARG_HEADER_LEFT)) {
      return 2;
    }

    if (option.equals("-" + ARG_HEADER_CENTER)) {
      return 2;
    }

    if (option.equals("-" + ARG_HEADER_RIGHT)) {
      return 2;
    }

    if (option.equals("-" + ARG_PGN_TYPE)) {
      return 2;
    }

    if (option.equals("-" + ARG_PGN_ALIGNMENT)) {
      return 2;
    }

    if (option.equals("-" + ARG_CREATE_FRAME)) {
      return 1;
    }

    if (option.equals("-" + ARG_API_TITLE_PAGE)) {
      return 1;
    }

    if (option.equals("-" + ARG_API_TITLE_FILE)) {
      return 2;
    }

    if (option.equals("-" + ARG_API_TITLE)) {
      return 2;
    }

    if (option.equals("-" + ARG_API_AUTHOR)) {
      return 2;
    }

    if (option.equals("-" + ARG_API_COPYRIGHT)) {
      return 2;
    }

    if (option.equals("-" + ARG_FONT_TEXT_NAME)) {
      return 2;
    }
    if (option.equals("-" + ARG_FONT_TEXT_ENC)) {
      return 2;
    }
    if (option.equals("-" + ARG_FONT_CODE_NAME)) {
      return 2;
    }
    if (option.equals("-" + ARG_FONT_CODE_ENC)) {
      return 2;
    }

    if (option.startsWith("-" + ARG_APPENDIX_PREFIX)) {
      return 2;
    }

    return 0;
  }

  /**
   * Returns a reference to the Index object.
   *
   * @return The index object.
   */
  public static Index getIndex()
  {
    return index;
  }

  /**
   * Returns a reference to the PDF file object.
   *
   * @return The PDF file object.
   */
  public static File getPdfFile()
  {
    return file;
  }
}

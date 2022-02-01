/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import com.tarsec.javadoc.pdfdoclet.builder.WriterFactory;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import com.tarsec.javadoc.pdfdoclet.util.Utils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.tarsec.javadoc.pdfdoclet.IConstants.*;

/**
 *
 * @author mdo
 */
public class PDFDocletNew implements Doclet
{
  private static final Logger LOG = LoggerFactory.getLogger(PDFDocletNew.class);

  /**
   * Reference to the PDF file.
   */
  private Path pdfOutputFile = Paths.get("/tmp/test.pdf");

  private Reporter reporter;

  // Stores list of inner classes
  private Map innerClassesList = new HashMap();
  
  private WriterFactory factory;

  
  @Override
  public String getName()
  {
    return getClass().getSimpleName();
  }

  @Override
  public SourceVersion getSupportedSourceVersion()
  {
    return SourceVersion.latest();
  }

  @Override
  public void init(Locale locale, Reporter reporter)
  {
    this.reporter = reporter;
  }

  @Override
  public boolean run(DocletEnvironment environment)
  {
    factory = new WriterFactory(environment);
    
    DocTrees docTrees = environment.getDocTrees();

    PdfWriter pdfWriter = null;
    Document pdfDocument = createPdfDocument();

    PDFDocument.setInstance(pdfDocument);
    
    try {
      pdfWriter = PdfWriter.getInstance(pdfDocument, Files.newOutputStream(pdfOutputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
      pdfDocument.open();

      pdfWriter.setPageEvent(new PageEventHandler(pdfWriter));

      // Bookmarks.init();
      // Prepare index
      Index index = new Index(pdfWriter, pdfDocument);

      reporter.print(Diagnostic.Kind.NOTE, "Prepare title page");
      TitlePage title = new TitlePage(pdfDocument);
      title.print();
      pdfDocument.newPage();

      // Print javadoc overview
      reporter.print(Diagnostic.Kind.NOTE, "Print Overview...");
      State.setCurrentHeaderType(IConstants.HEADER_OVERVIEW);
      //Overview.print(root);

      State.setCurrentHeaderType(IConstants.HEADER_API);

      reporter.print(Diagnostic.Kind.NOTE, "Print types...");
      List<TypeElement> types = getTypes(environment);
      reporter.print(Diagnostic.Kind.NOTE, "Number of types: " + types.size());

      // Iterate through all single, separately specified classes
      reporter.print(Diagnostic.Kind.NOTE, "Iterating through single classes...");
      Map<PackageElement, List<TypeElement>> packageToTypeMap = types.stream().collect(Collectors.groupingBy(Utils::getPackage));

      // At this point we have a collection of packages. For each package,
      // we have a list of all classes that should be processed.
      reporter.print(Diagnostic.Kind.NOTE, "Size of package collection: " + packageToTypeMap.size());

      // Prepare alphabetically sorted list of all classes for bookmarks
      //Bookmarks.prepareBookmarkEntries(packageToTypeMap);

      // Now process all packages and classes
      reporter.print(Diagnostic.Kind.NOTE, "Iterating through package list...");
      for (Map.Entry<PackageElement, List<TypeElement>> entry : packageToTypeMap.entrySet()) {
        PackageElement pkgDoc = entry.getKey();
        List<TypeElement> pkgList = entry.getValue();

        State.increasePackageChapter();
        printPackage(pdfDocument, pkgDoc, pkgList, docTrees);
      }

      reporter.print(Diagnostic.Kind.NOTE, "Adding appendices");
      Appendices.print();

      reporter.print(Diagnostic.Kind.NOTE, "Creating index");
      index.create();

      reporter.print(Diagnostic.Kind.NOTE, "Content completed, creating bookmark outline tree");
      //Bookmarks.createBookmarkOutline();
    }
    catch (Exception ex) {
      reporter.print(Diagnostic.Kind.ERROR, ex.getMessage());
      ex.printStackTrace();
      return false;
    }
    finally {
      pdfDocument.close();
      pdfWriter.flush();
      pdfWriter.close();
    }

    return true;
  }

  /**
   * Processes one Java package of the whole API.
   *
   * @param packageDoc The javadoc information for the package.
   * @throws Exception
   */
  private void printPackage(Document pdfDocument, PackageElement packageDoc, List<TypeElement> types, DocTrees docTrees) throws Exception
  {
    //State.setCurrentPackage(packageDoc.getSimpleName().toString());
    //State.setCurrentDoc(packageDoc);

    pdfDocument.newPage();

    String packageName = packageDoc.getSimpleName().toString();

    // Text "package"
    //State.setCurrentClass("");
    Paragraph label = new Paragraph((float) 22.0, IConstants.LB_PACKAGE, Fonts.getFont(IConstants.TIMES_ROMAN, BOLD, 18));
    pdfDocument.add(label);

    Paragraph titlePara = new Paragraph((float) 30.0, "");
    // Name of the package (large font)
    Chunk titleChunk = new Chunk(packageName, Fonts.getFont(IConstants.TIMES_ROMAN, BOLD, 30));
    titleChunk.setLocalDestination(packageName);
//    if (State.getCurrentFile() != null) {
//      String packageAnchor = Destinations.createAnchorDestination(State.getCurrentFile(), null);
//      titlePara.add(PDFUtil.createAnchor(packageAnchor, titleChunk.font()));
//    }
    titlePara.add(titleChunk);
    pdfDocument.add(titlePara);

    // Some empty space
    pdfDocument.add(new Paragraph((float) 20.0, " "));

    State.setContinued(true);

    String packageText = getPackageComment(docTrees, packageDoc);
    com.itextpdf.text.Element[] objs = HtmlParserWrapper.createPdfObjects(packageText);

    if (objs.length == 0) {
      String packageDesc = Util.stripLineFeeds(packageText);
      pdfDocument.add(new Paragraph((float) 11.0, packageDesc,
                                    Fonts.getFont(IConstants.TIMES_ROMAN, 10)));
    }
    else {
      PDFUtil.printPdfElements(objs);
    }

    State.setContinued(false);

    State.increasePackageSection();

    printClasses(Utils.createSorted(types), packageDoc);
  }
  
  
    /**
   * Processes all classes of one Java package..
   *
   * @param classDocs The javadoc information list for the classes.
   * @param packageDoc The javadoc information for the package which the classes
   * belong to.
   * @throws Exception
   */
  private void printClasses(List<TypeElement> classDocs, PackageElement packageDoc)
      throws Exception
  {
    LOG.debug(">");
    for (int i = 0; i < classDocs.size(); i++) {

      // Avoid processing a class which has already been
      // processed as an inner class
      if (innerClassesList.get(classDocs.get(i)) == null) {
        TypeElement doc = classDocs.get(i);
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
  private void printClassWithInnerClasses(TypeElement doc, PackageElement packageDoc, boolean isInnerClass)
      throws Exception
  {

    if (isInnerClass) {
      LOG.debug("Print inner class: " + doc.getSimpleName());
    }
    else {
      LOG.debug("Print class: " + doc.getSimpleName());
    }
    
    factory.createClassParagraph(doc).appendTo(PDFDocument.instance());
   
    
   

    List<? extends Element> innerClasses = doc.getEnclosedElements();
    
    for (TypeElement type : ElementFilter.typesIn(doc.getEnclosedElements())) {
      // Check if this inner class has not yet been handled
      if (innerClassesList.get(type) == null) {
        innerClassesList.put(type, "x");
        State.setInnerClass(true);
        printClassWithInnerClasses(type, packageDoc, true);
        State.setInnerClass(false);
      }
    }
  }

  private String getPackageComment(DocTrees docTrees, PackageElement pkg)
  {
    DocCommentTree comment = docTrees.getDocCommentTree(pkg);
    if (comment == null) {
      return "";
    }
    List<? extends DocTree> fullBody = comment.getFullBody();
    for (DocTree docTree : fullBody) {
      System.out.println("Type: " + docTree.getKind() + " -> " + docTree.toString());
    }
    return "bla";
  }

  private List<TypeElement> getTypes(DocletEnvironment environment)
  {
    List<TypeElement> types = new ArrayList<>();

    for (TypeElement t : ElementFilter.typesIn(environment.getIncludedElements())) {
      types.add(t);
    }

    return types;
  }

  private Map<PackageElement, List<TypeElement>> createPackageToTypeMap(List<TypeElement> types)
  {
    return types.stream().collect(Collectors.groupingBy(Utils::getPackage));
  }

  private Document createPdfDocument()
  {
    Document pdfDocument = new Document();
    pdfDocument.setPageSize(Configuration.getPageSize());
    pdfDocument.setMargins(LEFT_MARGIN_WIDTH, RIGHT_MARGIN_WIDTH, TOP_MARGIN_WIDTH, BOTTOM_MARGIN_WIDTH);
    //pdfDocument.open();

    return pdfDocument;
  }

  public void printElement(DocTrees trees, Element e)
  {
    DocCommentTree docCommentTree = trees.getDocCommentTree(e);
    if (docCommentTree != null) {
      System.out.println("Element (" + e.getKind() + ": "
                         + e + ") has the following comments:");
      System.out.println("Entire body: " + docCommentTree.getFullBody());
      System.out.println("Block tags: " + docCommentTree.getBlockTags());
    }
  }

  @Override
  public Set<? extends Option> getSupportedOptions()
  {
    return Set.of(optPdfOutputFile);
  }

  private final Option optPdfOutputFile = new PdfDocletOption("--pdf", true, "PDF output file name", "<path>")
  {
    @Override
    public boolean process(String option, List<String> arguments)
    {
      pdfOutputFile = Paths.get(arguments.get(0));
      return true;
    }
  };

  /**
   * A base class for declaring options. Subtypes for specific options should
   * implement the {@link #process(String,List) process} method to handle
   * instances of the option found on the command line.
   */
  private abstract class PdfDocletOption implements Doclet.Option
  {

    private final String name;
    private final boolean hasArg;
    private final String description;
    private final String parameters;

    PdfDocletOption(String name, boolean hasArg, String description, String parameters)
    {
      this.name = name;
      this.hasArg = hasArg;
      this.description = description;
      this.parameters = parameters;
    }

    @Override
    public int getArgumentCount()
    {
      return hasArg ? 1 : 0;
    }

    @Override
    public String getDescription()
    {
      return description;
    }

    @Override
    public Kind getKind()
    {
      return Kind.STANDARD;
    }

    @Override
    public List<String> getNames()
    {
      return List.of(name);
    }

    @Override
    public String getParameters()
    {
      return hasArg ? parameters : "";
    }
  }
  
  
  public static void main(String... args)
  {
    String[] docletArgs = new String[]{
        "-doclet", PDFDocletNew.class.getName(),
        "-docletpath", "/home/mdo/Dev/MIS/Japi/1_general/trunk/schiller-general-layer/commons/commons-core/target/", 
        "-sourcepath", "/home/mdo/Dev/MIS/Japi/1_general/trunk/schiller-general-layer/commons/commons-core/src/main/java/", 
        "-classpath", "/home/mdo/.m2/repository/org/slf4j/slf4j-api/1.7.35/slf4j-api-1.7.35.jar:/home/mdo/.m2/repository/org/checkerframework/checker-qual/3.21.1/checker-qual-3.21.1.jar:/home/mdo/.m2/repository/org/openjfx/javafx-base/17.0.2/javafx-base-17.0.2-linux.jar",
        "ch.schiller.japi.commons.io"
    };
    DocumentationTool docTool = ToolProvider.getSystemDocumentationTool();
    docTool.run(System.in, System.out, System.err, docletArgs);
  }
}

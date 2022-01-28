/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/Bookmarks.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;

/**
 * This class creates the bookmarks frame.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class Bookmarks implements IConstants
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(Bookmarks.class);

  /**
   * Stores a reference to the root PDF outline object.
   */
  private static PdfOutline rootOutline = null;

  /**
   * Stores a reference to the root node of the bookmark tree.
   */
  private static BookmarkEntry rootEntry = null;

  private static TreeMap alphabeticalClassList = new TreeMap();

  /**
   * Use this to store the bookmark entries for classes for re-using them in
   * different branches of the tree
   */
  private static TreeMap classBookmarks = new TreeMap();

  /**
   * Use this to store the bookmark entries for packages for re-using them in
   * different branches of the tree
   */
  private static TreeMap packagesBookmarks = new TreeMap();

  /**
   * This hashtable stores all packages for which the bookmark entry already has
   * been connected in the outline.
   */
  private static Hashtable usedPackages = new Hashtable();

  /**
   * Initializes the bookmarks creation.
   */
  public static void init()
  {
    PdfWriter writer = PDFDocument.getWriter();
    rootOutline = writer.getRootOutline();
    rootEntry = new BookmarkEntry();
  }

  /**
   * Creates the tree branches for the classes and packages bookmarks.
   *
   * @param packageList The list of packages processed by javadoc.
   */
  public static void prepareBookmarkEntries(Map packagesList)
  {
    for (Iterator i = packagesList.entrySet().iterator(); i.hasNext();) {
      // Get package..
      Map.Entry entry = (Map.Entry) i.next();
      List pkgList = (List) entry.getValue();
      // Get list of classes in package...
      ClassDoc[] pkgClasses = (ClassDoc[]) pkgList.toArray(new ClassDoc[pkgList.size()]);
      for (int no = 0; no < pkgClasses.length; no++) {
        alphabeticalClassList.put(pkgClasses[no].name(), pkgClasses[no]);
      }
    }

    // Now we have a list of packages and a list of classes.
    createClassesBookmarks(alphabeticalClassList);

    createPackagesBookmarks(packagesList);
  }

  /**
   * Creates all bookmark entries for all classes.
   *
   * @param classesList The alphabetically sorted list of classes.
   */
  private static void createClassesBookmarks(TreeMap classesList)
  {
    log.debug(">");

    log.debug("Create all classes bookmarks");

    // Prepare bookmark entries for all classes
    String labelClasses = Configuration.getProperty(ARG_LB_OUTLINE_CLASSES, LB_CLASSES);
    BookmarkEntry bookmarkClasses = Bookmarks.addStaticRootBookmark(labelClasses);

    Iterator iter = classesList.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      ClassDoc doc = (ClassDoc) entry.getValue();
      BookmarkEntry classBookmark = Bookmarks.addClassBookmark(bookmarkClasses, doc);
      classBookmarks.put(doc.qualifiedName(), classBookmark);
    }
    log.debug("<");
  }

  /**
   * Creates all bookmark entries for all packages.
   *
   * @param packagesList The list of all packages.
   */
  private static void createPackagesBookmarks(Map packagesList)
  {

    log.debug(">");

    log.debug("Create all packages bookmarks");

    Iterator iter = packagesList.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      PackageDoc doc = (PackageDoc) entry.getKey();
      String name = doc.name();
      log.debug("Create Bookmark Entry for Package: " + name);
      BookmarkEntry packageBookmark = new BookmarkEntry(name, name);

      // Now create sub-entries for classes within that package
      ClassDoc[] classes = doc.allClasses();
      // Sort list of classes within a package alphabetically
      Map pkgMap = new TreeMap();
      for (int u = 0; u < classes.length; u++) {
        pkgMap.put(classes[u].qualifiedName(), classes[u]);
      }
      // Now iterate alphabetically
      for (Iterator i = pkgMap.keySet().iterator(); i.hasNext();) {
        String className = (String) i.next();
        BookmarkEntry classEntry = (BookmarkEntry) classBookmarks.get(className);
        // If filtering is active, no entry may exist for this class, so check for null
        if (classEntry != null) {
          packageBookmark.addChild(classEntry);
        }
      }
      packagesBookmarks.put(doc.name(), packageBookmark);
    }

    // Now connect package bookmarks according to default
    // or -group parameters
    Properties groups = Configuration.getGroups();
    // TODO: REPLACE HARDOCDED LABEL
    String defaultGroup = "Packages";
    if (groups.size() != 0) {
      defaultGroup = "Other Packages";
      Enumeration groupNames = groups.keys();
      while (groupNames.hasMoreElements()) {
        String groupName = (String) groupNames.nextElement();
        String groupList = groups.getProperty(groupName);
        connectPackages(groupName, groupList);
      }
      connectPackages(defaultGroup, "*");
    }
    else {
      connectPackages(defaultGroup, "*");
    }

    // Now create rest of bookmarks
    log.debug("<");
  }

  /**
   * Connects the previously created package bookmark entries of a group with
   * the outline tree.
   *
   * @param groupName The name of the group
   * @param list The list of packages as defined by the -group parameter; "*"
   * means all packages.
   */
  private static void connectPackages(String groupName, String list)
  {
    BookmarkEntry bookmarkPackages = null;
    log.debug("Group name: " + groupName + ", list: " + list);
    if (list.equals("*")) {

      // No group defined, use one package entry
      // If there have been -group parameters, the root entry for all
      // packages not in any group must be "Other Packages". If no -group
      // parameters have been specified, the root entry for all packages
      // is just "Packages".
      String labelPackages = null;
      if (Configuration.getGroups().size() > 0) {
        labelPackages = Configuration.getProperty(ARG_LB_OUTLINE_OTHERPACKAGES,
                                                  LB_OTHERPACKAGES);
      }
      else {
        labelPackages = Configuration.getProperty(ARG_LB_OUTLINE_PACKAGES, LB_PACKAGES);
      }
      bookmarkPackages = new BookmarkEntry(labelPackages, null);

      Iterator packageNames = packagesBookmarks.keySet().iterator();
      while (packageNames.hasNext()) {
        String name = (String) packageNames.next();
        if (usedPackages.get(name) == null) {
          BookmarkEntry packageBookmark = (BookmarkEntry) packagesBookmarks.get(name);
          bookmarkPackages.addChild(packageBookmark);
        }
      }
      if (bookmarkPackages.getChildren().length > 0) {
        rootEntry.addChild(bookmarkPackages);
      }

    }
    else {

      bookmarkPackages = Bookmarks.addStaticRootBookmark(groupName);
      // parse list of packages for group
      StringTokenizer tok = new StringTokenizer(list, ":");
      while (tok.hasMoreTokens()) {
        String token = tok.nextToken();
        boolean matchStartsWith = false;
        // Check if exact package name is specified or with wildcard
        if (token.endsWith("*")) {
          matchStartsWith = true;
        }
        Iterator packageNames = packagesBookmarks.keySet().iterator();
        while (packageNames.hasNext()) {
          boolean addPackage = false;
          String name = (String) packageNames.next();
          if (matchStartsWith) {
            if (name.startsWith(token.substring(0, token.length() - 1))) {
              addPackage = true;
            }
          }
          else {
            if (name.equals(token)) {
              addPackage = true;
            }
          }
          log.debug("add package: " + addPackage);
          if (addPackage) {
            BookmarkEntry packageBookmark = (BookmarkEntry) packagesBookmarks.get(name);
            bookmarkPackages.addChild(packageBookmark);
            // Store in list of used packages, so that its not used
            // again when the rest of the packages (not included in any group)
            // is added to the outline.
            usedPackages.put(name, packageBookmark);
          }
        }
      }
    }
    log.debug("<");
  }

  /**
   * Finally creates the entire outline tree with all bookmarks in the PDF
   * document.
   */
  public static void createBookmarkOutline()
  {
    log.debug(">");
    createBookmarks(rootOutline, rootEntry.getChildren());
    log.debug("<");
  }

  /**
   * Creates entries for all the given bookmark entry objects. If any of them
   * has child nodes, the method calls itself recursively to process them as
   * well.
   *
   * @param parent The parent PDF outline object.
   * @param entries The bookmark entries for which to add outline objects.
   */
  private static void createBookmarks(PdfOutline parent, BookmarkEntry[] entries)
  {
    log.debug(">");
    if (entries == null) {
      return;
    }
    for (int i = 0; i < entries.length; i++) {
      String name = entries[i].getDestinationName();
      log.debug("Entry name: " + name);
      PdfAction action = null;
      if (name == null) {
        action = new PdfAction();
      }
      else {
        action = PdfAction.gotoLocalPage(name, false);
      }
      PdfOutline outline = new PdfOutline(parent, action, entries[i].getLabel());
      outline.setOpen(false);
      createBookmarks(outline, entries[i].getChildren());
    }
    log.debug("<");
  }

  /**
   * Adds a bookmark entry which will be in the root of the bookmark outline.
   *
   * @param label The label for the entry.
   * @param dest The named destination to which the entry points.
   * @return The newly created bookmark entry object.
   */
  public static BookmarkEntry addRootBookmark(String label, String dest)
  {
    BookmarkEntry entry = new BookmarkEntry(label, dest);
    rootEntry.addChild(entry);
    return entry;
  }

  /**
   * Adds a bookmark entry which will be in the root of the bookmark outline.
   * The entry will be static, meaning that a click on it will not let the PDF
   * viewer jump to any position in the document. This can be used to create
   * parent nodes which don't have any sensible target in the document by
   * themselves.
   *
   * @param label The label for this entry.
   * @param dest The named destination to which the entry points.
   * @return The newly created bookmark entry object.
   */
  public static BookmarkEntry addStaticRootBookmark(String label)
  {
    BookmarkEntry entry = new BookmarkEntry(label, null);
    rootEntry.addChild(entry);
    return entry;
  }

  /**
   * Adds a bookmark entry which will become a child node of a given parent
   * entry.
   *
   * @param parent The parent bookmark entry.
   * @param label The label for this entry.
   * @param dest The named destination to which the entry points.
   * @return The newly created bookmark entry object.
   */
  public static BookmarkEntry addSubBookmark(BookmarkEntry parent, String label, String dest)
  {
    BookmarkEntry entry = new BookmarkEntry(label, dest);
    parent.addChild(entry);
    return entry;
  }

  /**
   * Creates a bookmark entry for a class and adds it to a parent bookmark
   * entry.
   *
   * @param parent The parent bookmark entry.
   * @param classDoc The doc of the class.
   * @return The newly created class bookmark entry with all member subentries.
   */
  public static BookmarkEntry addClassBookmark(BookmarkEntry parent, ClassDoc classDoc)
  {
    // Create class entry
    String dest = classDoc.qualifiedName();
    String label = classDoc.name();
    BookmarkEntry entry = new BookmarkEntry(label, dest);
    parent.addChild(entry);

    // Create sub-entries for fields, constructors and methods
    FieldDoc[] fieldDocs = classDoc.fields();
    if (fieldDocs != null && fieldDocs.length > 0) {
      BookmarkEntry fields = new BookmarkEntry("Fields", null);
      entry.addChild(fields);
      addMemberEntries(fields, classDoc.fields(), false);
    }

    ConstructorDoc[] constructorDocs = classDoc.constructors();
    if (!classDoc.isInterface() && constructorDocs != null && constructorDocs.length > 0) {
      BookmarkEntry constructors = new BookmarkEntry("Constructors", null);
      entry.addChild(constructors);
      addMemberEntries(constructors, classDoc.constructors(), true);
    }

    MethodDoc[] methodDocs = classDoc.methods();
    if (methodDocs != null && methodDocs.length > 0) {
      BookmarkEntry methods = new BookmarkEntry("Methods", null);
      entry.addChild(methods);
      addMemberEntries(methods, classDoc.methods(), true);
    }

    return entry;
  }

  /**
   * Adds the bookmark entries of a set of members of a class (like all methods,
   * all fields or all constructors) to the bookmark entry of the class.
   *
   * @param parent The bookmark entry of the class.
   * @param className The name of the class.
   * @param docs The list of members to be added.
   * @param isExec If true, the members are executable (methods or
   * constructors), if false, the members are fields.
   */
  private static void addMemberEntries(BookmarkEntry parent, MemberDoc[] docs, boolean isExec)
  {

    Map pkgMap = new TreeMap();
    for (int i = 0; i < docs.length; i++) {
      pkgMap.put(docs[i].qualifiedName(), docs[i]);
    }
    for (Iterator i = pkgMap.keySet().iterator(); i.hasNext();) {
      String dest = (String) i.next();
      MemberDoc doc = (MemberDoc) pkgMap.get(dest);
      String label = doc.name();

      if (isExec) {
        ExecutableMemberDoc execDoc = (ExecutableMemberDoc) doc;
        dest = dest + execDoc.flatSignature();
        label = label + execDoc.flatSignature();
      }
      BookmarkEntry entry = new BookmarkEntry(label, dest);
      log.debug("add entry for: " + dest);
      parent.addChild(entry);

    }
  }
}

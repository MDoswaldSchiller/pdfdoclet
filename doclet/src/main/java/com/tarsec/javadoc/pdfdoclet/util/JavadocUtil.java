/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.tarsec.javadoc.pdfdoclet.Configuration;
import com.tarsec.javadoc.pdfdoclet.Destinations;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.State;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.lang.model.element.TypeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Javadoc parsing utility class.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class JavadocUtil implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(JavadocUtil.class);

  /**
   * List of package prefixes (built at startup time).
   */
  private static String[] PACKAGE_PREFIXES = {"com.tarsec."};

  /**
   * Returns the source File for the given Doc; this is either the .java
   * filename for classes/methods/fields/etc, the package.html for packages, or
   * the overview HTML file for RootDoc. Might return null if there is no source
   * file for the given Doc.
   *
   * @param doc The doc for which to look up the filename.
   */
  public static File getSourceFile(Doc doc)
  {
    if (doc != null) {
      SourcePosition pos = doc.position();
      if (pos != null) {
        return pos.file();
      }
    }
    return null;
  }

  /**
   *
   *
   * @param tag
   * @return
   */
  public static String formatSeeTag(SeeTag tag)
  {

    boolean plainText = tag.name().startsWith("@linkplain");
    String linkDest = null;
    String defaultLabel = null;
    String label = tag.label();
    String fullText = getComment(tag.inlineTags());

    if (fullText.startsWith("<") || fullText.startsWith("\"")) {
      return fullText;
    }

    if (tag.referencedMemberName() != null) {
      MemberDoc member = tag.referencedMember();
      if (member != null) {
        defaultLabel = member.name();
        if (member.containingClass().isIncluded()) {
          linkDest = member.qualifiedName();
        }
        /*
                 * Make the label fully qualified if not pointing to the current
                 * class
         */
        if (!member.containingClass().qualifiedName().equals(
            State.getCurrentClass())) {
          if (member instanceof ConstructorDoc) {
            defaultLabel = member.containingPackage().name() + "."
                           + defaultLabel;
          }
          else {
            defaultLabel = member.containingClass().name() + "."
                           + defaultLabel;
          }
        }
        if (member instanceof ExecutableMemberDoc) {
          if (linkDest != null) {
            linkDest += ((ExecutableMemberDoc) member).signature();
            defaultLabel += ((ExecutableMemberDoc) member)
                .flatSignature();
          }
          else {
            defaultLabel += ((ExecutableMemberDoc) member)
                .signature();
          }
        }
      }
      else {
        defaultLabel = tag.referencedMemberName();
      }
    }
    else if (tag.referencedClassName() != null) {
      ClassDoc classDoc = tag.referencedClass();
      if (classDoc != null) {
        if (classDoc.isIncluded()) {
          linkDest = classDoc.qualifiedName();
          defaultLabel = classDoc.name();
        }
        else {
          defaultLabel = classDoc.qualifiedName();
        }
      }
      else {
        defaultLabel = tag.referencedClassName();
        // This actually might be a package reference
        if (tag.referencedPackage() != null && Destinations.isValid(defaultLabel)) {
          linkDest = defaultLabel;
        }
      }
    }
    else if (tag.referencedPackage() != null) {
      PackageDoc packageDoc = tag.referencedPackage();
      defaultLabel = packageDoc.name();
      if (packageDoc.isIncluded()
          || Destinations.isValid(packageDoc.name())) {
        linkDest = defaultLabel;
      }
    }

    if (label == null || label.length() == 0) {
      if (defaultLabel != null) {
        label = defaultLabel;
      }
      else {
        label = fullText;
      }
    }

    if (linkDest != null && plainText) {
      return "<a href=\"locallinkplain:" + linkDest + "\">" + label + "</a>";
    }
    else if (linkDest != null) {
      return "<a href=\"locallink:" + linkDest + "\">" + label + "</a>";
    }
    else if (!plainText) {
      return "<code>" + label + "</code>";
    }
    else {
      return label;
    }
  }

  /**
   * This utility method returns the comment text for a given method doc. If the
   * method comment uses the "inheritDoc"-tag, it is resolved recursively.
   *
   * @param doc The method doc.
   * @return The final comment text.
   */
  public static String getComment(Doc doc)
  {
    if (doc == null) {
      return "";
    }
    String text = getComment(doc.inlineTags());
    if (doc instanceof MethodDoc && (text == null || text.length() == 0)) {
      MethodDoc superMethod = ((MethodDoc) doc).overriddenMethod();
      if (superMethod != null) {
        text = getComment(superMethod);
      }
    }
    return text;
  }

  /**
   * Returns the HTML string for an array of Tags. Meant to be called with the
   * result of the inlineTags() method of Doc and/or Tag, as this does not
   * support the tag types found elsewhere. Specifically, this method handles
   * docRoot, inheritDoc, link, and linkplain tags, as well as raw-text Tags.
   */
  public static String getComment(Tag[] tags)
  {
    if (tags == null || tags.length == 0) {
      return "";
    }

    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < tags.length; i++) {
      buf.append(getComment(tags[i]));
    }
    return buf.toString();
  }

  /**
   * Returns the HTML string for a single tag.
   *
   * @param tag
   * @return
   */
  public static String getComment(Tag tag)
  {
    if (tag == null) {
      return "";
    }

    if (tag.kind().equalsIgnoreCase("Text")) {
      return tag.text();
    }
    else if (tag.name().equalsIgnoreCase("@docRoot")) {
      return Configuration.getWorkDir();
    }
    else if (tag.name().equalsIgnoreCase("@inheritDoc")) {
      Doc doc = tag.holder();
      if (doc instanceof MethodDoc) {
        return getComment(((MethodDoc) doc).overriddenMethod());
      }
      else if (doc instanceof ClassDoc) {
        return getComment(((ClassDoc) doc).superclass());
      }
      else {
        LOG.warn("Unknown @inheritDoc doc type " + doc);
      }
      return tag.text();
    }
    else if (tag instanceof SeeTag) {
      return formatSeeTag((SeeTag) tag);
    }
    else {
      //FIXME custom tag handling?
      if (LOG.isDebugEnabled()) {
        LOG.debug("Unknown tag " + tag.name() + ": " + tag.text());
      }
      return tag.text(); // not sure this is right
    }

  }

  /**
   * Builds the internal list of packages used later to decide if a given class
   * is an "external" class or if it belongs to one of these packages.
   *
   * @param root The javadoc root
   */
  public static void buildPackageList(RootDoc root)
  {

    ClassDoc classes[] = root.classes();
    Hashtable pkgMap = new Hashtable();
    for (int i = 0; i < classes.length; i++) {
      pkgMap.put(classes[i].containingPackage().name(), "");
    }

    // Now check if there have been any additional
    // packages been specified with "dontspec"
    String dontSpec = Configuration.getProperty(ARG_DONTSPEC);
    if (dontSpec != null) {
      StringTokenizer tok = new StringTokenizer(dontSpec, ",");
      while (tok.hasMoreTokens()) {
        String token = tok.nextToken();
        pkgMap.put(token, "");
      }
    }

    int number = pkgMap.size();

    PACKAGE_PREFIXES = new String[number];
    Enumeration keys = pkgMap.keys();
    int ct = 0;
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      PACKAGE_PREFIXES[ct] = key;
      ct++;
    }
  }

  /**
   * Utility method which returns the name of a class or interface. For classes
   * and interfaces of external packages, the fully qualified name is returned;
   * for such of of the same package, only the short name is returned.
   *
   * @param classDoc The class or interface whose name should be returned.
   * @return The short or fully qualifed name of the class or interface.
   */
  public static String getQualifiedNameIfNecessary(ClassDoc classDoc)
  {
    String name = classDoc.name();
    boolean isExternal = true;

    for (int i = 0; (i < PACKAGE_PREFIXES.length) && isExternal; i++) {
      if (classDoc.qualifiedTypeName().startsWith(PACKAGE_PREFIXES[i])) {
        isExternal = false;
      }
    }

    if (isExternal) {
      name = classDoc.qualifiedTypeName();
    }

    return name;
  }
  
    public static String getQualifiedNameIfNecessary(TypeElement classDoc)
  {
    String name = classDoc.getSimpleName().toString();
    boolean isExternal = true;

    for (int i = 0; (i < PACKAGE_PREFIXES.length) && isExternal; i++) {
      if (classDoc.getQualifiedName().toString().startsWith(PACKAGE_PREFIXES[i])) {
        isExternal = false;
      }
    }

    if (isExternal) {
      name = classDoc.getQualifiedName().toString();
    }

    return name;
  }

  /**
   * Utility method which returns only the first sentence of a given text
   * String. This is used for the summary tables where only the first sentence
   * of the doc of a constructor, field or method is printed.
   *
   * @param text The whole doc text.
   * @return The first sentence of the text.
   */
  public static String getFirstSentence(Doc text)
  {
    return getComment(text.firstSentenceTags());
  }

  /**
   * Utility method which returns the name of a class or interface. For classes
   * and interfaces of external packages, the fully qualified name is returned;
   * for such of of the same package, only the short name is returned.
   *
   * @param qualifiedName The qualified name of the class or interface.
   * @return The short or fully qualifed name of the class or interface.
   */
  public static String getQualifiedNameIfNecessary(String qualifiedName)
  {
    String name = qualifiedName;
    boolean isExternal = true;

    for (int i = 0; (i < PACKAGE_PREFIXES.length) && isExternal; i++) {
      if (qualifiedName.startsWith(PACKAGE_PREFIXES[i])) {
        isExternal = false;
      }
    }

    if ((isExternal == false) && (qualifiedName.indexOf(".") != -1)) {
      name = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1,
                                     qualifiedName.length());
    }

    return name;
  }

  /**
   * Returns a text String with the modifiers of a given class.
   *
   * @param classDoc The ClassDoc for the given class.
   * @return A text String with all modifiers.
   */
  public static String getClassModifiers(ClassDoc classDoc)
  {
    String info = "";

    if (classDoc.isPublic()) {
      info = "public ";
    }

    if (classDoc.isPrivate()) {
      info = "private ";
    }

    if (classDoc.isProtected()) {
      info = "protected ";
    }

    if (!classDoc.isInterface()) {
      if (classDoc.isStatic()) {
        info = info + "static ";
      }

      if (classDoc.isFinal()) {
        info = info + "final ";
      }

      if (classDoc.isAbstract()) {
        info = info + "abstract ";
      }

      info = info + "class ";
    }
    else {
      info = info + "interface ";
    }

    return info;
  }
  
  public static String getClassModifiers(TypeElement classDoc)
  {
    String info = "";

//    if (classDoc.isPublic()) {
//      info = "public ";
//    }
//
//    if (classDoc.isPrivate()) {
//      info = "private ";
//    }
//
//    if (classDoc.isProtected()) {
//      info = "protected ";
//    }
//
//    if (!classDoc.isInterface()) {
//      if (classDoc.isStatic()) {
//        info = info + "static ";
//      }
//
//      if (classDoc.isFinal()) {
//        info = info + "final ";
//      }
//
//      if (classDoc.isAbstract()) {
//        info = info + "abstract ";
//      }
//
//      info = info + "class ";
//    }
//    else {
//      info = info + "interface ";
//    }

    return info;
  }

  /**
   * Returns a text String with the modifiers of a given method.
   *
   * @param methodDoc The MethodDoc for the given method.
   * @return A text String with all modifiers.
   */
  public static String getMethodModifiers(MethodDoc methodDoc)
  {
    // Remark: Do NOT use "methodDoc.modifiers()" here, because it
    // also includes modifiers usually ignored by javadoc, such
    // as "synchronized".
    String declaration = "";

    if (methodDoc.isPublic()) {
      declaration = "public ";
    }
    else if (methodDoc.isProtected()) {
      declaration = "protected ";
    }
    else if (methodDoc.isPrivate()) {
      declaration = "private ";
    }

    if (methodDoc.isFinal()) {
      declaration = declaration + "final ";
    }

    if (methodDoc.isStatic()) {
      declaration = declaration + "static ";
    }

    if (methodDoc.isNative()) {
      declaration = declaration + "native ";
    }

    if (methodDoc.isAbstract()) {
      declaration = declaration + "abstract ";
    }

    return declaration;
  }

  /**
   * Returns a text String with the modifiers of the method summary of a given
   * method.
   *
   * @param methodDoc The MethodDoc for the given method.
   * @return A text String with all modifiers.
   */
  public static String getMethodSummaryModifiers(MethodDoc methodDoc)
  {
    // Remark: Do NOT use "methodDoc.modifiers()" here, because it
    // also includes modifiers usually ignored by javadoc, such
    // as "synchronized".
    String declaration = "";

    if (methodDoc.isStatic()) {
      declaration = declaration + "static ";
    }

    if (methodDoc.isNative()) {
      declaration = declaration + "native ";
    }

    if (methodDoc.isAbstract()) {
      declaration = declaration + "abstract ";
    }

    return declaration;
  }

  /**
   * Returns a text String with the modifiers of a given constructor.
   *
   * @param constructorDoc The ConstructorDoc for the given constructor.
   * @return A text String with all modifiers.
   */
  public static String getConstructorModifiers(ConstructorDoc constructorDoc)
  {
    String declaration = "";

    if (constructorDoc.isPublic()) {
      declaration = "public ";
    }

    if (constructorDoc.isProtected()) {
      declaration = "protected ";
    }

    if (constructorDoc.isPrivate()) {
      declaration = "private ";
    }

    if (constructorDoc.isFinal()) {
      declaration = declaration + "final ";
    }

    return declaration;
  }

  /**
   * Returns a text String with the modifiers of a given field.
   *
   * @param fieldDoc The FieldDoc for the given field.
   * @return A text String with all modifiers.
   */
  public static String getFieldModifiers(FieldDoc fieldDoc)
  {
    return fieldDoc.modifiers() + " ";
  }

  /**
   * Returns a string with the modifiers of a given field.
   *
   * @param fieldDoc The FieldDoc for the given field.
   * @return A text String with all modifiers.
   */
  public static String getFieldSummaryModifiers(FieldDoc fieldDoc)
  {
    StringBuffer buffer = new StringBuffer();

    if (fieldDoc.isStatic()) {
      buffer.append("static ");
    }
    if (fieldDoc.isFinal()) {
      buffer.append("final ");
    }

    String type = getQualifiedNameIfNecessary(fieldDoc.type().toString());
    buffer.append(type);

    return buffer.toString();
  }

  /**
   * Utility method for sorting ClassDoc table in function of names;
   *
   * @param original to be sorted
   * @return The sorted classdocs in an array.
   */
  public static ClassDoc[] sort(ClassDoc[] original)
  {
    int taille = original.length;

    if (taille > 1) {
      String[] str = new String[taille];
      ClassDoc[] tabresult = new ClassDoc[taille];

      for (int i = 0; i < taille; i++) {
        str[i] = original[i].name().toLowerCase();
      }

      Arrays.sort(str);

      for (int i = 0; i < taille; i++) {
        for (int j = 0; j < taille; j++) {
          if (str[i].equals(original[j].name().toLowerCase())) {
            tabresult[i] = original[j];

            break;
          }
        }
      }

      return tabresult;
    }
    else {
      return original;
    }
  }
  

  /**
   * Utility method for concatening ClassDoc tables.
   *
   * @param all destination table
   * @param pos from where must be included source table into destination table
   * @param classes table, to be included into destination table
   */
  public static void addClassDoc(ClassDoc[] all, int pos, ClassDoc[] classes)
  {
    for (int i = 0; i < classes.length; i++) {
      all[i + pos] = classes[i];
    }
  }

  /**
   * Returns the type of a parameter.
   *
   * @param parm The parameter whose type is to be determined.
   * @return The class name or primitive type name.
   */
  public static String getParameterType(Parameter parm)
  {
    String result = "";
    Type type = parm.type();
    ClassDoc classDoc = type.asClassDoc();
    String dimension = type.dimension();

    if (classDoc == null) {
      // primitive type
      result = type.typeName();
    }
    else {
      // class type
      result = JavadocUtil.getQualifiedNameIfNecessary(classDoc);
    }

    return result + dimension;
  }

//  public static String findSuperClassWithMethod(String method)
//  {
//    LOG.debug("Method: " + method);
//    String result = State.getCurrentClass().toString();
//    Doc doc = State.getCurrentDoc();
//    if (doc instanceof ClassDoc) {
//      ClassDoc classDoc = (ClassDoc) doc;
//      result = findMethodInClass(classDoc, method);
//    }
//    LOG.debug("Result: " + result);
//    return result;
//  }

  /**
   * Recursively looks for a superclass with a certain method.
   *
   * @param classDoc The current class to search.
   * @param method The method name and optionally signature.
   * @return The class name found or null.
   */
  private static String findMethodInClass(ClassDoc classDoc, String method)
  {
    LOG.debug("ClassDoc: " + classDoc.name() + ", Method: " + method);
    String result = null;
    MethodDoc[] methods = classDoc.methods();
    if (methods != null && methods.length > 0) {
      method = method.trim();
      for (int i = 0; i < methods.length; i++) {
        MethodDoc doc = methods[i];
        if (method.indexOf("(") == -1 || method.endsWith("()")) {
          // If method search string does not contain parameters
          // just compare method name (what else can we do?)
          if (method.equals(doc.name())) {
            result = doc.qualifiedName();
          }
        }
        else {
          int pos1 = method.indexOf("(");
          int pos2 = method.indexOf(")");
          if (pos1 != -1 && pos2 > pos1) {
            String methodName = method.substring(0, pos1);
            if (doc.name().equals(methodName)) {
              // extract the parameter list from the search method
              // string
              String args1 = method.substring(pos1 + 1, pos2);
              // build the parameter list for the doc method
              // string
              String args2 = doc.flatSignature();
              // extract brackets
              args2 = args2.substring(1, args2.length() - 1);

              // Now compare the two. Since they can contain any
              // blanks between parameters and commas, a simple
              // string compare is not enough. We need to build
              // clean, blank-free strings to compare.
              // Build list 1 (search method string)
              StringTokenizer tok1 = new StringTokenizer(args1,
                                                         ",");
              String list1 = "";
              while (tok1.hasMoreTokens()) {
                String arg = tok1.nextToken().trim();
                list1 = list1 + arg;
                if (tok1.hasMoreTokens()) {
                  list1 = list1 + ",";
                }
              }

              // Build list 2 (doc method string)
              StringTokenizer tok2 = new StringTokenizer(args2,
                                                         ",");
              String list2 = "";
              while (tok2.hasMoreTokens()) {
                String arg = tok2.nextToken().trim();
                list2 = list2 + arg;
                if (tok2.hasMoreTokens()) {
                  list2 = list2 + ",";
                }
              }

              // Now we have to compacted lists we can compare
              if (list1.equals(list2)) {
                result = doc.qualifiedName();
              }
            }
          }
        }
      }
    }
    // If it was not found in this class, check next superclass
    if (result == null) {
      ClassDoc superClassDoc = classDoc.superclass();
      if (superClassDoc != null) {
        result = findMethodInClass(superClassDoc, method);
      }
    }
    return result;
  }

  /**
   * Returns the fully qualified type of a parameter.
   *
   * @param parm The parameter whose type is to be determined.
   * @return The class name or primitive type name.
   */
  public static String getQualifiedParameterType(Parameter parm)
  {
    String result = "";
    Type type = parm.type();
    ClassDoc classDoc = type.asClassDoc();

    if (classDoc == null) {
      // primitive type
      result = type.typeName();
    }
    else {
      // class type
      result = classDoc.qualifiedTypeName();
    }

    return result;
  }

}

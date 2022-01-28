/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/filter/FilteredPackageDoc.java,v 1.1 2007/07/18 22:15:08 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.filter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.tarsec.javadoc.pdfdoclet.Configuration;

/**
 * ...
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class FilteredPackageDoc implements PackageDoc
{

  private PackageDoc packageDoc = null;

  /**
   * Default constructor.
   */
  public FilteredPackageDoc()
  {
  }

  /**
   * Creates an instance of this class which wraps the given PackageDoc object.
   *
   * @param packageDoc The PackageDoc object to be wrapped.
   */
  public FilteredPackageDoc(PackageDoc packageDoc)
  {
    this.packageDoc = packageDoc;
  }

  /**
   * Allows to set the PackageDoc object which this class should wrap.
   *
   * @param packageDoc The PackageDoc object to be wrapped.
   */
  public void setPackageDoc(PackageDoc packageDoc)
  {
    this.packageDoc = packageDoc;
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.PackageDoc#allClasses()
   */
  public ClassDoc[] allClasses()
  {
    if (!Configuration.isFilterActive()) {
      return packageDoc.allClasses();
    }
    return Filter.createFilteredClassesList(packageDoc.allClasses());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.PackageDoc#errors()
   */
  public ClassDoc[] errors()
  {
    if (!Configuration.isFilterActive()) {
      return packageDoc.errors();
    }
    return Filter.createFilteredClassesList(packageDoc.errors());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.PackageDoc#exceptions()
   */
  public ClassDoc[] exceptions()
  {
    if (!Configuration.isFilterActive()) {
      return packageDoc.exceptions();
    }
    return Filter.createFilteredClassesList(packageDoc.exceptions());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.PackageDoc#interfaces()
   */
  public ClassDoc[] interfaces()
  {
    if (!Configuration.isFilterActive()) {
      return packageDoc.interfaces();
    }
    return Filter.createFilteredClassesList(packageDoc.interfaces());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.PackageDoc#ordinaryClasses()
   */
  public ClassDoc[] ordinaryClasses()
  {
    if (!Configuration.isFilterActive()) {
      return packageDoc.ordinaryClasses();
    }
    return Filter.createFilteredClassesList(packageDoc.ordinaryClasses());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.PackageDoc#allClasses(boolean)
   */
  public ClassDoc[] allClasses(boolean arg0)
  {
    if (!Configuration.isFilterActive()) {
      return packageDoc.allClasses(arg0);
    }
    return Filter.createFilteredClassesList(packageDoc.allClasses(arg0));
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.PackageDoc#findClass(java.lang.String)
   */
  public ClassDoc findClass(String arg0)
  {
    return packageDoc.findClass(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isClass()
   */
  public boolean isClass()
  {
    return packageDoc.isClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isConstructor()
   */
  public boolean isConstructor()
  {
    return packageDoc.isConstructor();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isError()
   */
  public boolean isError()
  {
    return packageDoc.isError();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isException()
   */
  public boolean isException()
  {
    return packageDoc.isException();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isField()
   */
  public boolean isField()
  {
    return packageDoc.isField();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isIncluded()
   */
  public boolean isIncluded()
  {
    return packageDoc.isIncluded();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isInterface()
   */
  public boolean isInterface()
  {
    return packageDoc.isInterface();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isMethod()
   */
  public boolean isMethod()
  {
    return packageDoc.isMethod();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isOrdinaryClass()
   */
  public boolean isOrdinaryClass()
  {
    return packageDoc.isOrdinaryClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#seeTags()
   */
  public SeeTag[] seeTags()
  {
    return packageDoc.seeTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#position()
   */
  public SourcePosition position()
  {
    return packageDoc.position();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#firstSentenceTags()
   */
  public Tag[] firstSentenceTags()
  {
    return packageDoc.firstSentenceTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#inlineTags()
   */
  public Tag[] inlineTags()
  {
    return packageDoc.inlineTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags()
   */
  public Tag[] tags()
  {
    return packageDoc.tags();
  }

  /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object arg0)
  {
    return packageDoc.compareTo(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#commentText()
   */
  public String commentText()
  {
    return packageDoc.commentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#getRawCommentText()
   */
  public String getRawCommentText()
  {
    return packageDoc.getRawCommentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#name()
   */
  public String name()
  {
    return packageDoc.name();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
   */
  public void setRawCommentText(String arg0)
  {
    packageDoc.setRawCommentText(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags(java.lang.String)
   */
  public Tag[] tags(String arg0)
  {
    return packageDoc.tags(arg0);
  }

}

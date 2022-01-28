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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public ClassDoc findClass(String arg0)
  {
    return packageDoc.findClass(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isClass()
   */
  @Override
  public boolean isClass()
  {
    return packageDoc.isClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isConstructor()
   */
  @Override
  public boolean isConstructor()
  {
    return packageDoc.isConstructor();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isError()
   */
  @Override
  public boolean isError()
  {
    return packageDoc.isError();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isException()
   */
  @Override
  public boolean isException()
  {
    return packageDoc.isException();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isField()
   */
  @Override
  public boolean isField()
  {
    return packageDoc.isField();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isIncluded()
   */
  @Override
  public boolean isIncluded()
  {
    return packageDoc.isIncluded();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isInterface()
   */
  @Override
  public boolean isInterface()
  {
    return packageDoc.isInterface();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isMethod()
   */
  @Override
  public boolean isMethod()
  {
    return packageDoc.isMethod();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isOrdinaryClass()
   */
  @Override
  public boolean isOrdinaryClass()
  {
    return packageDoc.isOrdinaryClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#seeTags()
   */
  @Override
  public SeeTag[] seeTags()
  {
    return packageDoc.seeTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#position()
   */
  @Override
  public SourcePosition position()
  {
    return packageDoc.position();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#firstSentenceTags()
   */
  @Override
  public Tag[] firstSentenceTags()
  {
    return packageDoc.firstSentenceTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#inlineTags()
   */
  @Override
  public Tag[] inlineTags()
  {
    return packageDoc.inlineTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags()
   */
  @Override
  public Tag[] tags()
  {
    return packageDoc.tags();
  }

  /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Object arg0)
  {
    return packageDoc.compareTo(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#commentText()
   */
  @Override
  public String commentText()
  {
    return packageDoc.commentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#getRawCommentText()
   */
  @Override
  public String getRawCommentText()
  {
    return packageDoc.getRawCommentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#name()
   */
  @Override
  public String name()
  {
    return packageDoc.name();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
   */
  @Override
  public void setRawCommentText(String arg0)
  {
    packageDoc.setRawCommentText(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags(java.lang.String)
   */
  @Override
  public Tag[] tags(String arg0)
  {
    return packageDoc.tags(arg0);
  }

}

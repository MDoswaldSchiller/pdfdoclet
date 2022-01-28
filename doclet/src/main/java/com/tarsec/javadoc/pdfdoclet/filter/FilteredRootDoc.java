/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/filter/FilteredRootDoc.java,v 1.1 2007/07/18 22:15:08 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.filter;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.tarsec.javadoc.pdfdoclet.Configuration;

/**
 * Wraps a Javadoc RootDoc object to allow to filter out packages / classes /
 * methods transparently.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class FilteredRootDoc implements RootDoc
{

  /**
   * Wrapped RootDoc object reference.
   */
  private RootDoc rootDoc = null;

  /**
   * Default constructor.
   */
  public FilteredRootDoc()
  {
  }

  /**
   * Creates an instance of this class which wraps the given RootDoc object.
   *
   * @param rootDoc The RootDoc object to be wrapped.
   */
  public FilteredRootDoc(RootDoc rootDoc)
  {
    this.rootDoc = rootDoc;
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.RootDoc#classes()
   */
  public ClassDoc[] classes()
  {
    if (!Configuration.isFilterActive()) {
      return rootDoc.classes();
    }
    return Filter.createFilteredClassesList(rootDoc.classes());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.RootDoc#specifiedClasses()
   */
  public ClassDoc[] specifiedClasses()
  {
    if (!Configuration.isFilterActive()) {
      return rootDoc.specifiedClasses();
    }
    return Filter.createFilteredClassesList(rootDoc.specifiedClasses());
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.RootDoc#specifiedPackages()
   */
  public PackageDoc[] specifiedPackages()
  {
    PackageDoc[] list = rootDoc.specifiedPackages();
    FilteredPackageDoc[] result = new FilteredPackageDoc[list.length];
    for (int i = 0; i < list.length; i++) {
      result[i] = new FilteredPackageDoc(list[i]);
    }
    return result;
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.RootDoc#options()
   */
  public String[][] options()
  {
    return rootDoc.options();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.RootDoc#classNamed(java.lang.String)
   */
  public ClassDoc classNamed(String arg0)
  {
    return rootDoc.classNamed(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.RootDoc#packageNamed(java.lang.String)
   */
  public PackageDoc packageNamed(String arg0)
  {
    return rootDoc.packageNamed(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isClass()
   */
  public boolean isClass()
  {
    return rootDoc.isClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isConstructor()
   */
  public boolean isConstructor()
  {
    return rootDoc.isConstructor();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isError()
   */
  public boolean isError()
  {
    return rootDoc.isError();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isException()
   */
  public boolean isException()
  {
    return rootDoc.isException();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isField()
   */
  public boolean isField()
  {
    return rootDoc.isField();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isIncluded()
   */
  public boolean isIncluded()
  {
    return rootDoc.isIncluded();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isInterface()
   */
  public boolean isInterface()
  {
    return rootDoc.isInterface();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isMethod()
   */
  public boolean isMethod()
  {
    return rootDoc.isMethod();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#isOrdinaryClass()
   */
  public boolean isOrdinaryClass()
  {
    return rootDoc.isOrdinaryClass();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#seeTags()
   */
  public SeeTag[] seeTags()
  {
    return rootDoc.seeTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#position()
   */
  public SourcePosition position()
  {
    return rootDoc.position();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#firstSentenceTags()
   */
  public Tag[] firstSentenceTags()
  {
    return rootDoc.firstSentenceTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#inlineTags()
   */
  public Tag[] inlineTags()
  {
    return rootDoc.inlineTags();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags()
   */
  public Tag[] tags()
  {
    return rootDoc.tags();
  }

  /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object arg0)
  {
    return rootDoc.compareTo(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#commentText()
   */
  public String commentText()
  {
    return rootDoc.commentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#getRawCommentText()
   */
  public String getRawCommentText()
  {
    return rootDoc.getRawCommentText();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#name()
   */
  public String name()
  {
    return rootDoc.name();
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#setRawCommentText(java.lang.String)
   */
  public void setRawCommentText(String arg0)
  {
    rootDoc.setRawCommentText(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.Doc#tags(java.lang.String)
   */
  public Tag[] tags(String arg0)
  {
    return rootDoc.tags(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.DocErrorReporter#printError(java.lang.String)
   */
  public void printError(String arg0)
  {
    rootDoc.printError(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.DocErrorReporter#printNotice(java.lang.String)
   */
  public void printNotice(String arg0)
  {
    rootDoc.printNotice(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.DocErrorReporter#printWarning(java.lang.String)
   */
  public void printWarning(String arg0)
  {
    rootDoc.printWarning(arg0);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.DocErrorReporter#printError(com.sun.javadoc.SourcePosition, java.lang.String)
   */
  public void printError(SourcePosition arg0, String arg1)
  {
    rootDoc.printError(arg0, arg1);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.DocErrorReporter#printNotice(com.sun.javadoc.SourcePosition, java.lang.String)
   */
  public void printNotice(SourcePosition arg0, String arg1)
  {
    rootDoc.printNotice(arg0, arg1);
  }

  /* (non-Javadoc)
     * @see com.sun.javadoc.DocErrorReporter#printWarning(com.sun.javadoc.SourcePosition, java.lang.String)
   */
  public void printWarning(SourcePosition arg0, String arg1)
  {
    rootDoc.printWarning(arg0, arg1);
  }

}

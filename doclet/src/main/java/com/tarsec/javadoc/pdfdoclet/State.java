/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the state of the doclet creation process.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class State
{
  private static final Logger LOG = LoggerFactory.getLogger(State.class);

  /**
   * Debug output flag.
   */
  public static boolean debug = false;

  /**
   * Current type of page header (none, class name, index..)
   */
  private static int headerType = 0;

  /**
   * Number of current page
   */
  private static int currentPage = 0;

  /**
   * Determines if it is the last method to be managed.
   */
  private static boolean last = false;

  /**
   * Determines if it a text is continued on the next page.
   */
  private static boolean isContinued = false;

  /**
   * Name of current package
   */
  public static CharSequence currentPackage = "";

  /**
   * Name of current class
   */
  public static CharSequence currentClass = "";

  /**
   * Name of current member
   */
  public static CharSequence currentMember = "";

  /**
   * Name of current method
   */
  public static CharSequence currentMethod = "";

  /**
   * Current File
   */
  public static File currentFile = null;

  // ******************************************************
  // ACCESSOR METHODS
  // ******************************************************

  /**
   * Returns the currently processed File.
   *
   * @return The currently processed File (or null).
   */
  public static File getCurrentFile()
  {
    return currentFile;
  }

  /**
   * Sets the currently processed File.
   */
  public static void setCurrentFile(File file)
  {
    currentFile = file;
  }


  /**
   * State method which returns the name of the class currently processed by the
   * doclet.
   *
   * @return The name of the current class.
   */
  public static CharSequence getCurrentClass()
  {
    LOG.debug("Get current class: " + currentClass);
    return currentClass;
  }

  /**
   * Sets the name of the class that is currently processed.
   *
   * @param value The name of the class.
   */
  public static void setCurrentClass(CharSequence value)
  {
    LOG.debug("Set current class to " + value);
    currentClass = value;
  }

  /**
   * State method which returns the name of the package currently processed by
   * the doclet.
   *
   * @return The name of the current package.
   */
  public static CharSequence getCurrentPackage()
  {
    LOG.debug("Get current package: " + currentPackage);
    return currentPackage;
  }

  /**
   * Sets the name of the package that is currently processed.
   *
   * @param value The name of the package.
   */
  public static void setCurrentPackage(CharSequence value)
  {
    LOG.debug("Set current package to " + value);
    currentPackage = value;
  }

  /**
   * State method which returns the name of the member currently processed by
   * the doclet.
   *
   * @return The name of the current member.
   */
  public static CharSequence getCurrentMember()
  {
    LOG.debug("Get current member: " + currentMember);
    return currentMember;
  }

  /**
   * Sets the name of the member that is currently processed.
   *
   * @param value The name of the member.
   */
  public static void setCurrentMember(CharSequence value)
  {
    LOG.debug("Set current member to " + value);
    currentMember = value;
  }

  /**
   * State method which returns the name of the method currently processed by
   * the doclet.
   *
   * @return The name of the current method.
   */
  public static CharSequence getCurrentMethod()
  {
    return currentMethod;
  }

  /**
   * Sets the name of the method that is currently processed.
   *
   * @param value The name of the method.
   */
  public static void setCurrentMethod(CharSequence value)
  {
    currentMethod = value;
  }

  /**
   * Defines if the document is currently in the state of a "continuing"
   * section.
   *
   * @param value True if a documentation text is currently being continued.
   */
  public static void setContinued(boolean value)
  {
    isContinued = value;
  }

  /**
   * Determines if the document is currently in the state of a "continuing"
   * section (member documentation). If this is the case when the end of a page
   * is reached, the footer of that page will get an additional remark
   * ("continued on the next page") and the header of the next page another one
   * ("continued from last page").
   *
   * @return true if currently something is continued.
   */
  public static boolean isContinued()
  {
    //return isContinued;
    return false;
  }

  /**
   * Sets the current page to a specific value.
   *
   * @param page The number of the current page.
   */
  public static void setCurrentPage(int page)
  {
    LOG.debug("Set current page to " + page);
    currentPage = page;
  }

  /**
   * Returns the current page number.
   *
   * @return The number of the current page.
   */
  public static int getCurrentPage()
  {
    LOG.debug("Get current page: " + currentPage);
    return currentPage;
  }

  /**
   * State method which returns true if it is the last method of the class.
   *
   * @return boolean to warn if it is the last method.
   */
  public static boolean isLastMethod()
  {
    return last;
  }

  /**
   * Defines if the method currently processed is the last.
   *
   * @param value True if it is the last method.
   */
  public static void setLastMethod(boolean value)
  {
    last = value;
  }

  /**
   * Sets the type of header to be used for the current page (none, class name
   * or index title).
   *
   * @param value The header type.
   */
  public static void setCurrentHeaderType(int value)
  {
    headerType = value;
  }

  /**
   * Returns the type of header to be used for the current page (none, class
   * name or index title).
   *
   * @return The header type value.
   */
  public static int getCurrentHeaderType()
  {
    return headerType;
  }
}

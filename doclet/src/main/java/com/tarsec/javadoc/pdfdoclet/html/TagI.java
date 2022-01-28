/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

/**
 * Implements the italic tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagI extends HTMLTag
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagI.class);

  public TagI(HTMLTag parent, int type)
  {
    super(parent, type);
    isItalic = true;
  }

  public boolean isItalic()
  {
    return true;
  }
}

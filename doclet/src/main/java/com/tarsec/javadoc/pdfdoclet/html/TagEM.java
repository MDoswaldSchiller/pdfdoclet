/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

/**
 * Implements the emphasized tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagEM extends HTMLTag
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagEM.class);

  public TagEM(HTMLTag parent, int type)
  {
    super(parent, type);
    isItalic = true;
  }

  public boolean isItalic()
  {
    return true;
  }
}

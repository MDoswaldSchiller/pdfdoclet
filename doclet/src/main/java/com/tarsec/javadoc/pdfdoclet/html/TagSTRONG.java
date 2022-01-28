/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

/**
 * Implements the STRONG HTML tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagSTRONG extends HTMLTag
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagSTRONG.class);

  public TagSTRONG(HTMLTag parent, int type)
  {
    super(parent, type);
    isBold = true;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#isBold()
   */
  public boolean isBold()
  {
    return true;
  }
}

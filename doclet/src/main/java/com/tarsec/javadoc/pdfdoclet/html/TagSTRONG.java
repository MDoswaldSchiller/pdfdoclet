/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

/**
 * Implements the STRONG HTML tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagSTRONG extends HTMLTag
{
  public TagSTRONG(HTMLTag parent, int type)
  {
    super(parent, type);
    isBold = true;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#isBold()
   */
  @Override
  public boolean isBold()
  {
    return true;
  }
}

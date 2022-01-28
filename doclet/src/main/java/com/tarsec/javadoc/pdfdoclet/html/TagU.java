/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

/**
 * Represents the underline tag
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagU extends HTMLTag
{
  public TagU(HTMLTag parent, int type)
  {
    super(parent, type);
    isUnderline = true;
  }

  /*
     *  (non-Javadoc)
     * @see com.tarsec.javadoc.pdfdoclet.html.HTMLTag#isUnderline()
   */
  @Override
  public boolean isUnderline()
  {
    return true;
  }
}

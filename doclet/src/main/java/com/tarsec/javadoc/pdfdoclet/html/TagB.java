/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

/**
 * Implements the bold HTML tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagB extends HTMLTag
{
  public TagB(HTMLTag parent, int type)
  {
    super(parent, type);
    isBold = true;
  }

  public boolean isBold()
  {
    return true;
  }
}

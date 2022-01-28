/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

/**
 * Implements the italic tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagI extends HTMLTag
{
  public TagI(HTMLTag parent, int type)
  {
    super(parent, type);
    isItalic = true;
  }

  @Override
  public boolean isItalic()
  {
    return true;
  }
}

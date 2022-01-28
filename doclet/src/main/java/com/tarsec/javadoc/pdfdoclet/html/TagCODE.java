/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

/**
 * Implements the CODE tag.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagCODE extends HTMLTag
{
  public TagCODE(HTMLTag parent, int type)
  {
    super(parent, type);
    isCode = true;
  }

  public boolean isCode()
  {
    return true;
  }
}

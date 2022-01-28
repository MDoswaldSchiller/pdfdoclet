/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

/**
 * Represents the underline tag
 */
public class TagSTRIKE extends HTMLTag
{
  public TagSTRIKE(HTMLTag parent, int type)
  {
    super(parent, type);
    isStrikethrough = true;
  }

  public boolean isStrikethrough()
  {
    return true;
  }
}

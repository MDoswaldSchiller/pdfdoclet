/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

/**
 * Represents the underline tag
 */
public class TagSTRIKE extends HTMLTag
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagSTRIKE.class);

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

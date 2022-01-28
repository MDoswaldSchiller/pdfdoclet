/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements external links (http://..)
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagDIV extends HTMLTag
{
  private static final Logger LOG = LoggerFactory.getLogger(TagDIV.class);

  /**
   * Create a link tag instance.
   *
   * @param parent The parent HTML tag.
   * @param type The type of this tag.
   */
  public TagDIV(HTMLTag parent, int type)
  {
    super(parent, type);

    LOG.debug("** DIV tag created ");
  }
}

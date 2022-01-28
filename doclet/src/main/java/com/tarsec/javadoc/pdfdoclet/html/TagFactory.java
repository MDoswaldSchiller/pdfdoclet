/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import org.apache.log4j.Logger;

/**
 * Factory for creating HTML tag objects.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagFactory
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagFactory.class);

  protected static String[] tags = HTMLTag.tags;

  /**
   * Creates a HTML tag object of the given type.
   *
   * @param parent The parent HTML tag. Can be null if the HTML tag to be
   * created does not have a parent tag (i.e. is not a nested tag).
   * @param type The type for the HTML tag to be created.
   * @param node The DOM node of the HTML tag to be created.
   * @return A HTML tag object or null if none could be created.
   */
  public static HTMLTag createTag(HTMLTag parent, int type)
  {
    log.debug(">");
    switch (type) {
      case HTMLTag.TAG_UL:
        return new TagUL(parent, type);
      case HTMLTag.TAG_OL:
        return new TagOL(parent, type);
      case HTMLTag.TAG_LI:
        return new TagLI(parent, type);
      case HTMLTag.TAG_P:
        return new TagP(parent, type);
      case HTMLTag.TAG_BR:
        return new TagBR(parent, type);
      case HTMLTag.TAG_A:
        return new TagA(parent, type);
      case HTMLTag.TAG_EM:
        return new TagEM(parent, type);
      case HTMLTag.TAG_B:
        return new TagB(parent, type);
      case HTMLTag.TAG_INS:
      case HTMLTag.TAG_U:
        return new TagU(parent, type);
      case HTMLTag.TAG_I:
        return new TagI(parent, type);
      case HTMLTag.TAG_CODE:
        return new TagCODE(parent, type);
      case HTMLTag.TAG_DEL:
      case HTMLTag.TAG_S:
      case HTMLTag.TAG_STRIKE:
        return new TagSTRIKE(parent, type);
      case HTMLTag.TAG_H1:
      case HTMLTag.TAG_H2:
      case HTMLTag.TAG_H3:
      case HTMLTag.TAG_H4:
      case HTMLTag.TAG_H5:
      case HTMLTag.TAG_H6:
        return new TagH(parent, type);
      case HTMLTag.TAG_CENTER:
        return new TagCENTER(parent, type);
//            case HTMLTag.TAG_DIV:     return new TagDIV(parent, type);
      case HTMLTag.TAG_PRE:
        return new TagPRE(parent, type);
      case HTMLTag.TAG_IMG:
        return new TagIMG(parent, type);
      case HTMLTag.TAG_HR:
        return new TagHR(parent, type);
      case HTMLTag.TAG_TABLE:
        return new TagTABLE(parent, type);
      case HTMLTag.TAG_THEAD:
        return new TagTHEAD(parent, type);
      case HTMLTag.TAG_TR:
        return new TagTR(parent, type);
      case HTMLTag.TAG_TD:
      case HTMLTag.TAG_TH:
        return new TagTD(parent, type);
      case HTMLTag.TAG_DL:
        return new TagDL(parent, type);
      case HTMLTag.TAG_DT:
        return new TagDT(parent, type);
      case HTMLTag.TAG_DD:
        return new TagDD(parent, type);
      case HTMLTag.TAG_STRONG:
        return new TagSTRONG(parent, type);
      case HTMLTag.TAG_BODY:
      default:
        return new TagBODY(parent, type);
    }
  }
}

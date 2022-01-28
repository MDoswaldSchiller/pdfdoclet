/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/TagList.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;

/**
 * This class builds a list of all tags in a given doc object, such as a method
 * doc. When the list is returned for printing, it is sorted and filtered
 * according to the configuration (for instance, the 'author' tag is only
 * returned if it's enabled in the configuration).
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class TagList implements IConstants
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TagList.class);

  /**
   * Reference to the doc object.
   */
  private Doc doc = null;

  /**
   * Stores the tags and their values.
   */
  private Hashtable allTags = new Hashtable();

  /**
   * Stores the print labels for the tags.
   */
  private static Properties labels = new Properties();

  /**
   * Pre-sets some tag name labels.
   */
  public static void initialize()
  {
    // First set some default values
    setTagLabel(DOC_TAG_PARAM, LB_PARM_TAG);
    setTagLabel(DOC_TAG_RETURN, LB_RETURNS_TAG);
    setTagLabel(DOC_TAG_THROWS, LB_EXCEP_TAG);
    setTagLabel(DOC_TAG_EXCEPTION, LB_EXCEP_TAG);
    setTagLabel(DOC_TAG_VERSION, LB_VERSION_TAG);
    setTagLabel(DOC_TAG_SEE, LB_SEE_TAG);
    setTagLabel(DOC_TAG_AUTHOR, LB_AUTHOR_TAG);
    setTagLabel(DOC_TAGS_DEPRECATED, LB_DEPRECATED_TAG);
    setTagLabel(DOC_TAG_SINCE, LB_SINCE_TAG);

    // Now override with configuration
    Properties props = Configuration.getConfiguration();
    Enumeration names = props.keys();
    int len = ARG_LB_TAGS_PREFIX.length();
    while (names.hasMoreElements()) {
      String value = (String) names.nextElement();
      if (value.startsWith(ARG_LB_TAGS_PREFIX)) {
        // extract name of tag to set label for
        String tagName = value.substring(len, value.length());
        String label = Configuration.getProperty(value, "");
        setTagLabel(tagName, label);
      }
    }
  }

  /**
   * Sets the printing label for a certain tag.
   *
   * @param name The name of the tag (like "@todo").
   * @param label The label to print for that tag (like "To Do:").
   */
  public static void setTagLabel(String name, String label)
  {
    if (!name.startsWith("@")) {
      name = "@" + name;
    }
    log.debug("Set label '" + label + "' for tag '" + name + "'");
    labels.setProperty(name, label);
  }

  /**
   * Returns the label for a given tag or an empty String, if it's an unknown
   * tag.
   *
   * @param name The name of the tag.
   * @return The label to print for that tag, or an empty string.
   */
  public static String getTagLabel(String name)
  {
    if (!name.startsWith("@")) {
      name = "@" + name;
    }
    String label = labels.getProperty(name);
    log.debug("Get label for '" + name + "': " + label);
    return label;
  }

  /**
   * Creates a taglist object.
   *
   * @param doc The doc with the tags (like a methoddoc).
   */
  public TagList(Doc doc)
  {
    Tag[] tags = doc.tags();
    if (tags != null && tags.length > 0) {
      // fill all tags into the hashtable
      for (int i = 0; i < tags.length; i++) {
        String name = tags[i].name();
        if (!isDisabled(name)) {
          // Do not print the "deprecated" tag separately for a class,
          // because it's printed right after the heritation tree
          // as a special remark already.
          if (!name.equalsIgnoreCase(DOC_TAGS_DEPRECATED)) {
            Tag[] namedTags = doc.tags(name);
            if (namedTags != null && namedTags.length > 0) {
              log.debug("Store tags for " + name);
              allTags.put(name, namedTags);
            }
          }
        }
      }
    }
  }

  /**
   *
   * @param name
   * @return
   */
  public Tag[] getTags(String name)
  {

    if (!name.startsWith("@")) {
      name = "@" + name;
    }
    log.debug("Tags for name: " + name);
    return (Tag[]) allTags.get(name);
  }

  /**
   * Returns a list of all tags with their names.
   *
   * @return The array with all tag names. If there are none, the array will
   * have 0 entries.
   */
  public String[] getTagNames()
  {
    Enumeration names = allTags.keys();
    ArrayList nameList = new ArrayList();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      log.debug("Add tag name: " + name);
      nameList.add(name);
    }
    return (String[]) nameList.toArray(new String[0]);
  }

  /**
   * Checks if a tag with a certain name is disabled and should not be printed.
   *
   * @param tag The tag to check.
   * @return True if the tag should not be printed.
   */
  private boolean isDisabled(String tag)
  {
    boolean disabled = false;
    String comp = tag;
    if (comp.startsWith("@")) {
      comp = comp.substring(1, comp.length());
    }
    if (comp.equalsIgnoreCase(DOC_TAG_VERSION)) {
      if (!Configuration.isShowVersionActive()) {
        disabled = true;
      }
    }
    else if (comp.equalsIgnoreCase(DOC_TAG_AUTHOR)) {
      if (!Configuration.isShowAuthorActive()) {
        disabled = true;
      }
    }
    else if (comp.equalsIgnoreCase(DOC_TAG_SINCE)) {
      if (!Configuration.isShowSinceActive()) {
        disabled = true;
      }
    }
    else {
      // custom tags are disabled, if there is no label defined for them
      if (getTagLabel(tag) == null) {
        log.warn("Custom tag not printed (no label defined): " + tag);
        disabled = true;
      }
    }
    return disabled;
  }
}

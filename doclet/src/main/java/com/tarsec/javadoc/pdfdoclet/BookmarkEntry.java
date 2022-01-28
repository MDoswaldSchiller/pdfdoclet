/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/BookmarkEntry.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: tetrade (zurich) ltd., Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.util.ArrayList;

/**
 * Represents one entry in the bookmarks outline tree.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class BookmarkEntry
{

  /**
   * Reference to the parent node.
   */
  private BookmarkEntry parent = null;

  /**
   * The bookmark label.
   */
  private String label = "";

  /**
   * Name of the internal destination.
   */
  private String destinationName = null;

  /**
   * List of child nodes.
   */
  private ArrayList children = new ArrayList();

  /**
   * Creates the root bookmark entry.
   */
  public BookmarkEntry()
  {
  }

  /**
   * Creates a bookmark entry with a certain label.
   *
   * @param label The label for the bookmark entry.
   * @param name Name of the internal destination.
   */
  public BookmarkEntry(String label, String name)
  {
    this.label = label;
    this.destinationName = name;
  }

  /**
   * Connects a child node to this entry.
   *
   * @param entry A sub-node of this bookmark entry.
   */
  public void addChild(BookmarkEntry entry)
  {
    if (entry == null) {
      throw new IllegalArgumentException("null entry not allowed!");
    }
    children.add(entry);
  }

  /**
   * Returns all children of this entry as an array. The order of the child
   * nodes in the array corresponds to the order in which they have been added
   * to this entry.
   *
   * @return The array with child nodes.
   */
  public BookmarkEntry[] getChildren()
  {
    return (BookmarkEntry[]) children.toArray(new BookmarkEntry[children.size()]);
  }

  /**
   * Returns the label of this bookmark entry or an empty string, if it was not
   * set.
   *
   * @return The label text or an empty string.
   */
  public String getLabel()
  {
    return label;
  }

  /**
   * Returns the name of the destination of this bookmark entry.
   *
   * @return The name of the destination.
   */
  public String getDestinationName()
  {
    return destinationName;
  }
}

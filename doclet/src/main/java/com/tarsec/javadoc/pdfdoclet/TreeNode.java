/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/TreeNode.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.util.Vector;

/**
 * Class used to build a hierachical derivation tree of all classes.
 */
public class TreeNode
{

  private static Vector treeResults = null;

  private String name = "";
  private TreeNode parent = null;
  private TreeNode[] next = new TreeNode[0];

  /**
   * Constructs a node with a given class name.
   *
   * @param name The qualified name of the class.
   */
  public TreeNode(String name)
  {
    this.name = name;
  }

  /**
   * Traverses the tree beginning from this node and returns all subnodes in an
   * array.
   *
   * @return All subnodes of this node.
   */
  public TreeNode[] getNodes()
  {
    // Initialize the static Vector which will hold the results
    treeResults = new Vector();
    getSubNodes();

    // Then put everything into an array
    TreeNode[] nodes = new TreeNode[treeResults.size()];
    nodes = (TreeNode[]) treeResults.toArray(nodes);

    return nodes;
  }

  /**
   * Traverses the tree beginning from this node and returns all parent nodes in
   * an array.
   *
   * @return All parent nodes of this node.
   */
  public TreeNode[] getParents()
  {
    // Initialize the static Vector which will hold the results
    treeResults = new Vector();
    getParentNodes();

    // Then put everything into an array
    TreeNode[] nodes = new TreeNode[treeResults.size()];
    nodes = (TreeNode[]) treeResults.toArray(nodes);

    return nodes;
  }

  /**
   * Collects all subnodes of this node in the static Vector object. This method
   * calls itself recursively on all subnodes.
   */
  private void getSubNodes()
  {
    TreeNode[] nodes = next();

    for (int i = 0; i < nodes.length; i++) {
      nodes[i].getSubNodes();
      treeResults.addElement(nodes[i]);
    }
  }

  /**
   * Collects all parent nodes of this node in the static Vector object. This
   * method calls itself recursively on all subnodes.
   */
  private void getParentNodes()
  {
    if (parent != null) {
      parent.getParentNodes();
      treeResults.addElement(parent);
    }
  }

  /**
   * Adds a subnode to this node.
   *
   * @param node The subnode to be added.
   */
  public void addNode(TreeNode node)
  {
    node.parent = this;

    TreeNode[] nodes = new TreeNode[this.next.length + 1];
    System.arraycopy(next, 0, nodes, 0, this.next.length);
    nodes[nodes.length - 1] = node;
    this.next = nodes;
  }

  /**
   * Returns a list of all direct subnodes of this node.
   *
   * @return All direct subnodes of this node.
   */
  public TreeNode[] next()
  {
    return this.next;
  }

  /**
   * Returns the the direct parent node of this node.
   *
   * @return The parent node.
   */
  private TreeNode parent()
  {
    return this.parent;
  }

  /**
   * Returns the qualified class name of this tree node.
   *
   * @return The qualified class name.
   */
  public String getName()
  {
    return this.name;
  }
}

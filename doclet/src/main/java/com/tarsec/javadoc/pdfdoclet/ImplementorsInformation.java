/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;

/**
 * This class processes an entire javadoc tree and stores information about
 * classes (their known subclasses) and interfaces (known implementing classes).
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class ImplementorsInformation implements IConstants
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(ImplementorsInformation.class);

  private static RootDoc rootDoc = null;
  private static Hashtable implementors = new Hashtable();

  /**
   * Constructor which takes a root of a javadoc to be processed entirely.
   *
   * @param docRoot The javadoc root object.
   */
  public static void initialize(RootDoc docRoot)
  {
    rootDoc = docRoot;
  }

  /**
   * Traverses the entire javadoc tree and stores information about which
   * classes implement which interfaces, relations between sub- and superclasses
   * etc.
   */
  public static void collectInformation()
  {
    // 1st run: Create a node for every class
    ClassDoc[] classDocs = rootDoc.classes();

    // add overview file
    Destinations.addValidDestinationFile(JavadocUtil.getSourceFile(rootDoc));

    for (int u = 0; u < classDocs.length; u++) {
      TreeNode node = new TreeNode(classDocs[u].qualifiedName());
      implementors.put(classDocs[u].qualifiedName(), node);
    }

    for (int u = 0; u < classDocs.length; u++) {

      // Build list of all valid destinations
      // 1st, the class itself
      Destinations.addValidDestination(classDocs[u].qualifiedName());
      Destinations.addValidDestinationFile(JavadocUtil.getSourceFile(classDocs[u]));

      // 2nd, all its fields
      FieldDoc[] fields = classDocs[u].fields();
      for (int i = 0; i < fields.length; i++) {
        Destinations.addValidDestination(fields[i].qualifiedName());
      }
      // 3rd, all its constructors
      ConstructorDoc[] constructors = classDocs[u].constructors();
      for (int i = 0; i < constructors.length; i++) {
        Destinations.addValidDestination(constructors[i].qualifiedName());
        Destinations.addValidDestination(constructors[i].qualifiedName()
                                         + "()");
        Destinations.addValidDestination(constructors[i].qualifiedName()
                                         + constructors[i].signature());
        Destinations.addValidDestination(constructors[i].qualifiedName()
                                         + constructors[i].flatSignature());
      }
      // 4th, all methods
      MethodDoc[] methods = classDocs[u].methods();
      for (int i = 0; i < methods.length; i++) {
        Destinations.addValidDestination(methods[i].qualifiedName());
        Destinations.addValidDestination(methods[i].qualifiedName()
                                         + "()");
        Destinations.addValidDestination(methods[i].qualifiedName()
                                         + methods[i].signature());
        Destinations.addValidDestination(methods[i].qualifiedName()
                                         + methods[i].flatSignature());
      }
      // 5th, its package
      if (classDocs[u].containingPackage().isIncluded()) {
        PackageDoc packageDoc = classDocs[u].containingPackage();
        Destinations.addValidDestination(packageDoc.name());
        Destinations.addValidDestinationFile(JavadocUtil.getSourceFile(packageDoc));
      }

      // Now collect inheritance relation information
      if (classDocs[u].isInterface()) {
        // Check if interface has superinterfaces
        ClassDoc[] superDocs = classDocs[u].interfaces();

        if ((superDocs != null) && (superDocs.length > 0)) {

          for (int no = 0; no < superDocs.length; no++) {
            ClassDoc superDoc = superDocs[no];
            String name = superDoc.qualifiedName();

            if (implementors.get(name) != null) {
              // If yes, connect it with the parent class in the tree
              TreeNode parentNode = (TreeNode) implementors.get(name);
              TreeNode node
                  = (TreeNode) implementors.get(
                      classDocs[u].qualifiedName());
              parentNode.addNode(node);
            }
          }

        }
      }
      else {
        // Check if class has a superclass
        ClassDoc superDoc = classDocs[u].superclass();

        if (superDoc != null) {
          // Since the superclass may be OUTSIDE this package
          // (like java.lang.Object) check if the superclass
          // also as a node.
          String name = superDoc.qualifiedName();

          if (implementors.get(name) != null) {
            // If yes, connect it with the parent class in the tree
            TreeNode parentNode = (TreeNode) implementors.get(name);
            TreeNode node
                = (TreeNode) implementors.get(
                    classDocs[u].qualifiedName());
            parentNode.addNode(node);
          }
        }
      }
    }
  }

  /**
   * Returns a list of names of classes that implement a certain interfaces.
   *
   * @param interfaceName The name of the interface in question.
   * @return An array of class name Strings
   */
  public static String[] getImplementingClasses(String interfaceName)
  {
    List implementingClassesList = new ArrayList();

    //get all the classes
    ClassDoc[] classes = rootDoc.classes();

    for (int i = 0; i < classes.length; i++) {
      ClassDoc aClass = classes[i];
      if (!aClass.isInterface()) {
        ClassDoc[] interfaces = aClass.interfaces();
        for (int j = 0; j < interfaces.length; j++) {
          ClassDoc anInterface = interfaces[j];

          if (!aClass.isInterface()
              && anInterface.qualifiedName().equalsIgnoreCase(
                  interfaceName)) {
            implementingClassesList.add(aClass.qualifiedName());
          }
        }
      }
    }

    String[] returnvalue = new String[implementingClassesList.size()];

    return (String[]) implementingClassesList.toArray(returnvalue);
  }

  /**
   * Returns a list of names of classes that are subclasses of a certain given
   * class.
   *
   * @param className The (super)class in question.
   * @return An array of class name Strings. If there are no subclasses, the
   * array has a length of 0 (zero entries).
   */
  public static String[] getKnownSubclasses(String className)
  {
    log.debug("Get known subclasses for: " + className);
    TreeNode node = (TreeNode) implementors.get(className);
    TreeNode[] nodes = node.getNodes();
    String[] result = new String[nodes.length];

    for (int i = 0; i < result.length; i++) {
      result[i] = nodes[i].getName();
    }

    return result;
  }

  /**
   * Returns a list of names of classes that are direct subclasses of a certain
   * given class.
   *
   * @param className The (super)class in question.
   * @return An array of class name Strings. If there are no subclasses, the
   * array has a length of 0 (zero entries).
   */
  public static String[] getDirectSubclasses(String className)
  {
    log.debug("Get direct subclasses for: " + className);
    TreeNode node = (TreeNode) implementors.get(className);
    TreeNode[] nodes = node.next();
    String[] result = new String[nodes.length];

    for (int i = 0; i < result.length; i++) {
      result[i] = nodes[i].getName();
    }

    return result;
  }

  /**
   * Returns a list of names of classes that are superclasses of a certain given
   * class.
   *
   * @param className The (sub)class in question.
   * @return An array of class name Strings. If there are no superclasses, the
   * array has a length of 0 (zero entries).
   */
  public static String[] getKnownSuperclasses(String className)
  {
    log.debug("Get known superclasses for: " + className);
    TreeNode node = (TreeNode) implementors.get(className);
    TreeNode[] nodes = node.getParents();
    String[] result = new String[nodes.length];

    for (int i = 0; i < result.length; i++) {
      result[i] = nodes[i].getName();
    }

    return result;
  }
}

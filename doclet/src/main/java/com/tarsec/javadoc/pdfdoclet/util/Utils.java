/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tarsec.javadoc.pdfdoclet.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author mdo
 */
public class Utils
{
  /**
   * List of package prefixes (built at startup time).
   */
  private static String[] PACKAGE_PREFIXES = {"com.tarsec."};
  
  
  public static PackageElement getPackage(TypeElement type)
  {
    Element packageElement = type;

    do {
      packageElement = packageElement.getEnclosingElement();
    }
    while (!(packageElement instanceof PackageElement) && packageElement != null);

    return (PackageElement) packageElement;
  }
  
  
  public static String getClassModifiers(TypeElement classDoc)
  {
    String info = "";

    
//    if (classDoc.isPublic()) {
//      info = "public ";
//    }
//
//    if (classDoc.isPrivate()) {
//      info = "private ";
//    }
//
//    if (classDoc.isProtected()) {
//      info = "protected ";
//    }
//
//    if (!classDoc.isInterface()) {
//      if (classDoc.isStatic()) {
//        info = info + "static ";
//      }
//
//      if (classDoc.isFinal()) {
//        info = info + "final ";
//      }
//
//      if (classDoc.isAbstract()) {
//        info = info + "abstract ";
//      }
//
//      info = info + "class ";
//    }
//    else {
//      info = info + "interface ";
//    }

    return info;
  }

  public static String getQualifiedNameIfNecessary(TypeElement classDoc)
  {
    String name = classDoc.getSimpleName().toString();
    boolean isExternal = true;

    for (int i = 0; (i < PACKAGE_PREFIXES.length) && isExternal; i++) {
      if (classDoc.getQualifiedName().toString().startsWith(PACKAGE_PREFIXES[i])) {
        isExternal = false;
      }
    }

    if (isExternal) {
      name = classDoc.getQualifiedName().toString();
    }

    return name;
  }
  
    /**
   * Utility method which returns the name of a class or interface. For classes
   * and interfaces of external packages, the fully qualified name is returned;
   * for such of of the same package, only the short name is returned.
   *
   * @param qualifiedName The qualified name of the class or interface.
   * @return The short or fully qualifed name of the class or interface.
   */
  public static String getQualifiedNameIfNecessary(String qualifiedName)
  {
    String name = qualifiedName;
    boolean isExternal = true;

    for (int i = 0; (i < PACKAGE_PREFIXES.length) && isExternal; i++) {
      if (qualifiedName.startsWith(PACKAGE_PREFIXES[i])) {
        isExternal = false;
      }
    }

    if ((isExternal == false) && (qualifiedName.indexOf(".") != -1)) {
      name = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1,
                                     qualifiedName.length());
    }

    return name;
  }
  
  public static List<TypeElement> createSorted(List<TypeElement> original)
  {
    List<TypeElement> sorted = new ArrayList<>(original);
    sorted.sort(Comparator.comparing(t -> t.getSimpleName().toString(), String.CASE_INSENSITIVE_ORDER));
    return sorted;
  }  
}
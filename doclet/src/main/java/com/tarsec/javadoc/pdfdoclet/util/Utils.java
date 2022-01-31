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
  public static PackageElement getPackage(TypeElement type)
  {
    Element packageElement = type;

    do {
      packageElement = packageElement.getEnclosingElement();
    }
    while (!(packageElement instanceof PackageElement) && packageElement != null);

    return (PackageElement) packageElement;
  }
  
  public static List<TypeElement> createSorted(List<TypeElement> original)
  {
    List<TypeElement> sorted = new ArrayList<>(original);
    sorted.sort(Comparator.comparing(t -> t.getSimpleName().toString(), String.CASE_INSENSITIVE_ORDER));
    return sorted;
  }  
}

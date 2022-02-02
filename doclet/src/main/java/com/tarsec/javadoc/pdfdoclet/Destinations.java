/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/Destinations.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.sun.javadoc.ExecutableMemberDoc;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.zip.CRC32;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class keeps a list of valid destinations for internal links to avoid
 * problems when the PDF is created.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class Destinations implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(Destinations.class);

  /**
   * Stores the names of all valid destinations.
   */
  private static final Properties destinations = new Properties();

  /**
   * Stores the names of the .html files that we process
   */
  private static final HashSet destinationFiles = new HashSet();

  /**
   * Adds a file that can be linked to with an HTML anchor tag. Generally these
   * are package.html files, class files, overview, title page, and appendicies.
   */
  public static void addValidDestinationFile(File aFile)
  {
    if (aFile != null && aFile.exists() && aFile.isFile()) {
      try {
        LOG.debug("Adding destination file " + aFile);
        destinationFiles.add(aFile.getCanonicalPath());
      }
      catch (IOException e) {
        LOG.debug("Error adding destination file " + aFile, e);
      }
    }
  }

  /**
   * Checks to see if the given file is a valid destination for an HTML anchor.
   */
  public static boolean isValidDestinationFile(File aFile)
  {
    if (aFile == null) {
      return false;
    }
    try {
      return destinationFiles.contains(aFile.getCanonicalPath());
    }
    catch (IOException e) {
      LOG.debug("Error checking destination file " + aFile, e);
      return false;
    }
  }

  /**
   * Adds a valid destination to the list.
   *
   * @param destination The valid destination.
   */
  public static void addValidDestination(String destination)
  {
    LOG.debug("Add: {}", destination);
    destinations.setProperty(destination, "x");
  }

  /**
   * Verifies if a given destination is valid.
   *
   * @param destination The local destination to check.
   * @return True if the destination is valid, false if not.
   * @throws IllegalArgumentException If the argument is null.
   */
  public static boolean isValid(String destination)
  {
    if (destination == null) {
      throw new IllegalArgumentException("null destination not allowed!");
    }
    if (destinations.get(destination) != null) {
      LOG.debug("Is valid: " + destination);
      return true;
    }
    LOG.debug("Is invalid: " + destination);
    return false;
  }

  /**
   * Return a destination based on an HTML anchor name and a filename (we do
   * this in case the same anchor name occurs in more than one included file).
   */
  public static String createAnchorDestination(File file, String htmlAnchor)
  {
    String fileHash = "";

    if (file != null) {
      String filePath;
      try {
        filePath = file.getCanonicalPath();
      }
      catch (IOException e) {
        filePath = file.getAbsolutePath();
      }
      /*
             * Use a hash value to represent the filename... it's shorter, and
             * does not encode path information from the original hard drive
             * into the PDF.
       */
      CRC32 crc = new CRC32();
      crc.update(filePath.getBytes());
      fileHash = Long.toHexString(crc.getValue());
    }
    if (htmlAnchor == null) {
      htmlAnchor = "";
    }

    return "_LOCAL:" + fileHash + ":" + htmlAnchor.trim();
  }
  
  public static Phrase createDestination(String label, TypeElement doc, Font font)
  {
    boolean multiPart = false;
    Chunk chunk = null;
    boolean canHaveParms = false;
    if (doc.getKind() == ElementKind.CONSTRUCTOR || doc.getKind() == ElementKind.METHOD) {
      canHaveParms = true;
      LOG.debug("is method or constructor.");
    }

    if (canHaveParms) {
      multiPart = true;
      LOG.debug("Create multi-part link.");
    }
    chunk = new Chunk(label, font);
    Phrase phrase = new Phrase(chunk);

    String destination = doc.getQualifiedName().toString();
    LOG.debug("Create destination 1: " + destination);
    phrase.add(PDFUtil.createAnchor(destination));
    if (multiPart) {
      ExecutableMemberDoc execDoc = (ExecutableMemberDoc) doc;
      String destinationTwo = destination + "()";
      LOG.debug("Create destination 2: " + destinationTwo);
      phrase.add(PDFUtil.createAnchor(destinationTwo));

      String destinationThree = destination + execDoc.signature();
      LOG.debug("Create destination 3: " + destinationThree);
      phrase.add(PDFUtil.createAnchor(destinationThree));

      String destinationFour = destination + execDoc.flatSignature();
      LOG.debug("Create destination 4: " + destinationFour);
      phrase.add(PDFUtil.createAnchor(destinationFour));
    }

    return phrase;
  }
  
  public static Phrase createMethodDestination(TypeElement type, ExecutableElement method, String caption, Font font)
  {
    String linkDestination = getMethodLinkQualifier(type, method);
    LOG.debug("Create destination 1: " + linkDestination);

    Chunk chunk = new Chunk(caption, font);
    Phrase phrase = new Phrase(chunk);
    phrase.add(PDFUtil.createAnchor(linkDestination));
    
    return phrase;
  }
  
  public static String getMethodLinkQualifier(TypeElement type, ExecutableElement method)
  {
    StringBuilder destination = new StringBuilder();
    destination.append(type.getQualifiedName());
    destination.append('.');
    destination.append(method.getSimpleName());
    destination.append('[');
    for (VariableElement param : method.getParameters()) {
      destination.append(param.asType()).append(':');
    }
    destination.append(']');
    
    return destination.toString();
  }
  
  
  
  public static Phrase createFieldDestination(TypeElement type, VariableElement field, Font font)
  {
    Chunk chunk = new Chunk(field.getSimpleName().toString(), font);
    Phrase phrase = new Phrase(chunk);

    String destination = getFieldLinkQualifier(type, field);
    LOG.debug("Create destination 1: " + destination);
    phrase.add(PDFUtil.createAnchor(destination));

    return phrase;
  }
  
  public static String getFieldLinkQualifier(TypeElement type, VariableElement field)
  {
    return String.format("%s.%s", type.getQualifiedName(), field.getSimpleName());
  }
}

/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/Destinations.java,v 1.1 2007/07/18 22:15:10 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.zip.CRC32;
import javax.lang.model.element.TypeElement;
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
  private static Properties destinations = new Properties();

  /**
   * Stores the names of the .html files that we process
   */
  private static HashSet destinationFiles = new HashSet();

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
    LOG.debug("Add: " + destination);
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

  /**
   * Creates a destination in the document. This method does nothing if the
   * given destination is invalid.
   * <p>
   * This method creates a phrase with one or two empty chunks who only serve as
   * holders for the destinations.
   *
   * @param label The label for the destination.
   * @param doc The javadoc element for which to create a destination.
   * @param font The font for the label.
   * @return The Phrase with the destination in it.
   */
  public static Phrase createDestination(String label, ProgramElementDoc doc, Font font)
  {
    boolean multiPart = false;
    Chunk chunk = null;
    boolean canHaveParms = false;
    if (doc instanceof ConstructorDoc
        || doc instanceof MethodDoc) {
      canHaveParms = true;
      LOG.debug("is method or constructor.");
    }

    if (canHaveParms) {
      multiPart = true;
      LOG.debug("Create multi-part link.");
    }
    chunk = new Chunk(label, font);
    Phrase phrase = new Phrase(chunk);

    String destination = doc.qualifiedName();
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

  
  public static Phrase createDestination(String label, TypeElement doc, Font font)
  {
    boolean multiPart = false;
    Chunk chunk = null;
    boolean canHaveParms = false;
    if (doc instanceof ConstructorDoc
        || doc instanceof MethodDoc) {
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
  
  /**
   * Creates a link to a destination in the document. This method does nothing
   * if the given destination is invalid.
   *
   * @param destination The destination for the link.
   */
  public static void createLinkTo(ProgramElementDoc doc, Chunk chunk)
  {
    String destination = doc.qualifiedName();
    if (destinations.get(destination) != null) {
      LOG.debug("Create link to: " + destination);
      chunk.setLocalGoto(destination);
    }
  }
}

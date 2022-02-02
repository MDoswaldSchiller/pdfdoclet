/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and alphabetical index at the end of the API document.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Index implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(Index.class);

  /**
   * Container for member index information
   */
  private final Map<String,Set<Integer>> memberIndex = new HashMap<>();

  /**
   * Holds a list of all classes, methods, constructors and fields.
   */
  private final Set<String> memberList = new TreeSet<>();
  private Document pdfDocument = null;
  private PdfWriter pdfWriter = null;

  private static Index theInstance;
  
  /**
   * Constructs an Index object for a given document.
   *
   * @param writer The PdfWriter used to create the document.
   * @param document The PDF document.
   */
  public Index(PdfWriter writer, Document document)
  {
    this.pdfWriter = writer;
    this.pdfDocument = document;
    
    theInstance = this;
  }

  public static Index getInstance()
  {
    return theInstance;
  }
  
  /**
   * Adds a member (field or method) to the internal list used for the index
   * creation.
   *
   * @param memberName The fully qualified name of the member of the current
   * class.
   */
  public void addToMemberList(CharSequence memberName)
  {
    LOG.debug(">");
    addMemberWithPage(memberName.toString(), State.getCurrentPage());
    LOG.debug("<");
  }
  
  public void addToMemberList(CharSequence memberName, int page)
  {
    addMemberWithPage(memberName.toString(), page);
  }
  
  private void addMemberWithPage(String memberName, int page)
  {
    String memberShortName = memberName.substring(memberName.lastIndexOf(".") + 1, memberName.length());
    if (memberIndex.get(memberShortName) == null) {
      memberIndex.put(memberShortName, new TreeSet());
    }
    
    Set<Integer> list = memberIndex.get(memberName);
    list.add(page);
    
    memberList.add(memberShortName);
  }
  
  

  /**
   * Returns an Iterator for iterating through a sorted list of all page numbers
   * for a given member name.
   *
   * @param memberName The short, unqualified member name.
   * @return The iterator for the sorted page numbers (Integer objects).
   */
  private Set<Integer> getSortedPageNumbers(String memberName)
  {
    return memberIndex.get(memberName);
  }

  /**
   * Creates a simple alphabetical index of all classes and members of the API.
   *
   * @throws Exception If the Index could not be created.
   */
  public void create() throws Exception
  {
    LOG.debug(">");

    if (!Configuration.isCreateIndexActive()) {
      LOG.debug("Index creation disabled.");
      return;
    }

    LOG.debug("** Start creating Index...");

    State.setCurrentHeaderType(HEADER_INDEX);
    State.increasePackageChapter();

    // Name of the package (large font)
    pdfDocument.newPage();

    // Create "Index" bookmark
    String label = Configuration.getProperty(ARG_LB_OUTLINE_INDEX, LB_INDEX);
    String dest = "INDEX:";
//    Bookmarks.addRootBookmark(label, dest);
    Chunk indexChunk = new Chunk(label, Fonts.getFont(TIMES_ROMAN, BOLD, 30));
    indexChunk.setLocalDestination(dest);

    Paragraph indexParagraph = new Paragraph((float) 30.0, indexChunk);

    pdfDocument.add(indexParagraph);

    // we grab the ContentByte and do some stuff with it
    PdfContentByte cb = pdfWriter.getDirectContent();
    ColumnText ct = new ColumnText(cb);
    ct.setLeading((float) 9.0);

    float[] right = {70, 320};
    float[] left = {300, 550};

    // fill index columns with text
    String letter = "";
    Set<String> keys = memberList;

    // keys must be sorted case unsensitive
    List<String> sortedKeys = new ArrayList(keys);
    Collections.sort(sortedKeys, String.CASE_INSENSITIVE_ORDER);

    for (String memberName : sortedKeys) {
      String currentLetter = memberName.substring(0, 1).toUpperCase();
      LOG.debug("Create index entry for " + memberName);

      // Check if next letter in alphabet is reached
      if (currentLetter.equalsIgnoreCase(letter) == false) {
        // If yes, switch to new letter and print it
        letter = currentLetter.toUpperCase();
        Paragraph lphrase = new Paragraph((float) 13.0);
        lphrase.add(new Chunk("\n\n" + letter + "\n",
                              Fonts.getFont(TIMES_ROMAN, 12)));
        ct.addText(lphrase);
      }

      // Print member name
      Paragraph phrase = new Paragraph((float) 10.0);
      phrase.add(new Chunk("\n" + memberName + "  ", Fonts.getFont(TIMES_ROMAN, 9)));

      boolean firstNo = true;
      for (Integer pageNo : getSortedPageNumbers(memberName)) {
        // Always add 1 to the stored value, because the pages were
        // counted beginning with 0 internally, but their visible
        // numbering starts with 1
        String pageNumberText = String.valueOf(pageNo + 1);
        if (!firstNo) {
          phrase.add(new Chunk(", ", Fonts.getFont(TIMES_ROMAN, 9)));
        }
        phrase.add(new Chunk(pageNumberText, Fonts.getFont(TIMES_ROMAN, 9)));
        firstNo = false;
      }

      ct.addText(phrase);
    }

    // Now print index by printing columns into document
    int status = 0;
    int column = 0;

    while ((status & ColumnText.NO_MORE_TEXT) == 0) {
      ct.setSimpleColumn(right[column], 60, left[column], 790, 16,
                         Element.ALIGN_LEFT);
      status = ct.go();

      if ((status & ColumnText.NO_MORE_COLUMN) != 0) {
        column++;

        if (column > 1) {
          pdfDocument.newPage();
          column = 0;
        }
      }
    }

    LOG.debug("** Index created.");

    LOG.debug("<");
  }

  /**
   * Inner class used to sort member page numbers.
   */
  class PageNumberSorter implements Comparator
  {

    /**
     * Implements the Comparator interface. Makes sure that the page numbers of
     * a member are sorted in ascending order.
     *
     * @param o1 The first entry for the comparison.
     * @param o2 The second entry for the comparison.
     * @return A value defining the order.
     */
    @Override
    public int compare(Object o1, Object o2)
    {
      Integer int1 = (Integer) o1;
      Integer int2 = (Integer) o2;
      int result = -1;
      if (int1.intValue() == int2.intValue()) {
        result = 0;
      }
      if (int1.intValue() > int2.intValue()) {
        result = 1;
      }
      return result;
    }
  }
}

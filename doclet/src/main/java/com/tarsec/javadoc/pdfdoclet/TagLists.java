/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.tarsec.javadoc.pdfdoclet.elements.CellNoBorderNoPadding;
import com.tarsec.javadoc.pdfdoclet.html.HtmlParserWrapper;
import com.tarsec.javadoc.pdfdoclet.util.JavadocUtil;
import com.tarsec.javadoc.pdfdoclet.util.PDFUtil;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prints class and member tag lists. These are javadoc tags like "author" etc.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class TagLists implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(TagLists.class);

  /**
   *
   *
   * @param classDoc
   * @throws Exception
   */
  public static void printClassTags(ClassDoc classDoc) throws Exception
  {
    LOG.debug("> class: " + classDoc.name());
    // Get list of all tags
    TagList list = new TagList(classDoc);
    String[] names = list.getTagNames();

    // Are there any tags to print?
    for (int i = 0; i < names.length; i++) {
      String tagLabel = TagList.getTagLabel(names[i]);
      Tag[] tags = list.getTags(names[i]);
      if (tags == null) {
        LOG.error("NO TAGS FOR: " + names[i]);
      }
      printTags(tagLabel, tags, true, false);
    }
    LOG.debug("<");
  }

  /**
   * Prints one single tag type, for example "Throws" or "Parameters" for a
   * method.
   *
   * @param list The tag list object containing all tags of that type.
   * @param tagName The name of the tag type ("param", "throws"..).
   * @throws Exception If something failed.
   */
  private static void printTag(TagList list, String tagName) throws Exception
  {

    Tag[] paramTags = list.getTags(tagName);
    if (paramTags != null) {
      String tagLabel = TagList.getTagLabel(tagName);
      printTags(tagLabel, paramTags, false, true);
    }
  }

  /**
   * Takes a list of tag names and clears it of certain default method tags
   * (like "Parameters", "Throws" etc.).
   *
   * @param names The list of all tags of a method.
   * @return The list with only the additional tags.
   */
  private static String[] clearTagsFromList(String[] names)
  {
    ArrayList newList = new ArrayList();
    int counter = 0;
    for (int i = 0; i < names.length; i++) {
      // XXX what about DOC_TAG_SEE?
      if (names[i].equals(DOC_TAG_EXCEPTION) == false
          && names[i].equals(DOC_TAG_RETURN) == false
          && names[i].equals(DOC_TAG_THROWS) == false
          && names[i].equals(DOC_TAG_SINCE) == false
          && names[i].equals(DOC_TAG_PARAM)) {
        newList.add(names[i]);
        counter++;
      }
    }
    return (String[]) newList.toArray(new String[counter]);
  }

  /**
   * Print all tags for a member (method).
   *
   * @param doc The doc for which to print tags.
   * @throws Exception
   */
  public static void printMemberTags(Doc doc) throws Exception
  {
    LOG.debug("> member: " + doc.name());
    // Get list of all tags
    TagList list = new TagList(doc);

    String[] names = list.getTagNames();
    // Are there any tags to print?
    if (names.length > 0) {
      String[] customNames = clearTagsFromList(names);
      if (doc instanceof ExecutableMemberDoc) {
        ExecutableMemberDoc execDoc = (ExecutableMemberDoc) doc;
        // First, print "Parameter" tags
        printMemberTags(TagList.getTagLabel(DOC_TAG_PARAM), execDoc.paramTags());
        // Then print "Returns" tags
        printTag(list, DOC_TAG_RETURN);
        // Then print "Throws"/"Exception" tags
        printMemberTags(TagList.getTagLabel(DOC_TAG_THROWS), execDoc.throwsTags());
      }
      // Then print "See Also" tags
      printMemberTags(TagList.getTagLabel(DOC_TAG_SEE), doc.seeTags());
      // Then print "Since" tags
      printTag(list, DOC_TAG_SINCE);

      // Then print custom tags
      for (int i = 0; i < customNames.length; i++) {
        String tagLabel = TagList.getTagLabel(customNames[i]);
        Tag[] tags = list.getTags(customNames[i]);
        if (tags == null) {
          LOG.error("NO TAGS FOR: " + customNames[i]);
        }
        printTags(tagLabel, tags, false, true);
      }
    }
    LOG.debug("<");
  }

  /**
   * Prints tags of a class member (method, field).
   *
   * @param title The bold face title text for the tag (like "Parameters:")
   * @param tags The list of tags to be printed.
   * @param isKeyValue Some tags, like exceptions, consist of two parts. The
   * first is the first word - the exception name - and the second is the rest,
   * which is the explanation for the exception. This parameter designates the
   * tag(s) to be of this type if true.
   * @throws Exception
   */
  public static void printMemberTags(String title, Tag[] tags) throws Exception
  {
    LOG.debug("> title: " + title);
    printTags(title, tags, false, true);
    LOG.debug("<");
  }

  /**
   * Prints tags of a class member (method, field).
   *
   * @param title The bold face title text for the tag (like "Parameters:")
   * @param tags The list of tags to be printed.
   * @param compress If true, the text of all the given tags will be
   * concatenated into one, comma separated. This is used for the author tag,
   * for example, where several separate author tags should be printed as one
   * only.
   * @param isMember If true, the whole tag paragraph is printed with additional
   * intendation (because it's a tag of a method, like the "Parameters:" tag).
   * @throws Exception
   */
  private static void printTags(String title, Tag[] tags,
                                boolean compress, boolean isMember) throws Exception
  {

    LOG.debug("> title: " + title);

    if ((tags != null) && (tags.length > 0)) {
      float[] widthsMember = {(float) 6.0, (float) 4.0, (float) 94.0};
      float[] widthsClass = {(float) 6.0, (float) 94.0};

      PdfPTable table = null;
      if (isMember) {
        table = new PdfPTable(widthsMember);
      }
      else {
        table = new PdfPTable(widthsClass);
      }
      table.setWidthPercentage((float) 100);

      Paragraph empty = new Paragraph(" ");

      // Add empty line after the title ("Parameters:" etc.)
      if (isMember) {
        table.addCell(new CellNoBorderNoPadding(empty));
        table.addCell(new CellNoBorderNoPadding(empty));
        table.addCell(new CellNoBorderNoPadding(empty));
      }

      PdfPCell titleCell = new CellNoBorderNoPadding(new Paragraph(
          (float) 24.0, title, Fonts.getFont(TIMES_ROMAN, BOLD, 10)));
      titleCell.setColspan(2);
      if (isMember) {
        table.addCell(new CellNoBorderNoPadding(empty)); // indentation
        // column
      }
      table.addCell(titleCell);

      int number = tags.length;
      String tagText = "";
      if (compress) {
        number = 1;
        for (int i = 0; i < tags.length; i++) {
          tagText = tagText + getTagText(tags[i]);
          if (i < tags.length - 1) {
            tagText = tagText + ", ";
          }
        }
      }

      for (int i = 0; i < number; i++) {

        // indentation columns
        if (isMember) {
          table.addCell(new CellNoBorderNoPadding(empty));
          table.addCell(new CellNoBorderNoPadding(empty));
        }
        else {
          table.addCell(new CellNoBorderNoPadding(empty));
        }

        if (!compress) {
          tagText = getTagText(tags[i]);
        }

        Element[] elements
            = HtmlParserWrapper.createPdfObjects(tagText);
        table.addCell(PDFUtil.createElementCell(0,
                                                Element.ALIGN_LEFT, elements));
      }

      // Add whole method block to document
      PDFDocument.instance().add(table);
    }
    LOG.debug("<");
  }

  /**
   * Returns the (HTML) text for a given Tag.
   *
   * @param tag The tag for which to return text for the document.
   * @return The text for the PDF document.
   */
  private static String getTagText(Tag tag)
  {

    if (tag instanceof SeeTag) {

      return JavadocUtil.formatSeeTag((SeeTag) tag);

    }
    else if (tag instanceof ParamTag) {

      ParamTag currParamTag = (ParamTag) tag;
      String name = "<code>" + currParamTag.parameterName() + "</code>";
      String pText = null;

      /*
             * In 1.4 at least, presense of {@inheritDoc}in param means that
             * the entire text gets replaced by the superclass' text.
       */
      while (true) {
        String pComment = currParamTag.parameterComment();
        if (pComment == null
            || pComment.indexOf(IConstants.DOC_INLINE_TAG_INHERITDOC) == -1) {
          pText = JavadocUtil.getComment(currParamTag.inlineTags());
          break;
        }
        pText = currParamTag.parameterComment();
        if (!(currParamTag.holder() instanceof MethodDoc)) {
          break;
        }
        MethodDoc methodDoc = (MethodDoc) currParamTag.holder();
        if (methodDoc.overriddenMethod() == null) {
          break;
        }
        Tag[] superTags = methodDoc.overriddenMethod().tags(tag.name());
        if (superTags == null || superTags.length == 0) {
          break;
        }
        if (!(superTags[0] instanceof ParamTag)) {
          break;
        }
        currParamTag = (ParamTag) superTags[0];
        // and repeat loop
      }

      if (pText != null && pText.length() > 0) {
        return name + " - " + pText;
      }
      else {
        return name;
      }

    }
    else if (tag instanceof ThrowsTag) {

      ClassDoc eDoc = ((ThrowsTag) tag).exception();
      String eName = ((ThrowsTag) tag).exceptionName();
      String eText = JavadocUtil.getComment(tag.inlineTags());
      String base;
      if (eDoc != null && Destinations.isValid(eDoc.qualifiedName())) {
        base = "<a href=\"locallink:" + eDoc.qualifiedName() + "\">"
               + eName + "</a>";
      }
      else {
        base = "<code>" + eName + "</code>";
      }
      if (eText != null && eText.length() > 0) {
        return base + " - " + eText;
      }
      else {
        return base;
      }

    }
    else {
      return JavadocUtil.getComment(tag.inlineTags());
    }
  }

}

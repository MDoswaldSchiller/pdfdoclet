package com.tarsec.javadoc.pdfdoclet.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.tarsec.javadoc.pdfdoclet.html.HTMLTagUtil;

public class TestProject extends TestCase
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(TestProject.class);

  public TestProject(String Name)
  {
    super(Name);
  }

  public void testTestTrivial()
  {
    assertEquals(1, 1);
  }

  public void testgetTagType()
  {
    int TagNumber;
    TagNumber = HTMLTagUtil.getTagType("<TABLE>");
    assertEquals(7, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("</TABLE>");
    assertEquals(7, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("< tAbLE>");
    assertEquals(7, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("tAbLE");
    assertEquals(7, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("</body>");
    assertEquals(0, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<p>");
    assertEquals(1, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<br>");
    assertEquals(2, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<code>");
    assertEquals(3, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<pre>");
    assertEquals(4, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<blockquote>");
    assertEquals(5, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<center>");
    assertEquals(6, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<tr>");
    assertEquals(8, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<td>");
    assertEquals(9, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<i>");
    assertEquals(10, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<b>");
    assertEquals(11, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<tt>");
    assertEquals(12, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<ul>");
    assertEquals(13, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<ol>");
    assertEquals(14, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<li>");
    assertEquals(15, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<em>");
    assertEquals(16, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<a href=anURL>");
    assertEquals(17, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<h1>");
    assertEquals(18, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<h2>");
    assertEquals(19, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<h3>");
    assertEquals(20, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<h4>");
    assertEquals(21, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<h5>");
    assertEquals(22, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<h6>");
    assertEquals(23, TagNumber);
    TagNumber = HTMLTagUtil.getTagType("<InvalidTag>");
    assertEquals(-1, TagNumber);
  }

  public static void main(String[] args)
  {
    junit.swingui.TestRunner.run(TestProject.class);
  }
}

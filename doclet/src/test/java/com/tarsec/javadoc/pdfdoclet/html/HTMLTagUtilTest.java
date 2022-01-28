/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/test/java/com/tarsec/javadoc/pdfdoclet/html/HTMLTagUtilTest.java,v 1.1 2007/07/18 22:15:49 marcelschoen Exp $
 * 
 * @Copyright: tetrade (zurich) ltd., Switzerland, 2004, All Rights Reserved.
 */

package com.tarsec.javadoc.pdfdoclet.html;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.w3c.tidy.Tidy;

/**
 * Unit tests for the HTML tag util class.
 * 
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class HTMLTagUtilTest extends TestCase {

    public void testClosingListTags() throws Exception {
        // Wrong string 
        String w1 = "blabla<ul><li>nr 1 <li> nr 2 </ul>";
        // Correct string 1
        String c1 = "<li>nr 1</li>";
        String c2 = "<li>nr 2</li>";

        Tidy parser = new Tidy();
        //parser.setCharEncoding(Configuration.UTF8);
        parser.setQuiet(true);
        parser.setEncloseBlockText(false);
        parser.setEncloseText(false);
        parser.setFixComments(false);
        parser.setForceOutput(true);
        parser.setDropEmptyParas(false);
        parser.setMakeClean(true);
        parser.setTrimEmptyElements(false);
        parser.setHideEndTags(false);
        parser.setShowWarnings(true);
        parser.setXmlTags(false);

        InputStream in = new ByteArrayInputStream(w1.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        parser.parse(in, out);
        String result = out.toString();
        System.out.println(result);
        int pos = result.indexOf(c1);
        int pos2 = result.indexOf(c2);
        assertTrue("List invalid ", (pos != -1) && (pos2 != -1));
    }
}

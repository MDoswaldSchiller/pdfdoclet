/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.html;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.net.URL;


import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.tarsec.javadoc.pdfdoclet.Configuration;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements external links (http://..)
 *
 * @version $Revision: 1.2 $
 * @author Marcel Schoen
 */
public class TagIMG extends HTMLTag
{
  private static final Logger LOG = LoggerFactory.getLogger(TagIMG.class);

  public TagIMG(HTMLTag parent, int type)
  {
    super(parent, type);
  }

  public Element[] toPdfObjects()
  {
    String src = getAttribute("src");
    Element[] results = new Element[0];
    Element result = null;

    if (src == null) {
      LOG.error("** Image tag has no 'src' attribute.");
      return null;
    }

    try {
      Image img = null;

      // now we use awt Image to create iText image instead of 
      // letting it read images by itself
      java.awt.Image awt = null;

      if (src.indexOf("://") > 0) {
        awt = Toolkit.getDefaultToolkit().createImage(new URL(src));
      }
      else {
        String filePath = Util.getFilePath(src);
        awt = Toolkit.getDefaultToolkit().createImage(filePath);
      }

      //img = Image.getInstance(PDFUtil.getFilePath(src));
      img = Image.getInstance(awt, null);

      // for the time being let's stick with A4
      Rectangle size = Configuration.getPageSize();
      String width = getAttribute("width");

      if (width != null) {
        // trying to cope with the resolution differences
        float w = Float.parseFloat(width) / 96 * 72;
        img.scaleAbsoluteWidth(w);
      }

      float maxW = size.width() - 100;

      if (img.plainWidth() > maxW) {
        img.scaleAbsoluteWidth(maxW);
      }

      String height = getAttribute("height");

      if (height != null) {
        // trying to cope with the resolution differences
        float h = Float.parseFloat(height) / 96 * 72;
        img.scaleAbsoluteHeight(h);
      }

      float maxH = size.height() - 100;

      if (img.plainHeight() > maxH) {
        img.scaleAbsoluteHeight(maxH);
      }
      /*
            Phrase chunk = new Phrase("", getFont());
            chunk.add(img);
            
            result = chunk;
       */
      result = img;

    }
    catch (FileNotFoundException e) {
      Util.error("** Image not found: " + src, e);
    }
    catch (Exception e) {
      Util.error("** Failed to read image: " + src, e);
    }

    if (result != null) {
      results = new Element[1];
      results[0] = result;
    }

    return results;
  }

  public Element[] openTagElements()
  {
    return null;
  }

  public Element[] closeTagElements()
  {
    return null;
  }
}

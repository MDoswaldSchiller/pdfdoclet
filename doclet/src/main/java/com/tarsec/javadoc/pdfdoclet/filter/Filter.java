/*
 * $Header: /cvsroot/pdfdoclet/pdfdoclet/doclet/src/main/java/com/tarsec/javadoc/pdfdoclet/filter/Filter.java,v 1.1 2007/07/18 22:15:08 marcelschoen Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */

package com.tarsec.javadoc.pdfdoclet.filter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.tarsec.javadoc.pdfdoclet.Configuration;
import com.tarsec.javadoc.pdfdoclet.IConstants;


/**
 * Utility class for filtering out classes and methods from
 * a print process using standard or custom tags.
 * 
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class Filter {

    /** Logger reference */
    private static Logger log = Logger.getLogger(Filter.class);

    /** Storage for all general filter tag names. */
    private static Hashtable filterTags = new Hashtable();

    /** Storage for names of all filter tag that must be check contentwise. */
    private static Properties filterTagsContent = new Properties();

    /**
     * Initializes the list of filter tags.
     */
    public static void initialize() {
        log.debug(">");
        if(Configuration.isFilterActive()) {
            log.debug("Filter is active!");
            String tags = Configuration.getProperty(IConstants.ARG_FILTER_TAGS,
                    "");
            // The tag names are in a comma separated list, so we use
            // a tokenizer to parse the list 
            StringTokenizer tok = new StringTokenizer(tags, ",");
            while(tok.hasMoreTokens()) {
                String tag = "@" + tok.nextToken();
                filterTags.put(tag, "X");
                log.debug("Filter tag: " + tag);
            }
            // Now check for specific filter tags
            Enumeration keys = Configuration.getConfiguration().keys();
            while(keys.hasMoreElements()) {
                String key = ((String)keys.nextElement()).trim();
                if(key.startsWith(IConstants.ARG_FILTER_TAG_PREFIX)) {
                    String tagName = "@" 
                        + key.substring(key.lastIndexOf(".") + 1, 
                                key.length());
                    String content = 
                        Configuration.getProperty(key, "").toLowerCase();
                    log.debug("Content filter tag '" + tagName + "': " 
                            + content);
                    filterTagsContent.setProperty(tagName, content);
                }
            }
        }
        log.debug("<");
    }
    
    /**
     * Checks if the given field doc must be filtered from the
     * list of field docs returned by a MethodDoc.
     *  
     * @param fieldDoc The fieldDoc to check.
     * @return True if it must be filtered (not printed).
     */
    public static boolean mustBeFiltered(FieldDoc fieldDoc) {
        log.debug(">");
        return mustBeFiltered(fieldDoc.tags());
    }
    
    /**
     * Checks if the given method doc must be filtered from the
     * list of method docs returned by a ClassDoc.
     *  
     * @param methodDoc The methodDoc to check.
     * @return True if it must be filtered (not printed).
     */
    public static boolean mustBeFiltered(MethodDoc methodDoc) {
        log.debug(">");
        return mustBeFiltered(methodDoc.tags());
    }
    
    /**
     * Checks if the given constructor doc must be filtered from the
     * list of constructor docs returned by a ClassDoc.
     *  
     * @param constructorDoc The constructorDoc to check.
     * @return True if it must be filtered (not printed).
     */
    public static boolean mustBeFiltered(ConstructorDoc constructorDoc) {
        log.debug(">");
        return mustBeFiltered(constructorDoc.tags());
    }
    
    /**
     * Checks if the given class doc must be filtered from the
     * list of class docs returned by a PackageDoc.
     *  
     * @param classDoc The classDoc to check.
     * @return True if it must be filtered (not printed).
     */
    public static boolean mustBeFiltered(ClassDoc classDoc) {
        log.debug(">");
        return mustBeFiltered(classDoc.tags());
    }
    
    /**
     * Checks if a given doc containing the given tags must be 
     * filtered from printing or not.
     *  
     * @param tag The tags to check.
     * @return True if it must be filtered (not printed).
     */
    private static boolean mustBeFiltered(Tag[] tags) {
        log.debug(">");
        for(int i = 0; i < tags.length; i++) {
            String tagName = tags[i].name();
            if(filterTags.get(tagName) != null) {
                // If we found one tag that is in the list of 
                // tags to filter after, this method must be printed.
                return false;
            }
            if(filterTagsContent.get(tagName) != null) {
                String correct = filterTagsContent.getProperty(tagName);
                String content = tags[i].text().toLowerCase();
                if(content.indexOf(correct) != -1) {
                    return false;
                }
            }
        }
        log.debug("<");
        return true;
    }
    
    /**
     * Creates a list of FilteredClassDoc elements for the
     * given input list of ClassDoc objects. The list itself
     * is also filtered according to the configuration.
     * 
     * @param input The list of ClassDoc objects to be filtered.
     * @return The filtered list of wrapper objects.
     */
    public static ClassDoc[] createFilteredClassesList(ClassDoc[] input)  {
        log.debug(">");
        FilteredClassDoc[] list = new FilteredClassDoc[input.length];
        for(int i = 0; i < input.length; i++) {
            list[i] = new FilteredClassDoc(input[i]);
        }
        // Otherwise, create a filtered list.
        ArrayList filteredList = new ArrayList();
        for(int i = 0; i < list.length; i++) {
            // If it doesn't have to be filtered out, add it to the list.
            if(!Filter.mustBeFiltered(list[i])) {
                filteredList.add(list[i]);
            }
        }
        ClassDoc[] result = new ClassDoc[filteredList.size()];
        log.debug("<");
        return (ClassDoc[])filteredList.toArray(result);
    }
    
    /**
     * Creates a list of ClassDoc elements for the
     * given input list of ClassDoc objects. The list  
     * is filtered according to the configuration.
     * 
     * @param input The list of ClassDoc objects to be filtered.
     * @return The filtered list of ClassDoc objects.
     */
    public static ClassDoc[] createClassesList(ClassDoc[] input)  {
        log.debug(">");
        // Otherwise, create a filtered list.
        ArrayList filteredList = new ArrayList();
        for(int i = 0; i < input.length; i++) {
            // If it doesn't have to be filtered out, add it to the list.
            if(!Filter.mustBeFiltered(input[i])) {
                filteredList.add(input[i]);
            }
        }
        ClassDoc[] result = new ClassDoc[filteredList.size()];
        log.debug("<");
        return (ClassDoc[])filteredList.toArray(result);
    }
    
    /**
     * Creates a list of MethodDoc elements for the
     * given input list of MethodDoc objects. The list  
     * is filtered according to the configuration.
     * 
     * @param input The list of MethodDoc objects to be filtered.
     * @return The filtered list of MethodDoc objects.
     */
    public static MethodDoc[] createMethodList(MethodDoc[] input)  {
        log.debug(">");
        
        // Otherwise, create a filtered list.
        ArrayList filteredList = new ArrayList();
        for(int i = 0; i < input.length; i++) {
            // If it doesn't have to be filtered out, add it to the list.
            if(!Filter.mustBeFiltered(input[i])) {
                filteredList.add(input[i]);
            }
        }
        MethodDoc[] result = new MethodDoc[filteredList.size()];
        log.debug("<");
        return (MethodDoc[])filteredList.toArray(result);
    }
    
    /**
     * Creates a list of FieldDoc elements for the
     * given input list of FieldDoc objects. The list  
     * is filtered according to the configuration.
     * 
     * @param input The list of FieldDoc objects to be filtered.
     * @return The filtered list of FieldDoc objects.
     */
    public static FieldDoc[] createFieldList(FieldDoc[] input)  {
        log.debug(">");
        // Otherwise, create a filtered list.
        ArrayList filteredList = new ArrayList();
        for(int i = 0; i < input.length; i++) {
            // If it doesn't have to be filtered out, add it to the list.
            if(!Filter.mustBeFiltered(input[i])) {
                filteredList.add(input[i]);
            }
        }
        FieldDoc[] result = new FieldDoc[filteredList.size()];
        log.debug("<");
        return (FieldDoc[])filteredList.toArray(result);
    }
    
    /**
     * Creates a list of ConstructorDoc elements for the
     * given input list of ConstructorDoc objects. The list  
     * is filtered according to the configuration.
     * 
     * @param input The list of ConstructorDoc objects to be filtered.
     * @return The filtered list of ConstructorDoc objects.
     */
    public static ConstructorDoc[] createConstructorList(ConstructorDoc[] input)  {
        log.debug(">");
        // Otherwise, create a filtered list.
        ArrayList filteredList = new ArrayList();
        for(int i = 0; i < input.length; i++) {
            // If it doesn't have to be filtered out, add it to the list.
            if(!Filter.mustBeFiltered(input[i])) {
                filteredList.add(input[i]);
            }
        }
        ConstructorDoc[] result = new ConstructorDoc[filteredList.size()];
        log.debug("<");
        return (ConstructorDoc[])filteredList.toArray(result);
    }

}

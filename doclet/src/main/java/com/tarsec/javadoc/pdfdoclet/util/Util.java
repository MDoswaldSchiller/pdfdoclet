/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tarsec.javadoc.pdfdoclet.Configuration;
import com.tarsec.javadoc.pdfdoclet.IConstants;
import com.tarsec.javadoc.pdfdoclet.State;

/**
 * General static utility methods.
 *
 * @version $Revision: 1.1 $
 * @author Marcel Schoen
 */
public class Util implements IConstants {

    /** Logger reference */
    private static Logger log = Logger.getLogger(Util.class);

    /**
     * Creates a String containing the specified number of "-" characters.
     * 
     * @param length The number of characters.
     * @return The String with the '-'-characters.
     */
    public static String getLine(int size) {
        StringBuffer line = new StringBuffer(size + 1);
        for(int i = 0; i < size; i++) {
            line.append("-");
        }
        return new String(line);
    }
    
    /**
     * Replaces all occurrences of a String in a given String.
     *  
     * @param content The String in which to search and replace.
     * @param toReplace The String to be replaced.
     * @param replaceWith The String to replace the search String with.
     * @return The new String.
     */
    public static String replace(String content, String toReplace, String replaceWith) {
        
        log.debug(">");

        log.debug("Content: " + content);
        log.debug("To replace: " + toReplace);
        log.debug("Replace with: " + replaceWith);
        
        if(toReplace.equals(replaceWith)) {
            log.warn("Replace-String is equal to the one to be replaced!");
            return content;
        }
        
        String result = content;
        int pos = result.indexOf(toReplace);
        while(pos != -1) {
            String firstPart = "", endPart = "";
            if(pos > 0) {
                firstPart = result.substring(0, pos);
            }
            if(pos + toReplace.length() < result.length()) {
                endPart = result.substring(pos + toReplace.length(), result.length());
            }
            result = firstPart + replaceWith + endPart;
            pos = result.indexOf(toReplace);
        }
        
        log.debug("<");
        
        return result;
    }
    
    /**
     * Method getFile returns the name of the file relative 
     * to the current package within the sourcepath.
     * 
     * @param filename The relative path of the file.
     * @return String The absolute path of the file.
     * @throws FileNotFoundException If no such file was found.
     */
    public static String getFilePath(String filename)
        throws FileNotFoundException
    {
        String foundPath = null;
        if(filename == null) {
            return ".";
        }
        
        foundPath = _getFilePath(filename);
        
        /*
         * See if a link is pointing to package-summary.html, the file generated from
         * package.html (the real source file) by Javadoc.  If so, return the path
         * to package.html, since that is the file we will have loaded.
         */
        String JAVADOC_PACKAGE_FILE = "package-summary.html";
        if (foundPath == null && filename.endsWith(JAVADOC_PACKAGE_FILE)) {
            String leadPath = filename.substring(0, filename.length()-JAVADOC_PACKAGE_FILE.length());
            String realPackageHtml = leadPath + "package.html";
            foundPath = _getFilePath(realPackageHtml);
        }
        
        /* Similar try for a link to class documentation. */
        if (foundPath == null && filename.endsWith(".html")) {
            String maybeJavaFile = filename.substring(0, filename.length()-5) + ".java";
            foundPath = _getFilePath(maybeJavaFile);
        }
        
        if (foundPath == null) {
            log.debug("Could not find file for "+filename);
            throw new FileNotFoundException("File: " + filename + " not found.");
        }

        log.debug("Searching for "+filename+" and found "+foundPath);
        return foundPath;
    }
    
    private static String _getFilePath(String fileName)
    {
        String filePath = null;

        // Handle {@docRoot} tag
        String DOC_ROOT = "{@docRoot}";
        if (fileName.startsWith(DOC_ROOT)) {
            // replace tag with value of work dir
            String newName = Configuration.getWorkDir();
            fileName =
                newName
                    + fileName.substring(DOC_ROOT.length(), fileName.length());
            File file = new File(fileName);
            if (file.exists()) {
                filePath = file.getAbsolutePath();
            }
        }

        /* Try relative to the current file, if we know it. */
        if (filePath == null) {
            File currFile = State.getCurrentFile();
            if (currFile != null) {
                File currDir = currFile.isDirectory()? currFile : currFile.getParentFile();
                File file = new File(currDir, fileName);
                if (file.exists()) {
                    return file.getAbsolutePath();
                }
            }
        }
/*
        // Handle "doc-files" directory
        if (fileName.startsWith(DOC_FILES)) {
            if (State.getCurrentPackage() != null) {
                ClassFile classFile =
                    Configuration.getSourcePath().getDirectory(
                        State.getCurrentPackage().replace(
                            '.',
                            File.separatorChar));
                if ((classFile != null) && classFile.exists()) {
                    filePath =
                        classFile.getAbsoluteName()
                            + File.separatorChar
                            + fileName;
                    return filePath;
                }
            }
        }

        if (State.getCurrentPackage() != null) {
            ClassFile classFile =
			Configuration.getSourcePath().getFile(
                    State.getCurrentPackage().replace('.', File.separatorChar)
                        + File.separatorChar
                        + fileName);

            if ((classFile != null) && classFile.exists()) {
                filePath = classFile.getAbsoluteName();
            }
        }
*/
        if (filePath == null) {
            File file = new File(Configuration.getWorkDir(), fileName);

            if (file.exists()) {
                filePath = file.getAbsolutePath();
            }
        }

        return filePath;
    }

    /**
     * Prints an error message to stdout with 
     * process status information.
     * 
     * @param text The error message text.
     */
    public static void error(String text) {
        log.error("****   ERROR: " + text + " ********");
        log.error("  ** Package: " + State.currentPackage);
        log.error("  **   Class: " + State.currentClass);
        log.error("  **  Method: " + State.currentMethod);
        log.error("  **  Member: " + State.currentMember);
        log.error("");
    }

    /**
     * Prints an error message to stdout with 
     * process status information.
     * 
     * @param text The error message text.
     * @param throwable Some causing throwable
     */
    public static void error(String text, Throwable throwable) {
        log.error("****   ERROR: " + text + " ********");
        log.error("  ** Package: " + State.currentPackage);
        log.error("  **   Class: " + State.currentClass);
        log.error("  **  Method: " + State.currentMethod);
        log.error("  **  Member: " + State.currentMember);
        log.debug("  ** Causing: " + throwable.toString(), throwable);
        log.error("");
    }

    /**
     * Utility method for printing / logging info.
     * Prints only if debug mode is on.
     *
     * @param text The text to be printed.
     */
    public static void debug(String text) {
        log.debug(text);
    }

    /**
     * Initializes the log4j facility.
     */
    public static void initLog4j(boolean debug) {
        // clean away the old logfile
        File logFile = new File("./pdfdoclet.log");
        logFile.delete();
        
        Properties props = new Properties();

        // appender for console
        props.setProperty("log4j.appender.stout",
                "org.apache.log4j.ConsoleAppender");
        props.setProperty("log4j.appender.stout.layout",
                "org.apache.log4j.PatternLayout");
        props.setProperty("log4j.appender.stout.layout.ConversionPattern", 
                "%5p %C{1}:%M() - %m%n");

        // appender for logfile
        props.setProperty("log4j.appender.logfile",
            "org.apache.log4j.DailyRollingFileAppender");
        props.setProperty("log4j.appender.logfile.File", "./pdfdoclet.log");
        props.setProperty("log4j.appender.logfile.DatePattern", "yyyy-MM-dd");
        props.setProperty("log4j.appender.logfile.layout",
                "org.apache.log4j.PatternLayout");
        props.setProperty("log4j.appender.logfile.layout.ConversionPattern",
                "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p %C{1}:%M() - %m%n");

        if(debug) {
            props.setProperty("log4j.rootLogger", "DEBUG, logfile, stout");
            props.setProperty("log4j.appender.stout.Threshold", "INFO");
        } else {
            props.setProperty("log4j.rootLogger", "FATAL, stout");
            props.setProperty("log4j.appender.stout.Threshold", "FATAL");
        }

        PropertyConfigurator.configure(props);
    }

    /**
     * Removes carriage return and linefeed characters from
     * a given text String.
     *
     * @param text The original text from the source file.
     * @return The new text without any linefeed characters.
     */
    public static String stripLineFeeds(String text) {
        if (text.length() == 0) {
            return "";
        }

        char[] charArray = text.toCharArray();

        String leftBlank = "";
        String rightBlank = "";
        int ct = 0;

        while ((charArray[ct++] == ' ') && (ct < charArray.length)) {
            leftBlank = leftBlank + " ";
        }

        ct = charArray.length - 1;

        while ((charArray[ct--] == ' ') && (ct > -1)) {
            rightBlank = rightBlank + " ";
        }

        // Step 1:dCount number of parts
        int listSize = 0;

        for (int i = 0; i < charArray.length; i++) {
            if ((charArray[i] == '\n') || (charArray[i] == '\r')) {
                listSize++;
            }
        }

        listSize++;

        if (listSize == 1) {
            return text;
        }

        // Step 2: Create list
        int lastPos = 0;
        int partCt = 0;
        String[] parts = new String[listSize];
        int i = 0;

        for (i = 0; i < charArray.length; i++) {
            if ((charArray[i] == '\n') || (charArray[i] == '\r')) {
                if (i > (lastPos + 1)) {
                    parts[partCt] =
                        new String(charArray, lastPos, i - lastPos).trim();
                    partCt++;
                }

                lastPos = i;
            }
        }

        if (i > (lastPos + 1)) {
            parts[partCt] = new String(charArray, lastPos, i - lastPos).trim();
            partCt++;
        }

        String result = "";

        for (i = 0; i < partCt; i++) {
            result = result + parts[i] + " ";
        }

        result = leftBlank + result.trim() + rightBlank;

        return result;
    }
    
    /**
     * A case-insensitive version of the String.indexOf() method.
     * 
     * @param target The string in which to search. 
     * @param substring The string to look for.
     * @return int The position of the search string in the target string.
     *             If it was not found, -1 is returned.
     */
    public static int indexOfIgnoreCase(String target, String substring) {
        if (target == null || substring == null) {
            return -1;
        }

        int targetLength = target.length();
        int searchLength = substring.length();

        int spotsToSearch = targetLength - searchLength+1;
        for (int i=0; i<spotsToSearch; i++) {
            if (target.regionMatches(true, i, substring, 0, searchLength)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the content of the specified HTML file, and if there are
     * &lt;body&gt; ... &lt;/body&gt; tags, it returns the section inside
     * those tags.
     * 
     * @param htmlFile The HTML file to read.
     * @return The HTML content in one String.
     * @throws IOException If reading the file failed.
     */
    public static String getHTMLBodyContent(File htmlFile) throws IOException {
        
        BufferedReader rd = new BufferedReader(new FileReader(htmlFile));
        StringBuffer buffer = new StringBuffer();
        char charBuffer[] = new char[2048];
        int numRead = 0;

        do {
            numRead = rd.read(charBuffer);
            if (numRead > 0) {
                buffer.append(charBuffer, 0, numRead);
            }
        } while (numRead >= 0);
        rd.close();

        String html = buffer.toString();
        int startIndex = indexOfIgnoreCase(html, "<body");
        int endIndex = -1;

        if (startIndex >= 0) {
            startIndex = html.indexOf('>', startIndex);
        }
        if (startIndex >= 0) {
            endIndex = indexOfIgnoreCase(html, "</body");
        }
        if (startIndex >= 0 && endIndex >= 0) {
            html = html.substring(startIndex+1, endIndex);
        }

        return html;
    }

}

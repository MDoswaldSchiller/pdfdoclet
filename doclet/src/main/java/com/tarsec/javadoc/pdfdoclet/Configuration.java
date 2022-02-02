/*
 * @Copyright: Marcel Schoen, Switzerland, 2004, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.tarsec.javadoc.pdfdoclet.util.Util;
import com.tarsec.javadoc.pdfdoclet.util.Version;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.tools.java.ClassPath;

/**
 * Handles the PDFDoclet configuration properties.
 *
 * @version $Revision: 1.3 $
 * @author Marcel Schoen
 */
public class Configuration implements IConstants
{
  private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

  /**
   * Default output filename constant
   */
  private final static String DEFAULT_FILENAME = "api.pdf";

  /**
   * Flag which determines if author should be printed
   */
  private static boolean isShowAuthorActive = false;

  /**
   * Flag which determines if method filtering is active.
   */
  private static boolean isFilterActive = false;

  /**
   * Flag which determines if version should be printed
   */
  private static boolean isShowVersionActive = false;

  /**
   * Flag which determines if since should be printed
   */
  private static boolean isShowSinceActive = false;

  /**
   * Flag which determines if summaries should be printed
   */
  private static boolean isShowSummaryActive = false;

  /**
   * Flag which determines if inherited summaries should be printed
   */
  private static boolean isShowInheritedSummaryActive = true;

  /**
   * Flag which determines if inherited summaries of external classes should be
   * printed
   */
  private static boolean isShowExternalInheritedSummaryActive = false;

  private static boolean isCreateIndexActive = true;
  
  /**
   * Flag which determines if a bookmark frame should be created
   */
  private static boolean isCreateFrame = false;

  /**
   * Stores package groups defined by the -group parameter.
   */
  private static Properties groups = new Properties();

  /**
   * Flag which determines if any internal links should be created in the
   * document
   */
  private static boolean isCreateLinksActive = true;

  /**
   * The working directory
   */
  private static String workDir = null;

  /**
   * The path for the source files
   */
  private static ClassPath sourcePath;

  /**
   * Holds the configuration properties
   */
  private static Properties configuration = new Properties();

  /**
   * Returns all configuration properties with a certain name prefix. This
   * method is useful to collect a list of a set of properties grouped by
   * numbering, for examle:
   * <pre>
   *   overview.file.1=...
   *   overview.file.2=...
   * </pre>
   *
   * @param prefix The property name prefix.
   * @return A Properties object with all properties found, or null, if none
   * were found.
   */
  public static String[] findNumberedProperties(String prefix)
  {
    int no = 0;
    Map pkgMap = new TreeMap();
    Enumeration keys = Configuration.getConfiguration().keys();
    // Find all properties with the prefix and store them
    // in the map to sort them alphabetically
    while (keys.hasMoreElements()) {
      String name = (String) keys.nextElement();
      if (name.startsWith(prefix)) {
        pkgMap.put(name, name);
      }
      no++;
    }

    // Now iterate alphabetically
    ArrayList result = new ArrayList();
    for (Iterator i = pkgMap.keySet().iterator(); i.hasNext();) {
      String name = (String) i.next();
      if (Configuration.hasProperty(name)) {
        String value = Configuration.getProperty(name);
        result.add(value);
      }
    }

    if (result.size() == 0) {
      return null;
    }
    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Returns a Properties object with all groups defined by the -group
   * parameter.
   * <p>
   * If no groups have been defined, the properties object is empty.
   *
   * @return A Properties object where each key is a group name (aka bookmark
   * label) and the value a list of colon-separated package names (see the
   * documentation of javadoc's -group parameter for details).
   */
  public static Properties getGroups()
  {
    return groups;
  }

  /**
   * Determines if links should be created.
   *
   * @return True if links should be created.
   */
  public static boolean isLinksCreationActive()
  {
    LOG.debug("Links creation active: " + isCreateLinksActive);
    return isCreateLinksActive;
  }

  /**
   * Determines if a bookmark frame should be created.
   *
   * @return True if the bookmark frame should be created.
   */
  public static boolean isCreateFrameActive()
  {
    LOG.debug("Create bookmark frame: " + isCreateFrame);
    return isCreateFrame;
  }

  /**
   * Determines if summary tables should be printed.
   *
   * @return True if method/field summary tables should be printed.
   */
  public static boolean isShowSummaryActive()
  {
    LOG.debug("Show summary: " + isShowSummaryActive);
    return isShowSummaryActive;
  }

  /**
   * Determines if inherited summary tables should be printed.
   *
   * @return True if summaries of inherited methods/fields should be printed.
   */
  public static boolean isShowInheritedSummaryActive()
  {
    LOG.debug("Show inherited summary: " + isShowInheritedSummaryActive);
    return isShowInheritedSummaryActive;
  }

  /**
   * Determines if inherited summary tables should be printed for external
   * classes.
   *
   * @return True if summaries of inherited external methods/fields should be
   * printed.
   */
  public static boolean isShowExternalInheritedSummaryActive()
  {
    LOG.debug("Show external inherited summary: " + isShowExternalInheritedSummaryActive);
    return isShowExternalInheritedSummaryActive;
  }

  /**
   * Determines if version should be printed.
   *
   * @return True if version should be printed.
   */
  public static boolean isShowVersionActive()
  {
    LOG.debug("Show version: " + isShowVersionActive);
    return isShowVersionActive;
  }

  /**
   * Determines if since should be printed.
   *
   * @return True if since should be printed.
   */
  public static boolean isShowSinceActive()
  {
    LOG.debug("Show sice: " + isShowSinceActive);
    return isShowSinceActive;
  }

  /**
   * Determines if summaries should be printed.
   *
   * @return True if summaries should be printed.
   */
  public static boolean isShowAuthorActive()
  {
    LOG.debug("Show author: " + isShowAuthorActive);
    return isShowAuthorActive;
  }

  /**
   * Determines if filtering (based on tags, usually custom tags) is active.
   *
   * @return True if filtering is active.
   */
  public static boolean isFilterActive()
  {
    LOG.debug("Filtering active: " + isFilterActive);
    return isFilterActive;
  }

  public static boolean isCreateIndexActive()
  {
    LOG.debug("Create index active: " + isCreateIndexActive);
    return isCreateIndexActive;
  }
  
  /**
   * Returns the String with the comma-separated list of packages which defines
   * the order of those packages in the bookmarks frame.
   *
   * @return The String with the comma-separated package list.
   */
  public static String getPackageOrder()
  {
    return configuration.getProperty(ARG_SORT);
  }

  /**
   * Method getWorkDir returns the current working directory used to resolve
   * relative paths while looking for files, like target document filename,
   * config file, title file name.
   *
   * @return String The working directory, WITHOUT a directory separating
   * character at the end
   */
  public static String getWorkDir()
  {
    if (workDir == null) {
      workDir = ".";
    }
    if (workDir.endsWith(File.separator)) {
      workDir = workDir.substring(0, workDir.length() - 1);
    }
    LOG.debug("Work dir: " + workDir);
    return workDir;
  }

  /**
   * Method getSourcePath returns the classpath for the source path.
   *
   * @return ClassPath
   */
  public static ClassPath getSourcePath()
  {
    return sourcePath;
  }

  /**
   * Returns the configuration properties of the PDF doclet.
   *
   * @return The Properties object with all configuration values.
   */
  public static Properties getConfiguration()
  {
    return configuration;
  }

  /**
   * Returns the value of a certain configuration property.
   *
   * @param name The name of the property.
   * @return The value of the property (may be null).
   */
  public static String getProperty(String name)
  {
    return configuration.getProperty(name);
  }

  /**
   * Returns the value of a certain configuration property.
   *
   * @param name The name of the property.
   * @param defaultValue The default value.
   * @return The value of the property (or the default value).
   */
  public static String getProperty(String name, String defaultValue)
  {
    return configuration.getProperty(name, defaultValue);
  }

  /**
   * Returns the configured page size to use. Defaults to A4 if none is
   * configured (with property "page.size").
   *
   * @return The page size to use.
   */
  public static Rectangle getPageSize()
  {
    Rectangle size = PageSize.A4;
    String pageSize = Configuration.getProperty(ARG_PAGE_SIZE, ARG_VAL_SIZE_A4);
    if (pageSize.equalsIgnoreCase(ARG_VAL_SIZE_LETTER)) {
      size = PageSize.LETTER;
    }

    if (Configuration.getProperty(ARG_PAGE_ORIENTATION, "normal").equals(ARG_VAL_LANDSCAPE)) {
      // Switch vertical and horizontal dimensions
      Rectangle landscape = new Rectangle(size.getHeight(), size.getWidth());
      size = landscape;
    }

    return size;
  }

  /**
   * Initializes the configuration by processing the input (commandline) options
   * and then optionally reading configuration values from a file.
   *
   * @param root The javadoc root object.
   * @return The name of the output file
   */
  public static void start(String[][] options) throws Exception
  {
    String configFilename = null;

    // If a config file was specified, process it first
    for (int i = 0; i < options.length; i++) {
      if ((options[i][0].equals("-" + ARG_CONFIG)) && (options[i].length > 1)) {
        configFilename = options[i][1];
      }
    }
    if (configFilename != null) {
      File configFile = new File(configFilename);

      if (!configFile.exists()) {
        configFile = new File(getWorkDir(), configFilename);
      }

      if (!configFile.exists()) {
        throw new RuntimeException("** Config file not found: "
                                   + configFilename + " **");
      }

      configuration.load(new FileInputStream(configFile));
    }

    // look for a config file and debug parameter first
    boolean log4jInitialized = false;
    if (getBooleanConfigValue(ARG_DEBUG, false)) {
      Util.initLog4j(true);
      log4jInitialized = true;
    }

    if (!log4jInitialized) {
      // by default, log on info level in console (file is always debug)
      Util.initLog4j(false);
    }

    // Log PDFDoclet version and build information
    String[] headers = Version.getHeaders();
    for (int i = 0; i < headers.length; i++) {
      LOG.info(headers[i]);
    }

    // Then process command line arguments to override file values
    for (int i = 0; i < options.length; i++) {
      LOG.debug(">> OPTION " + i + ": " + options[i][0]);

      if (options[i][0].startsWith("-")) {
        // Special handling of -group parameters for two reasons:
        // 1. this parameter can appear more than once and
        // 2. it has two arguments
        if (options[i][0].startsWith("-group")) {
          LOG.debug("-group options length: " + options[i].length);
          String groupName = options[i][1];
          String packages = options[i][2];
          LOG.debug(">> Config group: " + groupName + "=" + packages);
          Configuration.getGroups().setProperty(groupName, packages);
        }
        else {
          String propName = options[i][0];
          propName = propName.substring(1, propName.length());
          String propValue = "";
          if (options[i].length > 1) {
            propValue = options[i][1];
          }
          LOG.debug(">> Config property: " + propName + "=" + propValue);
          Configuration.getConfiguration().setProperty(propName, propValue);
        }
      }
    }

    processConfiguration();

  }

  /**
   * Processes the configuration Properties and sets internal values
   * accordingly.
   */
  private static void processConfiguration()
  {

    if (configuration.getProperty(ARG_SOURCEPATH) != null) {
      sourcePath = new ClassPath(configuration.getProperty(ARG_SOURCEPATH));
    }

    if (configuration.getProperty(ARG_FONT_TEXT_NAME) != null) {
      Fonts.mapFont(configuration.getProperty(ARG_FONT_TEXT_NAME),
                    configuration.getProperty(ARG_FONT_TEXT_ENC),
                    TIMES_ROMAN);
    }

    if (configuration.getProperty(ARG_WORKDIR) != null) {
      workDir = configuration.getProperty(ARG_WORKDIR);
    }

    // Some backward compatibility stuff
    if (Configuration.hasProperty("tag." + ARG_AUTHOR)) {
      isShowAuthorActive = getBooleanConfigValue("tag." + ARG_AUTHOR, false);
    }
    else {
      isShowAuthorActive = getBooleanConfigValue(ARG_AUTHOR, false);
    }
    if (Configuration.hasProperty("tag." + ARG_VERSION)) {
      isShowVersionActive = getBooleanConfigValue("tag." + ARG_VERSION, false);
    }
    else {
      isShowVersionActive = getBooleanConfigValue(ARG_VERSION, false);
    }
    if (Configuration.hasProperty("tag." + ARG_SINCE)) {
      isShowSinceActive = getBooleanConfigValue("tag." + ARG_SINCE, false);
    }
    else {
      isShowSinceActive = getBooleanConfigValue(ARG_SINCE, false);
    }

    isShowSummaryActive = getBooleanConfigValue(ARG_SUMMARY_TABLE, true);

    if (isShowSummaryActive) {
      String inheritedValue = getProperty(ARG_INHERITED_SUMMARY_TABLE, ARG_VAL_YES);
      if (inheritedValue.equalsIgnoreCase(ARG_VAL_INTERNAL)) {
        isShowInheritedSummaryActive = true;
        isShowExternalInheritedSummaryActive = false;
      }
      else {
        isShowInheritedSummaryActive = getBooleanConfigValue(ARG_INHERITED_SUMMARY_TABLE,
                                                             isShowSummaryActive);
        if (isShowInheritedSummaryActive) {
          // If 'inherited.summary.table' was set to 'yes',
          // external inherited members are printed as well
          isShowExternalInheritedSummaryActive = true;
        }
      }
    }
    else {
      isShowInheritedSummaryActive = false;
      isShowExternalInheritedSummaryActive = false;
    }

    isFilterActive = getBooleanConfigValue(ARG_FILTER, false);
    isCreateLinksActive = getBooleanConfigValue(ARG_CREATE_LINKS, true);
    isCreateFrame = getBooleanConfigValue(ARG_CREATE_FRAME, true);
    isCreateIndexActive = getBooleanConfigValue(ARG_CREATE_INDEX, true);
    LOG.debug("Show author tag: " + isShowAuthorActive);
    LOG.debug("Show version tag: " + isShowVersionActive);
    LOG.debug("Show since tag: " + isShowSinceActive);
    LOG.debug("Show summary tables: " + isShowSummaryActive);
    LOG.debug("Show inherited summary tables: " + isShowInheritedSummaryActive);
    LOG.debug("Show external inherited summary tables: " + isShowExternalInheritedSummaryActive);
    LOG.debug("Filter active: " + isFilterActive);
    LOG.debug("Create links: " + isCreateLinksActive);
    LOG.debug("Create index: " + isCreateIndexActive);
    LOG.debug("Create bookmarks frame: " + isCreateFrame);
  }

  /**
   * Checks if the given property exists in the configuration.
   *
   * @param property The name of the property.
   * @return True if such a property is available in the configuration, false if
   * not.
   */
  public static boolean hasProperty(String property)
  {
    if (Configuration.getProperty(property) == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns the boolean value of a configuration property. If the property has
   * a value of either "false" or "no", the method returns false. Otherwise it
   * returns true if the property exists, but has no value. If the property does
   * not exist at all, it returns the given default value.
   *
   * @param property The name of the configuration property.
   * @param defaultValue The default value for the property if it's not set.
   * @return The property's value.
   */
  public static boolean getBooleanConfigValue(String property, boolean defaultValue)
  {
    String value = Configuration.getProperty(property);
    boolean result = true;
    if (value != null) {
      if (value.equalsIgnoreCase("no")
          || value.equalsIgnoreCase("false")) {
        result = false;
      }
    }
    else {
      result = defaultValue;
    }
    return result;
  }
}

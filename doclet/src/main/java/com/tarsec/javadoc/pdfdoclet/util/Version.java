/*
 * @Copyright: Marcel Schoen, Switzerland, 2005, All Rights Reserved.
 */
package com.tarsec.javadoc.pdfdoclet.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Utility class for determining the library version and build information.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public final class Version
{

  /**
   * Logger reference
   */
  private static Logger log = Logger.getLogger(Version.class);

  /**
   * Avoid instantiation.
   */
  private Version()
  {
  }

  /**
   * Constant for undefined value.
   */
  public static final String UNKNOWN = "unknown";

  /**
   * Constant for name of the copyright property.
   */
  private static final String PROP_COPYRIGHT = "copyright";

  /**
   * Constant for name of the version property.
   */
  private static final String PROP_VERSION = "version";

  /**
   * Constant for name of the build property.
   */
  private static final String PROP_BUILD = "build";

  /**
   * Constant for name of the library name property.
   */
  private static final String PROP_NAME = "name";

  /**
   * Stores the copyright text string.
   */
  private static String copyright = UNKNOWN;

  /**
   * Stores the library name text string.
   */
  private static String name = UNKNOWN;

  /**
   * Stores the version information (like 1.3.0).
   */
  private static String version = UNKNOWN;

  /**
   * Stores the build information.
   */
  private static String build = UNKNOWN;

  /**
   * Initializes version, build and copyright information by reading the file
   * "version-info.properties" as a resource.
   */
  private static void init()
  {
    String packageName = Version.class.getPackage().getName();
    packageName = packageName.replace('.', '/');
    String resource = "/" + packageName + "/version-info.properties";
    try {

      InputStream in = Version.class.getResourceAsStream(resource);
      Properties props = new Properties();
      props.load(in);
      copyright = props.getProperty(PROP_COPYRIGHT, UNKNOWN).trim();
      version = props.getProperty(PROP_VERSION, UNKNOWN).trim();
      build = props.getProperty(PROP_BUILD, UNKNOWN).trim();
      name = props.getProperty(PROP_NAME, UNKNOWN).trim();

    }
    catch (Exception e) {
      // This should not happen, but for the sake of stability
      // do just use the default value here
      copyright = UNKNOWN;
      version = UNKNOWN;
      build = UNKNOWN;
      name = UNKNOWN;
    }
  }

  /**
   * Returns the name of the library.
   * <p>
   * This information is read from a properties file which is read using the
   * classloader resource mechanism. Therefore, that version properties file
   * must be in the classpath at the correct location.
   *
   * @return The library name string or "unknown", if the name information could
   * not be determined.
   */
  public static String getName()
  {
    if (name.equals(UNKNOWN)) {
      init();
    }
    return name;
  }

  /**
   * Returns the copyright information. This is a text string like:
   * <pre>
   * Copyright Marcel Schoen (zurich) ltd., Switzerland, 2003, All Rights Reserved.
   * </pre> This information is read from a properties file which is read using
   * the classloader resource mechanism. Therefore, that version properties file
   * must be in the classpath at the correct location.
   *
   * @return The copyright string or "unknown", if the copyright information
   * could not be determined.
   */
  public static String getCopyright()
  {
    if (copyright.equals(UNKNOWN)) {
      init();
    }
    return copyright;
  }

  /**
   * Returns the version information. This is a text string of the form
   * "[main].[minor].[maintenance]", for example "2.0.3".
   * <p>
   * This information is read from a properties file which is read using the
   * classloader resource mechanism. Therefore, that version properties file
   * must be in the classpath at the correct location.
   *
   * @return The version string, like "2.0.3", or "unknown", if the version
   * information could not be determined.
   */
  public static String getVersion()
  {
    if (version.equals(UNKNOWN)) {
      init();
    }
    return version;
  }

  /**
   * Returns the build information. This is a text string with some detailled
   * information about where/how/by whom etc. this library was built.
   * <p>
   * This text will be different automatically any time the JAR file is being
   * built, while the version may still be the same.
   *
   * @return The build string or "unknown", if the build information could not
   * be determined.
   */
  public static String getBuild()
  {
    if (build.equals(UNKNOWN)) {
      init();
    }
    return build;
  }

  /**
   * This method allows to run determine the version of this JAR file by using
   * the "java -jar" command (this class is the specified as main class in the
   * MANIFEST file).
   *
   * @param args The commandline arguments (irrelevant).
   */
  public static void main(final String[] args)
  {
    printHeaders();
  }

  /**
   * Prints the header lines to standard output.
   */
  public static void printHeaders()
  {
    String[] lines = getHeaders();
    for (int i = 0; i < lines.length; i++) {
      System.out.println(lines[i]);
    }
  }

  /**
   * Returns the header lines to be printed as an array of Strings. This is
   * useful for other classes, to print the output to a logfile etc.
   *
   * @return The version information header (5 lines, including the enclosing
   * lines of "-"-characters).
   */
  public static String[] getHeaders()
  {
    String[] lines = new String[5];
    lines[1] = getName() + ", Version " + getVersion();
    lines[2] = "Build: " + getBuild();
    lines[3] = getCopyright();

    // determine length of longest line
    int size = 0;
    for (int i = 1; i < 4; i++) {
      if (lines[i].length() > size) {
        size = lines[i].length();
      }
    }

    lines[0] = Util.getLine(size);
    lines[4] = Util.getLine(size);

    return lines;
  }
}

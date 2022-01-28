# Build-profile of Marcel Schoen on home-PC
#

JAVA_HOME=/stuff/java/j2se/jdk1.5.0_22

ANT_HOME=/stuff/java/ant/current
MAVEN_HOME=stuff/java/maven/maven-2/current
PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$MAVEN_HOME/bin:$PATH

CVSROOT=marcelschoen@cvs.pdfdoclet.sourceforge.net:/cvsroot/pdfdoclet
CVS_RSH=ssh

export JAVA_HOME ANT_HOME MAVEN_HOME PATH CVSROOT CVS_RSH


#!/bin/sh

##############################################################################
# Gradle start up script for POSIX
#########################################################################

APP_HOME=$( cd "${0%/*}" > /dev/null && pwd -P ) || exit
APP_BASE_NAME=${0##*/}

MAX_FD=maximum

warn () { echo "$*" >&2; }
die () { echo; echo "$*"; echo; exit 1; } >&2

cygwin=false; msys=false; darwin=false; nonstop=false
case "$( uname )" in
  CYGWIN* )         cygwin=true  ;;
  Darwin* )         darwin=true  ;;
  MSYS* | MINGW* )  msys=true    ;;
  NonStop* )        nonstop=true ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD=command -v java >/dev/null 2>&1 && echo "java" || die "ERROR: JAVA_HOME is not set"
fi

exec "$JAVACMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"

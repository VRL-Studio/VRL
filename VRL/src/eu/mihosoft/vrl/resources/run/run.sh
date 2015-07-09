#!/bin/bash

CALL_PATH=$(pwd)

APPDIR="$(dirname "$0")/.application"
cd "$APPDIR"
APPDIR="$(pwd)"

PROPERTY_FOLDER="property-folder/"
PROJECT_FILE="project.jar"

CONF="-property-folder $PROPERTY_FOLDER -plugin-checksum-test no -install-plugin-help no -install-payload-plugins yes"

OS=$(uname -a)


if [ "$1" == "-help" ]
then

cat << EOF

** Help: **

Basic Usage:	

./run.sh                      Starts VRL-Project

EOF

exit 0

fi

LIBDIR32="lib/linux/x86:custom-lib/linux/x86"
LIBDIR64="lib/linux/x64:custom-lib/linux/x64"
LIBDIROSX="lib/osx:custom-lib/osx"

if [[ $OS == *x86_64* ]]
then
  echo ">> detected x86 (64 bit) os"
  LIBDIR="$LIBDIR64:$LIBDIROSX"
  JAVAEXE="jre/x64/bin/java"
elif [[ $OS == *86* ]]
then
  echo ">> detected x86 (32 bit) os"
  LIBDIR="$LIBDIR32:$LIBDIROSX"
  JAVAEXE="jre/x86/bin/java"
else
  echo ">> unsupported architecture!"
  echo " --> executing installed java version"
  JAVAEXE="java"
fi

if [ ! -e $JAVAEXE ]
then
  echo ">> integrated jre not found!"
  echo " --> executing installed java version"
  JAVAEXE="java"
fi

if [[ $OS == *Darwin* ]]
then
  # ugly hack to enable vtk on osx
  export DYLD_LIBRARY_PATH="$PROPERTY_FOLDER/plugins/VRL-VTK/natives/osx/:$DYLD_LIBRARY_PATH"
fi
  
# optimized for jre 8 (09.07.2015)
$JAVAEXE -Xms256m -Xmx4096m -Djava.library.path="$LIBDIR" -jar "$PROJECT_FILE" $CONF --console-app-args $@

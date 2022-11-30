#!/bin/bash

DOWNLOAD=$1
if [ "--download" = "$DOWNLOAD" ] ; then
  echo "download"
  if [ ! -f ./packr-all.jar ] ; then
    wget https://github.com/libgdx/packr/releases/download/4.0.0/packr-all-4.0.0.jar
    mv packr-all*.jar packr-all.jar
    mkdir jre
    wget https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jdk_s390x_linux_hotspot_8u292b10.tar.gz
    mv *.tar.gz jre
    chmod u+x packr-all.jar
  fi
  exit 0
else 
  echo "build"
fi

java -jar packr-all.jar \
     --platform linux64 \
     --jdk ./jre/OpenJDK8U-jdk_s390x_linux_hotspot_8u292b10.tar.gz \
     --useZgcIfSupportedOs \
     --executable linux-executable-jndiff \
     --classpath ./target/jndiff.jar \
     --mainclass it.unibo.cs.ndiff.ui.Main \
     --vmargs Xmx1G \
     --resources src/main/resources \
     --output target/platform-exec

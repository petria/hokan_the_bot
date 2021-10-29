#!/bin/sh

MY_PID=$@

XMLTV_FILE="/usr/share/xmltv/xmltv.dtd"
BIN_FILE="tv_grab_fi"

CFG_FILE="cfg/tv_grab_fi.conf"
OUT_FILE="tmp/telkku_progs.xml"
TMP_FILE="tmp/telkku_progs.xml.tmp"

#OUT_FILE="tmp/$MY_PID.telkku_progs.xml"
#TMP_FILE="tmp/$MY_PID.telkku_progs.xml.tmp"

if [ -e $OUT_FILE ]
then
  exit 0
fi

cp -v $XMLTV_FILE tmp/

$BIN_FILE --config-file $CFG_FILE --output $TMP_FILE

cp -v $TMP_FILE $OUT_FILE

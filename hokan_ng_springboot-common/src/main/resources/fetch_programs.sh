#!/bin/sh

MY_PID=$@

QUIET="" # QUIET="--quiet"

XMLTV_FILE="/usr/share/xmltv/xmltv.dtd"
BIN_FILE="tv_grab_fi"

CFG_FILE="cfg/tv_grab_fi.conf"
OUT_FILE="tmp/telkku_progs.xml"
TMP_FILE="tmp/telkku_progs.xml.tmp"

#OUT_FILE="tmp/$MY_PID.telkku_progs.xml"
#TMP_FILE="tmp/$MY_PID.telkku_progs.xml.tmp"


echo "CFG_FILE=$CFG_FILE"
echo "OUT_FILE=$OUT_FILE"
echo "TMP_FILE=$TMP_FILE"


cp -v $XMLTV_FILE tmp/

$BIN_FILE --config-file $CFG_FILE --output $TMP_FILE $QUIET

cp -v $TMP_FILE $OUT_FILE

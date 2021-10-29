#!/bin/sh

grep -i $@ scripts/nsd_bbsss.txt | grep -v ";----;"

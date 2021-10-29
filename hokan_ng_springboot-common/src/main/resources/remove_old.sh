#!/bin/bash

cd backup/
mv `ls -t *.gz | head -5` tmp/
rm *.gz
mv tmp/*.gz .
cd -


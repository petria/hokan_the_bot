#!/bin/bash

BACKUP_FILE="backup/$(date +"%F_%T")_hokan_new_live.sql.gz"

echo "backup: $BACKUP_FILE"

## Binary path ##
MYSQL="/usr/bin/mysql"
MYSQLDUMP="/usr/bin/mysqldump"
GZIP="/bin/gzip"

## DB info ##
MUSER="hokan"
MPASS="hokan"
MHOST="192.168.0.60"
MDB="hokan_new_live"

echo "$MYSQLDUMP -u $MUSER -h $MHOST -p$MPASS $db | $GZIP -9 > $BACKUP_FILE"
$MYSQLDUMP -u $MUSER -h $MHOST -p$MPASS $MDB | $GZIP -9 > $BACKUP_FILE

sh >/dev/null scripts/remove_old.sh

echo "rsync -av --del backup/ $HOME/Dropbox/hokan_backup/"
rsync -av --del backup/ $HOME/Dropbox/hokan_backup/


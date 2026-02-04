#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -lt 6 ]; then
  echo "Usage: $0 <backup_file> <db_host> <db_port> <db_name> <db_user> <db_password>"
  exit 1
fi

BACKUP_FILE="$1"
DB_HOST="$2"
DB_PORT="$3"
DB_NAME="$4"
DB_USER="$5"
DB_PASSWORD="$6"

if [ ! -f "$BACKUP_FILE" ]; then
  echo "Backup file not found: $BACKUP_FILE"
  exit 1
fi

echo "Target database: $DB_NAME on $DB_HOST:$DB_PORT"
echo "Backup file: $BACKUP_FILE"
read -r -p "This will overwrite data in database '$DB_NAME'. Continue? (yes/NO): " CONFIRM1
if [ "$CONFIRM1" != "yes" ]; then
  echo "Aborted."
  exit 1
fi

read -r -p "Type 'RESTORE' to confirm full database restore: " CONFIRM2
if [ "$CONFIRM2" != "RESTORE" ]; then
  echo "Aborted."
  exit 1
fi

START_TIME=$(date +"%Y-%m-%d %H:%M:%S")
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < "$BACKUP_FILE"
EXIT_CODE=$?
END_TIME=$(date +"%Y-%m-%d %H:%M:%S")

LOG_FILE="$(dirname "$0")/restore-history.log"

if [ "$EXIT_CODE" -ne 0 ]; then
  echo "Restore failed with exit code $EXIT_CODE"
  echo "$START_TIME,$END_TIME,$DB_HOST,$DB_PORT,$DB_NAME,$BACKUP_FILE,$DB_USER,FAILED" >> "$LOG_FILE"
  exit "$EXIT_CODE"
fi

echo "Restore completed successfully."
echo "$START_TIME,$END_TIME,$DB_HOST,$DB_PORT,$DB_NAME,$BACKUP_FILE,$DB_USER,SUCCESS" >> "$LOG_FILE"


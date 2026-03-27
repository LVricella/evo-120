#!/bin/bash

BASE_OPT="$1"
SNAPSHOT_JSON="$2"
OUTPUT_OPT="$3"

if [ -z "$BASE_OPT" ] || [ -z "$SNAPSHOT_JSON" ] || [ -z "$OUTPUT_OPT" ]; then
  echo "Usage: $0 <base_opt> <snapshot_json> <output_opt>"
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_SRC_DIR="$(cd "$SCRIPT_DIR/../java/src" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

if [ ! -f "$BASE_OPT" ]; then
  echo "Base Option File not found: $BASE_OPT"
  exit 1
fi

if [ ! -f "$SNAPSHOT_JSON" ]; then
  echo "Snapshot JSON not found: $SNAPSHOT_JSON"
  exit 1
fi

cd "$JAVA_SRC_DIR" || exit 1

javac *.java
if [ $? -ne 0 ]; then
  echo "Java compilation failed."
  exit 1
fi

cd "$PROJECT_ROOT" || exit 1

java -cp "$JAVA_SRC_DIR" Main --base "$BASE_OPT" --snapshot "$SNAPSHOT_JSON" --output "$OUTPUT_OPT"
exit $?

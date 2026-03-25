#!/bin/bash

BASE_OPT="$1"
SNAPSHOT_JSON="$2"
OUTPUT_OPT="$3"

if [ -z "$BASE_OPT" ] || [ -z "$SNAPSHOT_JSON" ] || [ -z "$OUTPUT_OPT" ]; then
  echo "Usage: $0 <base_opt> <snapshot_json> <output_opt>"
  exit 1
fi

JAVA_SRC_DIR="$(cd "$(dirname "$0")/../java/src" && pwd)"

if [ ! -f "$BASE_OPT" ]; then
  echo "Base Option File not found: $BASE_OPT"
  exit 1
fi

if [ ! -f "$SNAPSHOT_JSON" ]; then
  echo "Snapshot JSON not found: $SNAPSHOT_JSON"
  exit 1
fi

cd "$JAVA_SRC_DIR" || exit 1

javac Main.java
if [ $? -ne 0 ]; then
  echo "Java compilation failed."
  exit 1
fi

java Main --base "$BASE_OPT" --snapshot "$SNAPSHOT_JSON" --output "$OUTPUT_OPT"
exit $?

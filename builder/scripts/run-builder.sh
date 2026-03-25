#!/bin/bash

# ==========================================
# run-builder.sh
# ==========================================
# Main builder wrapper.
#
# Current behavior:
# - if Java builder runner exists, use it
# - otherwise fallback to simple base OPT copy
# ==========================================

BASE_OPT="$1"
SNAPSHOT_JSON="$2"
OUTPUT_DIR="$3"

if [ -z "$BASE_OPT" ] || [ -z "$SNAPSHOT_JSON" ] || [ -z "$OUTPUT_DIR" ]; then
  echo "Usage: $0 <base_opt> <snapshot_json> <output_dir>"
  exit 1
fi

if [ ! -f "$BASE_OPT" ]; then
  echo "Base Option File not found: $BASE_OPT"
  exit 1
fi

if [ ! -f "$SNAPSHOT_JSON" ]; then
  echo "Snapshot JSON not found: $SNAPSHOT_JSON"
  exit 1
fi

mkdir -p "$OUTPUT_DIR"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_RUNNER="$SCRIPT_DIR/run-java-builder.sh"
OUTPUT_OPT="$OUTPUT_DIR/KONAMI-WIN32PES6OPT"

echo "=========================================="
echo "Option File Builder"
echo "=========================================="
echo "Base OPT:      $BASE_OPT"
echo "Snapshot JSON: $SNAPSHOT_JSON"
echo "Output dir:    $OUTPUT_DIR"
echo "=========================================="

if [ -f "$JAVA_RUNNER" ]; then
  bash "$JAVA_RUNNER" "$BASE_OPT" "$SNAPSHOT_JSON" "$OUTPUT_OPT"
  if [ $? -eq 0 ]; then
    echo "Java builder completed."
    exit 0
  fi

  echo "Java builder failed. Falling back to placeholder copy."
fi

cp "$BASE_OPT" "$OUTPUT_OPT"

echo "Placeholder copy completed."
echo "Output created at: $OUTPUT_OPT"

exit 0

#!/bin/bash

# ==========================================
# run-builder.sh
# ==========================================
# Placeholder builder runner.
#
# Future usage:
# ./run-builder.sh <base_opt> <snapshot_json> <output_dir>
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

echo "=========================================="
echo "Option File Builder"
echo "=========================================="
echo "Base OPT:      $BASE_OPT"
echo "Snapshot JSON: $SNAPSHOT_JSON"
echo "Output dir:    $OUTPUT_DIR"
echo "=========================================="

# Placeholder output
cp "$BASE_OPT" "$OUTPUT_DIR/KONAMI-WIN32PES6OPT"

echo "Builder placeholder completed."
echo "Output created at: $OUTPUT_DIR/KONAMI-WIN32PES6OPT"

exit 0

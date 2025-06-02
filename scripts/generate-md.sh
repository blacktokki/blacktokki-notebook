#!/bin/bash

SRC_DIR="src"
OUTPUT_FILE="scripts.md"

# 초기화
echo "" > "$OUTPUT_FILE"

# 파일 찾기
find "$SRC_DIR" -type f -name "*.java" | while read -r filepath; do
  echo "## $filepath" >> "$OUTPUT_FILE"
  echo '' >> "$OUTPUT_FILE"
  echo '```java' >> "$OUTPUT_FILE"
  cat "$filepath" >> "$OUTPUT_FILE"
  echo '```' >> "$OUTPUT_FILE"
  echo '' >> "$OUTPUT_FILE"
done

echo "✅ Markdown saved to $OUTPUT_FILE"
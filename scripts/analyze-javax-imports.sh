#!/bin/bash


set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_FILE="${PROJECT_ROOT}/javax-imports-analysis.txt"

echo "=== javax.* Import Analysis for Java Migration ===" > "$OUTPUT_FILE"
echo "Generated on: $(date)" >> "$OUTPUT_FILE"
echo "Project: Spring Boot RealWorld Example App" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

echo "Scanning for javax.* imports..." >&2

total_files=0
total_imports=0
validation_imports=0
servlet_imports=0
crypto_imports=0
other_imports=0

validation_file=$(mktemp)
servlet_file=$(mktemp)
crypto_file=$(mktemp)
other_file=$(mktemp)

find "$PROJECT_ROOT/src" -name "*.java" -type f | while read -r file; do
    if grep -n "import javax\." "$file" > /dev/null 2>&1; then
        echo "Processing: $file" >&2
        
        rel_path=${file#$PROJECT_ROOT/}
        
        grep -n "import javax\." "$file" | while IFS=: read -r line_num import_line; do
            if echo "$import_line" | grep -q "javax\.validation\|javax\.constraints"; then
                echo "$rel_path:$line_num:$import_line" >> "$validation_file"
            elif echo "$import_line" | grep -q "javax\.servlet"; then
                echo "$rel_path:$line_num:$import_line" >> "$servlet_file"
            elif echo "$import_line" | grep -q "javax\.crypto"; then
                echo "$rel_path:$line_num:$import_line" >> "$crypto_file"
            else
                echo "$rel_path:$line_num:$import_line" >> "$other_file"
            fi
        done
    fi
done

validation_count=$(wc -l < "$validation_file" 2>/dev/null || echo 0)
servlet_count=$(wc -l < "$servlet_file" 2>/dev/null || echo 0)
crypto_count=$(wc -l < "$crypto_file" 2>/dev/null || echo 0)
other_count=$(wc -l < "$other_file" 2>/dev/null || echo 0)
total_imports=$((validation_count + servlet_count + crypto_count + other_count))

echo "SUMMARY" >> "$OUTPUT_FILE"
echo "=======" >> "$OUTPUT_FILE"
echo "Total javax.* imports found: $total_imports" >> "$OUTPUT_FILE"
echo "  - Validation imports (javax.validation.*): $validation_count" >> "$OUTPUT_FILE"
echo "  - Servlet imports (javax.servlet.*): $servlet_count" >> "$OUTPUT_FILE"
echo "  - Crypto imports (javax.crypto.*): $crypto_count" >> "$OUTPUT_FILE"
echo "  - Other javax imports: $other_count" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

if [ "$validation_count" -gt 0 ]; then
    echo "VALIDATION IMPORTS (javax.validation.* -> jakarta.validation.*)" >> "$OUTPUT_FILE"
    echo "================================================================" >> "$OUTPUT_FILE"
    cat "$validation_file" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
fi

if [ "$servlet_count" -gt 0 ]; then
    echo "SERVLET IMPORTS (javax.servlet.* -> jakarta.servlet.*)" >> "$OUTPUT_FILE"
    echo "======================================================" >> "$OUTPUT_FILE"
    cat "$servlet_file" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
fi

if [ "$crypto_count" -gt 0 ]; then
    echo "CRYPTO IMPORTS (javax.crypto.* -> jakarta.crypto.*)" >> "$OUTPUT_FILE"
    echo "===================================================" >> "$OUTPUT_FILE"
    cat "$crypto_file" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
fi

if [ "$other_count" -gt 0 ]; then
    echo "OTHER JAVAX IMPORTS" >> "$OUTPUT_FILE"
    echo "==================" >> "$OUTPUT_FILE"
    cat "$other_file" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
fi

echo "MIGRATION RECOMMENDATIONS" >> "$OUTPUT_FILE"
echo "=========================" >> "$OUTPUT_FILE"
echo "1. javax.validation.* -> jakarta.validation.*" >> "$OUTPUT_FILE"
echo "   - Update @Valid, @NotBlank, @Email, @NotNull annotations" >> "$OUTPUT_FILE"
echo "   - Update ConstraintViolation and ConstraintViolationException" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "2. javax.servlet.* -> jakarta.servlet.*" >> "$OUTPUT_FILE"
echo "   - Update FilterChain, ServletException, HttpServletRequest, HttpServletResponse" >> "$OUTPUT_FILE"
echo "   - Critical for security filter functionality" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "3. javax.crypto.* -> jakarta.crypto.*" >> "$OUTPUT_FILE"
echo "   - Update SecretKey, SecretKeySpec for JWT functionality" >> "$OUTPUT_FILE"
echo "   - Test JWT operations thoroughly after migration" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

rm -f "$validation_file" "$servlet_file" "$crypto_file" "$other_file"

echo "Analysis complete. Results written to: $OUTPUT_FILE" >&2
echo "Found $total_imports javax.* imports across the codebase." >&2

if [ "$total_imports" -gt 0 ]; then
    exit 1
else
    exit 0
fi

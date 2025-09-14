#!/bin/bash


set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DB_FILE="${PROJECT_ROOT}/dev.db"
OUTPUT_FILE="${PROJECT_ROOT}/db-performance-baseline.txt"

echo "=== Database Performance Baseline ===" > "$OUTPUT_FILE"
echo "Generated on: $(date)" >> "$OUTPUT_FILE"
echo "Database: SQLite" >> "$OUTPUT_FILE"
echo "Database file: $DB_FILE" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

if [ ! -f "$DB_FILE" ]; then
    echo "Database file not found: $DB_FILE" >&2
    echo "Please run the application first to create the database." >&2
    exit 1
fi

measure_sql() {
    local description="$1"
    local sql="$2"
    local iterations=${3:-10}
    
    echo "Measuring: $description" >&2
    
    local total_time=0
    local times=()
    
    for ((i=1; i<=iterations; i++)); do
        local start_time=$(date +%s%N)
        sqlite3 "$DB_FILE" "$sql" > /dev/null 2>&1 || true
        local end_time=$(date +%s%N)
        local duration=$((end_time - start_time))
        local duration_ms=$((duration / 1000000))
        times+=($duration_ms)
        total_time=$((total_time + duration_ms))
    done
    
    local avg_time=$((total_time / iterations))
    
    IFS=$'\n' sorted=($(sort -n <<<"${times[*]}"))
    local p95_index=$(((iterations * 95) / 100))
    local p95_time=${sorted[$p95_index]}
    
    echo "$description:" >> "$OUTPUT_FILE"
    echo "  Average time: ${avg_time}ms" >> "$OUTPUT_FILE"
    echo "  95th percentile: ${p95_time}ms" >> "$OUTPUT_FILE"
    echo "  Iterations: $iterations" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
}

echo "Testing database connectivity..." >&2
if ! sqlite3 "$DB_FILE" "SELECT 1;" > /dev/null 2>&1; then
    echo "Cannot connect to database: $DB_FILE" >&2
    exit 1
fi

echo "OPERATION TIMINGS" >> "$OUTPUT_FILE"
echo "=================" >> "$OUTPUT_FILE"

measure_sql "Article table scan" "SELECT COUNT(*) FROM articles;"
measure_sql "Article by slug query" "SELECT * FROM articles WHERE slug = 'sample-article' LIMIT 1;"
measure_sql "Recent articles query" "SELECT * FROM articles ORDER BY created_at DESC LIMIT 10;"

measure_sql "User table scan" "SELECT COUNT(*) FROM users;"
measure_sql "User by email query" "SELECT * FROM users WHERE email = 'test@example.com' LIMIT 1;"
measure_sql "User by username query" "SELECT * FROM users WHERE username = 'testuser' LIMIT 1;"

measure_sql "Comment table scan" "SELECT COUNT(*) FROM comments;"
measure_sql "Comments by article query" "SELECT * FROM comments WHERE article_id = 'sample-id' ORDER BY created_at;"

measure_sql "Tag table scan" "SELECT COUNT(*) FROM tags;"
measure_sql "All tags query" "SELECT DISTINCT name FROM tags ORDER BY name;"

measure_sql "Article with tags join" "SELECT a.*, t.name FROM articles a LEFT JOIN article_tags at ON a.id = at.article_id LEFT JOIN tags t ON at.tag_id = t.id LIMIT 10;"
measure_sql "User favorites query" "SELECT a.* FROM articles a JOIN favorites f ON a.id = f.article_id WHERE f.user_id = 'sample-user-id';"

echo "SUMMARY" >> "$OUTPUT_FILE"
echo "=======" >> "$OUTPUT_FILE"
echo "Database performance baseline completed." >> "$OUTPUT_FILE"
echo "Use these metrics to compare performance after Java migration." >> "$OUTPUT_FILE"
echo "Alert if any operation shows >10% regression." >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

echo "Database performance baseline completed. Results in: $OUTPUT_FILE" >&2

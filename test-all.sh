#!/usr/bin/env bash
set -e

modules=(
    "blaise-common"
    "blaise-graphics"
    "blaise-graph-theory"
    "blaise-json"
    "blaise-graphics-ui"
    "blaise-graph-theory-ui"
    "blaise-svg"
)

root="$(cd "$(dirname "$0")" && pwd)"

for module in "${modules[@]}"; do
    echo ""
    echo "==> Testing $module"
    (cd "$root/$module" && mvn test)
done

echo ""
echo "All modules tested successfully."

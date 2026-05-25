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
    echo "==> Compiling $module"
    (cd "$root/$module" && mvn compile)
done

echo ""
echo "All modules compiled successfully."

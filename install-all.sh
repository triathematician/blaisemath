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
    echo "==> Clean-installing $module"
    (cd "$root/$module" && mvn clean install)
done

echo ""
echo "All modules installed successfully."

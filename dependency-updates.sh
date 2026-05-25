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
    echo "==> Dependency updates for $module"
    (cd "$root/$module" && mvn versions:display-dependency-updates)
done

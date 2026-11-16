#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Running ShellCheck on .github/scripts/ ==="
shellcheck "${SCRIPT_DIR}"/*.sh

echo "=== Running Bats Unit Tests ==="
bats "${SCRIPT_DIR}"/tests/*.bats

echo "=== All script tests passed successfully! ==="

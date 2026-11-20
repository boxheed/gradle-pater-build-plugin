#!/bin/bash
set -e
echo "Installing spec-kit (specify-cli) using uv..."
export PATH="$PATH:$HOME/.local/bin"
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git
echo "spec-kit (specify-cli) installed successfully."

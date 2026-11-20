#!/bin/bash
set -e
echo "Installing antigravity cli..."
curl -fsSL https://antigravity.google/cli/install.sh | bash
echo 'export PATH="/home/vscode/.local/bin:$PATH"' >> ~/.bashrc && source ~/.bashrc
echo "antigravity cli installed."
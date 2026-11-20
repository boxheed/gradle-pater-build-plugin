#!/bin/bash
set -e
echo "Fixing permissions for /workspace..."
sudo chown -R vscode:vscode /workspace
echo "Permissions fixed."
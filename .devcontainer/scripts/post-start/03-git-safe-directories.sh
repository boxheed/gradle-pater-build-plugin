#!/bin/bash
set -e
echo "Setting git safe directories..."
for folder in /workspace/*; do
    if [ -d "$folder/.git" ]; then
        git config --global --add safe.directory "$folder"
    fi
done
echo "Git safe directories set."

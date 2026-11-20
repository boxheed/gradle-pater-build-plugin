#!/bin/bash
set -e
# Update package lists and install python3-venv, python3-pip, and pipx
echo "Updating package lists and installing python3-venv, python3-pip, and pipx..."
sudo apt-get update
sudo apt-get install -y python3-venv python3-pip pipx

# Install uv using pipx
echo "Installing uv..."
# Ensure pipx's path is correctly configured for the current session.
curl -LsSf https://astral.sh/uv/install.sh | sh
echo "uv installed successfully."

#!/bin/bash
set -e
echo "Fixing permissions for .ssh directory..."
sudo chown -R vscode:vscode ~/.ssh
chmod 700 ~/.ssh
chmod 600 ~/.ssh/*
echo "Permissions fixed for .ssh directory."
#!/bin/bash

set -e
for SCRIPT in "$1"/*; do
    if [ -f "$SCRIPT" ] && [ -x "$SCRIPT" ]; then
        echo "Running script: $SCRIPT"
        "$SCRIPT"
    fi
done

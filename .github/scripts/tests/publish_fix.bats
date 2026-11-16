#!/usr/bin/env bats

setup() {
  TEST_TEMP_DIR="$(mktemp -d)"
  OUTPUT_DIR="$(mktemp -d)"
  REMOTE_DIR="$(mktemp -d)"
  MOCK_BIN="$(mktemp -d)"
  export GITHUB_OUTPUT="${OUTPUT_DIR}/github_output"
  touch "$GITHUB_OUTPUT"
  export SCRIPT_PATH="$(cd "${BATS_TEST_DIRNAME}/.." && pwd)/publish_fix.sh"

  # Initialize bare remote repo
  git init --bare -q "${REMOTE_DIR}/origin.git"

  # Create an isolated clean git repository
  cd "$TEST_TEMP_DIR"
  git init -q -b develop
  git config user.name "test-user"
  git config user.email "test@example.com"
  git remote add origin "${REMOTE_DIR}/origin.git"
  echo "initial" > init.txt
  git add init.txt
  git commit -q -m "initial commit"
  git push -q -u origin develop

  # Mock gh outside repo directory
  export PATH="${MOCK_BIN}:${PATH}"
  cat << 'EOF' > "${MOCK_BIN}/gh"
#!/bin/sh
if [ "$1" = "pr" ] && [ "$2" = "list" ]; then
  echo ""
  exit 0
fi
if [ "$1" = "pr" ] && [ "$2" = "create" ]; then
  echo "https://github.com/example/repo/pull/1"
  exit 0
fi
if [ "$1" = "pr" ] && [ "$2" = "edit" ]; then
  echo "Updated PR"
  exit 0
fi
EOF
  chmod +x "${MOCK_BIN}/gh"
}

teardown() {
  rm -rf "$TEST_TEMP_DIR" "$OUTPUT_DIR" "$REMOTE_DIR" "$MOCK_BIN"
}

@test "publish_fix exits with error when git working tree is clean" {
  cd "$TEST_TEMP_DIR"
  run bash "$SCRIPT_PATH"
  [ "$status" -eq 1 ]
  run grep "remediated=false" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "publish_fix commits and creates PR when modifications exist" {
  cd "$TEST_TEMP_DIR"
  echo "modified dependency" >> build.gradle
  export TARGET_BRANCH="develop"
  export VULN_IDS="GHSA-test-1234"
  export GITHUB_EVENT_NAME="schedule"

  run bash "$SCRIPT_PATH"
  [ "$status" -eq 0 ]
  run grep "remediated=true" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]

  # Check that branch fix/remediate-osv-develop exists on remote
  run git ls-remote --heads origin fix/remediate-osv-develop
  [ "$status" -eq 0 ]
  [ -n "$output" ]
}

@test "publish_fix updates existing PR when open PR already exists" {
  cd "$TEST_TEMP_DIR"
  echo "modified dependency again" >> build.gradle
  export TARGET_BRANCH="develop"
  export VULN_IDS="GHSA-test-5678"
  export GITHUB_EVENT_NAME="schedule"

  # Return an existing open PR number 99
  cat << 'EOF' > "${MOCK_BIN}/gh"
#!/bin/sh
if [ "$1" = "pr" ] && [ "$2" = "list" ]; then
  echo "99"
  exit 0
fi
if [ "$1" = "pr" ] && [ "$2" = "edit" ]; then
  echo "Updated PR #99"
  exit 0
fi
EOF
  chmod +x "${MOCK_BIN}/gh"

  run bash "$SCRIPT_PATH"
  [ "$status" -eq 0 ]
  run grep "remediated=true" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

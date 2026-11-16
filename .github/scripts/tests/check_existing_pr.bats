#!/usr/bin/env bats

setup() {
  TEST_TEMP_DIR="$(mktemp -d)"
  export GITHUB_OUTPUT="${TEST_TEMP_DIR}/github_output"
  touch "$GITHUB_OUTPUT"
  SCRIPT_PATH="${BATS_TEST_DIRNAME}/../check_existing_pr.sh"

  # Create a mock bin directory for gh
  MOCK_BIN="${TEST_TEMP_DIR}/bin"
  mkdir -p "$MOCK_BIN"
  export PATH="${MOCK_BIN}:${PATH}"
}

teardown() {
  rm -rf "$TEST_TEMP_DIR"
}

@test "bypasses check when FORCE=true" {
  export FORCE="true"
  run "$SCRIPT_PATH" "develop" "GHSA-1111"
  [ "$status" -eq 0 ]
  run grep "skip_remediation=false" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "bypasses check when in pull_request event" {
  export GITHUB_EVENT_NAME="pull_request"
  run "$SCRIPT_PATH" "develop" "GHSA-1111"
  [ "$status" -eq 0 ]
  run grep "skip_remediation=false" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "proceeds with remediation when no open PR exists" {
  cat << 'EOF' > "${MOCK_BIN}/gh"
#!/bin/sh
exit 0
EOF
  chmod +x "${MOCK_BIN}/gh"

  run "$SCRIPT_PATH" "develop" "GHSA-1111"
  [ "$status" -eq 0 ]
  run grep "skip_remediation=false" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "skips remediation when open PR already covers all vulnerabilities" {
  cat << 'EOF' > "${MOCK_BIN}/gh"
#!/bin/sh
echo '{"number": 42, "title": "fix(deps): remediate GHSA-1111, GHSA-2222", "body": "Automated remediation generated for: GHSA-1111, GHSA-2222"}'
EOF
  chmod +x "${MOCK_BIN}/gh"

  run "$SCRIPT_PATH" "develop" "GHSA-1111, GHSA-2222"
  [ "$status" -eq 0 ]
  run grep "skip_remediation=true" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
  run grep "existing_pr_number=42" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "proceeds with remediation when open PR misses a new vulnerability" {
  cat << 'EOF' > "${MOCK_BIN}/gh"
#!/bin/sh
echo '{"number": 42, "title": "fix(deps): remediate GHSA-1111", "body": "Automated remediation generated for: GHSA-1111"}'
EOF
  chmod +x "${MOCK_BIN}/gh"

  run "$SCRIPT_PATH" "develop" "GHSA-1111, GHSA-3333"
  [ "$status" -eq 0 ]
  run grep "skip_remediation=false" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
  run grep "existing_pr_number=42" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

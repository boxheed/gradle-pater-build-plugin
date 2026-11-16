#!/usr/bin/env bats

setup() {
  TEST_TEMP_DIR="$(mktemp -d)"
  export GITHUB_OUTPUT="${TEST_TEMP_DIR}/github_output"
  touch "$GITHUB_OUTPUT"
  SCRIPT_PATH="${BATS_TEST_DIRNAME}/../parse_scan_report.sh"
}

teardown() {
  rm -rf "$TEST_TEMP_DIR"
}

@test "handles missing report file gracefully" {
  run "$SCRIPT_PATH" "${TEST_TEMP_DIR}/nonexistent.json"
  [ "$status" -eq 0 ]
  [ -f "$GITHUB_OUTPUT" ]
  run grep "has_vulnerabilities=false" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
  run grep "vuln_ids=" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "handles report with no vulnerabilities" {
  local report="${TEST_TEMP_DIR}/empty_report.json"
  cat << 'EOF' > "$report"
{
  "results": []
}
EOF

  run "$SCRIPT_PATH" "$report"
  [ "$status" -eq 0 ]
  run grep "has_vulnerabilities=false" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
  run grep "vuln_ids=" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "extracts and deduplicates vulnerability IDs" {
  local report="${TEST_TEMP_DIR}/vuln_report.json"
  cat << 'EOF' > "$report"
{
  "results": [
    {
      "packages": [
        {
          "package": { "name": "pkg-a", "version": "1.0" },
          "vulnerabilities": [
            { "id": "GHSA-1111-2222-3333" },
            { "id": "GHSA-aaaa-bbbb-cccc" }
          ]
        },
        {
          "package": { "name": "pkg-b", "version": "2.0" },
          "vulnerabilities": [
            { "id": "GHSA-1111-2222-3333" }
          ]
        }
      ]
    }
  ]
}
EOF

  run "$SCRIPT_PATH" "$report"
  [ "$status" -eq 0 ]
  run grep "has_vulnerabilities=true" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
  run grep "vuln_ids=GHSA-1111-2222-3333, GHSA-aaaa-bbbb-cccc" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

@test "exports target_branch from environment" {
  export TARGET_BRANCH="feature/my-branch"
  run "$SCRIPT_PATH" "${TEST_TEMP_DIR}/nonexistent.json"
  [ "$status" -eq 0 ]
  run grep "target_branch=feature/my-branch" "$GITHUB_OUTPUT"
  [ "$status" -eq 0 ]
}

#!/usr/bin/env bash
set -euo pipefail

REPORT="${1:-${OSV_REPORT_PATH:-build/osv-scanner/osv-scanner-scan.json}}"
TARGET_BRANCH="${TARGET_BRANCH:-${GITHUB_HEAD_REF:-${GITHUB_REF_NAME:-develop}}}"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "target_branch=${TARGET_BRANCH}" >> "$GITHUB_OUTPUT"
fi

VULN_IDS=""
if [[ -f "$REPORT" ]]; then
  VULN_IDS=$(jq -r '[.results[].packages[].vulnerabilities[].id] | unique | join(", ")' "$REPORT" 2>/dev/null || true)
fi

if [[ -n "$VULN_IDS" ]]; then
  echo "Found vulnerabilities: ${VULN_IDS}"
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "has_vulnerabilities=true" >> "$GITHUB_OUTPUT"
    echo "vuln_ids=${VULN_IDS}" >> "$GITHUB_OUTPUT"
  fi
else
  echo "No unignored vulnerabilities found in report."
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "has_vulnerabilities=false" >> "$GITHUB_OUTPUT"
    echo "vuln_ids=" >> "$GITHUB_OUTPUT"
  fi
fi

#!/usr/bin/env bash
set -euo pipefail

TARGET_BRANCH="${1:-${TARGET_BRANCH:-develop}}"
VULN_IDS="${2:-${VULN_IDS:-}}"
FORCE="${FORCE:-false}"
EVENT_NAME="${GITHUB_EVENT_NAME:-workflow_dispatch}"

write_output() {
  local key="$1"
  local val="$2"
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "${key}=${val}" >> "$GITHUB_OUTPUT"
  fi
}

if [[ "${FORCE}" == "true" ]]; then
  echo "Force mode enabled. Bypassing existing PR check."
  write_output "skip_remediation" "false"
  write_output "existing_pr_number" ""
  exit 0
fi

if [[ "${EVENT_NAME}" == "pull_request" ]]; then
  echo "Running in PR context. Standalone remediation PR check not applicable."
  write_output "skip_remediation" "false"
  write_output "existing_pr_number" ""
  exit 0
fi

if ! command -v gh >/dev/null 2>&1; then
  echo "GitHub CLI (gh) not available. Proceeding without PR check."
  write_output "skip_remediation" "false"
  write_output "existing_pr_number" ""
  exit 0
fi

REMEDIATE_BRANCH="fix/remediate-osv-${TARGET_BRANCH}"
echo "Checking for open PR from ${REMEDIATE_BRANCH} to ${TARGET_BRANCH}..."

PR_JSON=$(gh pr list --state open --head "${REMEDIATE_BRANCH}" --base "${TARGET_BRANCH}" --json number,title,body --jq '.[0]' 2>/dev/null || true)

if [[ -z "${PR_JSON}" || "${PR_JSON}" == "null" ]]; then
  echo "No existing open remediation PR found."
  write_output "skip_remediation" "false"
  write_output "existing_pr_number" ""
  exit 0
fi

PR_NUMBER=$(echo "${PR_JSON}" | jq -r '.number // empty')
PR_CONTENT=$(echo "${PR_JSON}" | jq -r '(.title // "") + " " + (.body // "")')

ALL_COVERED=true
if [[ -n "${VULN_IDS}" ]]; then
  IFS=',' read -ra IDS <<< "${VULN_IDS}"
  for id in "${IDS[@]}"; do
    trimmed_id="$(echo "$id" | xargs)"
    if [[ -n "$trimmed_id" ]] && [[ "$PR_CONTENT" != *"$trimmed_id"* ]]; then
      ALL_COVERED=false
      break
    fi
  done
fi

if [[ "${ALL_COVERED}" == "true" && -n "${PR_NUMBER}" ]]; then
  echo "::notice::Open remediation PR #${PR_NUMBER} already covers ${VULN_IDS}. Skipping remediation to conserve API tokens."
  write_output "skip_remediation" "true"
  write_output "existing_pr_number" "${PR_NUMBER}"
else
  echo "::notice::Open remediation PR #${PR_NUMBER} exists, but new/unaddressed vulnerabilities were detected. Proceeding with remediation to update PR."
  write_output "skip_remediation" "false"
  write_output "existing_pr_number" "${PR_NUMBER:-}"
fi

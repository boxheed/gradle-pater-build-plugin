#!/usr/bin/env bash
set -euo pipefail

TARGET_BRANCH="${TARGET_BRANCH:-develop}"
VULN_IDS="${VULN_IDS:-OSV vulnerabilities}"
EVENT_NAME="${GITHUB_EVENT_NAME:-workflow_dispatch}"

if [[ -z "$(git status --porcelain)" ]]; then
  echo "::warning::Remediation agent finished without modifying any tracked files."
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "remediated=false" >> "$GITHUB_OUTPUT"
  fi
  exit 1
fi

if ! git config user.name >/dev/null 2>&1; then
  git config user.name "${GIT_AUTHOR_NAME:-${GITHUB_ACTOR:-github-actions[bot]}}"
  git config user.email "${GIT_AUTHOR_EMAIL:-${GITHUB_ACTOR:-github-actions[bot]}@users.noreply.github.com}"
fi

COMMIT_MSG="fix(deps): remediate ${VULN_IDS}"

if [[ "${EVENT_NAME}" == "pull_request" ]]; then
  echo "Pushing remediation commit directly to PR branch ${TARGET_BRANCH}..."
  git add -A
  git commit -m "${COMMIT_MSG}"
  git push origin "HEAD:${TARGET_BRANCH}"
else
  REMEDIATE_BRANCH="fix/remediate-osv-${TARGET_BRANCH}"
  echo "Pushing remediation to branch ${REMEDIATE_BRANCH}..."
  git checkout -B "${REMEDIATE_BRANCH}"
  git add -A
  git commit -m "${COMMIT_MSG}"
  git push -f -u origin "${REMEDIATE_BRANCH}"

  EXISTING_PR=$(gh pr list --state open --head "${REMEDIATE_BRANCH}" --base "${TARGET_BRANCH}" --json number --jq '.[0].number' 2>/dev/null || true)
  if [[ -n "${EXISTING_PR}" ]]; then
    echo "Updating existing open pull request #${EXISTING_PR}..."
    gh pr edit "${EXISTING_PR}" \
      --title "${COMMIT_MSG}" \
      --body "Automated remediation updated for: ${VULN_IDS}"
  else
    CLOSED_PR=$(gh pr list --state closed --head "${REMEDIATE_BRANCH}" --base "${TARGET_BRANCH}" --json number --jq '.[0].number' 2>/dev/null || true)
    if [[ -n "${CLOSED_PR}" ]]; then
      echo "Reopening closed pull request #${CLOSED_PR}..."
      if gh pr reopen "${CLOSED_PR}" 2>/dev/null; then
        gh pr edit "${CLOSED_PR}" \
          --title "${COMMIT_MSG}" \
          --body "Automated remediation updated for: ${VULN_IDS}"
      else
        echo "Could not reopen PR #${CLOSED_PR}. Creating a fresh pull request..."
        gh pr create \
          --base "${TARGET_BRANCH}" \
          --head "${REMEDIATE_BRANCH}" \
          --title "${COMMIT_MSG}" \
          --body "Automated remediation generated for: ${VULN_IDS}"
      fi
    else
      echo "Creating new pull request..."
      gh pr create \
        --base "${TARGET_BRANCH}" \
        --head "${REMEDIATE_BRANCH}" \
        --title "${COMMIT_MSG}" \
        --body "Automated remediation generated for: ${VULN_IDS}"
    fi
  fi
fi

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "remediated=true" >> "$GITHUB_OUTPUT"
fi
echo "Remediation successfully published to ${TARGET_BRANCH}."

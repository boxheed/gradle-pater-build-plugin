#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${AIDER_MODEL:-}" ]]; then
  if [[ -n "${OPENROUTER_API_KEY:-}" && -z "${GEMINI_API_KEY:-}" ]]; then
    MODEL="openrouter/google/gemini-2.5-flash-lite"
  else
    MODEL="gemini/gemini-3.5-flash-lite"
  fi
else
  MODEL="${AIDER_MODEL}"
fi
REPORT="${1:-${OSV_REPORT_PATH:-build/osv-scanner/osv-scanner-scan.json}}"

# Ensure uv and aider are available
if ! command -v aider >/dev/null 2>&1; then
  echo "Aider not found in PATH. Checking uv..."
  if ! command -v uv >/dev/null 2>&1; then
    echo "Installing uv..."
    curl -LsSf https://astral.sh/uv/install.sh | sh
    export PATH="${HOME}/.local/bin:${PATH}"
  fi
  echo "Installing Aider via uv..."
  uv tool install --python 3.12 aider-chat
  export PATH="${HOME}/.local/bin:${PATH}"
fi

# Ensure git author is configured if not already present
if ! git config user.name >/dev/null 2>&1; then
  git config user.name "${GIT_AUTHOR_NAME:-${GITHUB_ACTOR:-github-actions[bot]}}"
  git config user.email "${GIT_AUTHOR_EMAIL:-${GITHUB_ACTOR:-github-actions[bot]}@users.noreply.github.com}"
fi

# Dynamically discover Gradle build files and osv-scanner.toml
FILES_TO_EDIT=()
while IFS= read -r f; do
  clean_path="${f#./}"
  FILES_TO_EDIT+=(--file "${clean_path}")
done < <(find . -maxdepth 3 \( -name "build.gradle" -o -name "build.gradle.kts" -o -name "osv-scanner.toml" \) ! -path "*/.*" | sort)

echo "Running Aider remediation with model: ${MODEL}..."

AIDER_PROMPT=$(cat << EOF
Remediate the OSV vulnerabilities reported in ${REPORT} strictly adhering to the instructions in .agents/skills/osv-remediation/SKILL.md and AGENTS.md.
1. Primary: Update vulnerable dependencies in build.gradle (in-place bump for direct dependencies, or constraints block for transitive dependencies).
2. Fallback: ONLY if no fixed version exists in the advisory, or if bumping causes breaking changes that cannot be resolved, add an entry to osv-scanner.toml following the existing [[IgnoredVulns]] structure.
EOF
)

aider \
  --model "${MODEL}" \
  --read .agents/skills/osv-remediation/SKILL.md \
  --read AGENTS.md \
  --read "${REPORT}" \
  "${FILES_TO_EDIT[@]}" \
  --chat-history-file /tmp/aider.chat.history.md \
  --input-history-file /tmp/aider.input.history \
  --test-cmd "./gradlew osvInstall osvLockAndScan && ./gradlew spotlessApply && ./gradlew build" \
  --auto-test \
  --no-auto-commits \
  --no-dirty-commits \
  --no-gitignore \
  --yes-always \
  --message "${AIDER_PROMPT}" || true

if [[ -n "$(git status --porcelain)" ]]; then
  echo "Remediation finished with changes in working tree."
else
  echo "Remediation finished without making changes."
fi

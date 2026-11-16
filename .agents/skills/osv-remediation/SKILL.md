---
name: osv-remediation
description: >-
  Use this skill to investigate and remediate security vulnerabilities detected by OSV Scanner in this Gradle project.
---

# OSV Vulnerability Remediation

This skill guides the automated remediation of security vulnerabilities reported by OSV Scanner in this repository.

## Workflow

### 1. Identify Vulnerabilities
Read the OSV Scanner report at `build/osv-scanner/osv-scanner-scan.json` (or generate it by running `./gradlew osvInstall osvLockAndScan`).

For each vulnerability entry:
- Note the **Vulnerability ID** (e.g. `GHSA-xxx` or `CVE-xxx`).
- Note the **Package Name** and **Current Version** from `packages[].package`.
- Inspect `vulnerabilities[].affected[].ranges[].events` for the minimum `fixed` version that resolves the advisory.

### 2. Apply Remediation

Follow a two-tier remediation strategy:

#### Primary Strategy: Update Dependencies in `build.gradle`
Always attempt to fix vulnerabilities by updating dependencies first:
- **Direct Dependency**: If the vulnerable package is explicitly declared in `dependencies { ... }` or `buildscript { dependencies { ... } }`, update its version number directly in that declaration.
- **Transitive Dependency**: If the vulnerable package is NOT directly declared in `build.gradle` (it is brought in transitively by another dependency or plugin), enforce the fixed version inside the project-level `dependencies { constraints { ... } }` block at the bottom of `build.gradle`.
- **Never touch `buildscript` for constraints**: The `buildscript { dependencies { ... } }` block at the top only accepts `classpath` entries. NEVER insert `implementation(...)` or `constraints { ... }` inside `buildscript`.

##### Transitive Dependency Constraint Format
Add the constraint to the existing `dependencies { constraints { ... } }` block at the bottom of `build.gradle`:

```groovy
dependencies {
    // ... direct dependencies ...

    constraints {
        // ... existing constraints ...

        implementation('<group>:<artifact>') {
            because '<reason / CVE / GHSA-id>'
            version {
                require '<fixed-version>'
            }
        }
    }
}
```

#### Secondary / Fallback Strategy: Temporary Ignore in `osv-scanner.toml`
ONLY use this fallback if:
- The advisory has no upstream fixed version available yet, or
- Upgrading causes breaking changes that cannot be resolved in the current PR.

Add an ignore block to `osv-scanner.toml` following the exact schema:

```toml
[[IgnoredVulns]]
id = "GHSA-xxxx-yyyy-zzzz"
effectiveUntil = 2026-12-01
reason = "Explanation of why this vulnerability is ignored and cannot be fixed yet."
```
Always specify `id`, a descriptive `reason`, and a valid future `effectiveUntil` date (format `YYYY-MM-DD`). Never invent custom TOML keys.

### 3. Handle Breaking Changes
If bumping a dependency introduces compilation or API incompatibilities in the project, update the affected source or test files to restore compatibility while keeping the patched dependency version.

### 4. Verification & Formatting
Run the verification commands in sequence to ensure the fix is clean, properly formatted, and passes all tests and plugin checks:

```bash
# 1. Update OSV lockfile and verify no vulnerabilities remain
./gradlew osvInstall osvLockAndScan

# 2. Apply code style formatting
./gradlew spotlessApply

# 3. Execute build and verification tasks
./gradlew build
```

### 5. Execution Boundaries
- **CI Mode:** When invoked in CI, leave all modified files in the working directory. Do NOT execute `git commit`, `git checkout -b`, `git push`, or `gh pr create`; the CI runner handles commits, branches, and PR creation.
- **Local Developer Mode:** When invoked locally by a developer, summarize the changes and patched versions cleanly.

# Instructions for AI Coding Agents (Jules)

Welcome to this project. This document outlines the project conventions, branching strategy, and verification steps that all autonomous agents (such as Google Jules) must follow.

---

## 1. Branching Strategy & Target Branch

> [!IMPORTANT]
> **All Pull Requests MUST target the branch specified in the task / issue description (defaulting to `develop` if unspecified).**
> Never open Pull Requests against `master`.

- **Target / Base Branch:** Follow the branch specified in the issue/task (e.g. `- **Target Branch:** <branch>`). Defaults to `develop`.
- **Branch-Specific Remediations:** When remediating a vulnerability on a branch (such as a Dependabot PR branch `dependabot/...` or feature branch), checkout that branch, apply the fix, and submit the Pull Request targeting that specific branch.
- **Master Branch:** Reserved exclusively for releases and automated versioning. Never target `master`.

---

## 2. Dependency & Vulnerability Remediation

When remediating vulnerabilities detected by OSV Scanner or security issues, apply a two-tier strategy:

1. **Primary Strategy (Target: `build.gradle`):**
   - **Direct Dependencies:** If the affected package is directly declared in `dependencies { ... }` or `buildscript { dependencies { ... } }`, upgrade its version number in place.
   - **Transitive Dependencies:** Enforce fixed versions using Gradle dependency constraints within the project-level `dependencies { constraints { ... } }` block at the bottom of [`build.gradle`](build.gradle):

     ```groovy
     dependencies {
         constraints {
             implementation('<group>:<artifact>') {
                 because '<reason / CVE / GHSA-id>'
                 version {
                     require '<fixed-version>'
                 }
             }
         }
     }
     ```
   - **Never** add `implementation` or `constraints` inside `buildscript { ... }` (which only supports `classpath`).
2. **Fallback Strategy (Target: `osv-scanner.toml`):**
   - ONLY if no upstream fixed version exists in the advisory, or if bumping introduces breaking changes that cannot be resolved:
   - Add an entry to `osv-scanner.toml` under `[[IgnoredVulns]]` with `id`, `effectiveUntil` (YYYY-MM-DD), and `reason`.
3. **Commit Formatting:**
   - Follow Conventional Commits: `fix(deps): remediate <VULNERABILITY_ID>`

---

## 3. Verification & Build Commands

Before submitting a Pull Request, verify the changes locally:

```bash
# 1. Update/check OSV vulnerability lock and scanner
./gradlew osvInstall osvLockAndScan

# 2. Apply code style formatting
./gradlew spotlessApply

# 3. Run full build and verification tasks
./gradlew build
```

All verification commands require Java 17.

# CLAUDE.md

Guidance for Claude Code (and other coding agents) working in this repository.

## Repo Overview

**blaisemath** is a collection of Java visualization/mathematics libraries,
published as **seven independently versioned** Maven modules under
`com.googlecode.blaisemath` on Maven Central. There is **no root aggregator
`pom.xml`** — each module's `pom.xml` is self-contained and depends on
**fixed released versions** of any sibling modules it needs, resolved from
Maven Central/local repo, not a reactor build.

Dependency graph (arrows = "depends on a pinned released version of"):

```
blaise-common
  ├─ blaise-json
  ├─ blaise-graphics
  │    └─ blaise-graphics-ui  (also pins external blaise-app)
  │         ├─ blaise-svg           (also pins blaise-graphics)
  │         └─ blaise-graph-theory-ui  (also pins blaise-graph-theory)
  └─ blaise-graph-theory
       └─ blaise-graph-theory-ui
```

`blaise-app` is an **external** dependency — a separate GitHub repo
(`triathematician/blaise-app`), not part of this working directory. It is
pinned the same way sibling blaisemath modules are.

A safe build/release order that respects this graph (also the order used in
the repo scripts and in recent release history):

1. `blaise-common`
2. `blaise-graphics`, `blaise-graph-theory`, `blaise-json` (any order — each
   only needs `blaise-common`)
3. `blaise-graphics-ui` (needs `blaise-graphics`)
4. `blaise-svg` (needs `blaise-graphics`, `blaise-graphics-ui`), and
   `blaise-graph-theory-ui` (needs `blaise-graph-theory`, `blaise-graphics-ui`)
   — either order, both just need step 3 done first.

## Build & Test

Root-level scripts drive all modules in the dependency order above:

```bash
./compile-all.sh    # mvn compile in each module
./install-all.sh    # mvn clean install in each module (needed before
                     # building a downstream module against a SNAPSHOT you
                     # just changed locally)
./test-all.sh       # mvn test in each module
```

(`.ps1` equivalents exist for Windows/PowerShell.) To work on a single
module, `cd` into it and run `mvn test`/`mvn install` directly — but if you
change a module that others pin, `mvn install` it locally first so
downstream `mvn test` runs pick up the change.

CI (`.github/workflows/run-tests.yml`) runs the same modules in the same
dependency order on every push/PR — `install` for any module something else
in the sequence depends on, `test` for leaf modules.

Requires JDK 17 and Maven >= 3.6.3 (enforced by `maven-enforcer-plugin` in
every module's POM).

## Release Process

Each module is released independently via `maven-release-plugin` +
`central-publishing-maven-plugin` (Sonatype Central), e.g.:

```bash
cd blaise-common   # or any other module
mvn release:prepare
mvn release:perform
```

This pushes commits/tags and publishes irreversibly to Maven Central — an
agent should never run these commands without explicit user confirmation
immediately beforehand, and by default should only *prepare* a release
checklist and let the user run the commands.

If module A pins a fixed version of module B and B's version changes,
updating A's pinned `<version>` for B is a **separate, follow-up POM edit**
— not implied automatically by releasing B. See step 13 of the playbook
below for when this must happen before releasing A.

The README links each module to a GitHub wiki change-log page (e.g.
`blaise-common-change-log`). Updating these wiki pages is a manual step for
the user — the agent should call it out in the release checklist but not
attempt to edit the wiki itself.

## Dependency Maintenance Playbook

Use this generic, repeatable process for any dependency or tooling version
bump (Guava, Jackson, JUnit, plugin versions, etc.) — not a one-off recipe
for any single library.

1. **Open a tracking issue** — `gh issue create` in
   `triathematician/blaisemath`, e.g. title "Dependency maintenance:
   <date/scope>", with a checklist body that gets filled in as the process
   progresses.
2. **Survey dependencies** — run in every module (the `dependency-updates.sh`
   / `.ps1` script does this for all seven at once):
   ```bash
   mvn versions:display-dependency-updates
   mvn versions:display-plugin-updates
   ```
   Also check `gh` for open Dependabot PRs/alerts on the repo. Compile one
   combined list of available updates across all modules.
3. **Classify updates** — patch/minor (batchable, low risk) vs. major
   (review changelogs/breaking changes individually). Record the
   classification in the tracking issue.
4. **Decide scope for this cycle** — the agent proposes which updates to
   tackle now (based on the classification in step 3) and which to defer,
   but the **user approves the final list** before any code changes are
   made. Edit the issue with the exact target versions agreed on;
   explicitly defer the rest to a future issue.
5. **Branch and update** — one combined branch/PR is fine when the same
   dependency (e.g. Guava, JUnit) is bumped identically across modules,
   since these are declared per-module rather than inherited from a parent.
   Touch each affected module's `pom.xml` (and README where a version is
   user-visible).
6. **Run automated tests** in dependency order:
   ```bash
   ./test-all.sh   # or ./install-all.sh if a module's own artifact
                    # version needs to be visible to a downstream module
   ```
7. **Manual tests, conditionally** — only for major-version or otherwise
   behavior-relevant bumps. This repo has no bundled examples/integration
   harness to smoke-test against; if a demo is warranted, note that it would
   require checking out a downstream consumer repo (`blaise-app`,
   `firestarter`, `blaise-sketch`) separately. State the reasoning for
   running or skipping this step.
8. **Fix and iterate** until all triggered tests are green.
9. **Push the branch and open a PR** via `gh pr create`, referencing the
   tracking issue, summarizing the change and test evidence gathered.
10. **Wait for CI, then the user reviews and merges.** The agent does not
    merge dependency-update PRs itself — a human must review and click
    merge, even if tests are green. Address any review feedback by pushing
    fixes to the same branch.
11. **On merge** — close the tracking issue (`gh issue close`), pull `main`
    locally.
12. **Release — prepared by the agent, executed by the user.** Prepare a
    release checklist/notes (which module(s), in what order, per the
    dependency graph above) but do **not** run `mvn
    release:prepare`/`mvn release:perform` — that's a manual step the user
    runs, since it publishes irreversibly to Maven Central. Only modules
    whose own artifact version is actually changing need to be released;
    release order among independent modules is otherwise unconstrained, but
    a module must never be released before any sibling version it newly
    pins to (see step 13).
13. **If an upstream module was released in this cycle, sync every pin
    that depends on it before releasing further.** Immediately after a
    module's `mvn release:perform` completes, find every other module in
    the repo that pins its old version (e.g. releasing `blaise-graphics`
    means checking `blaise-graphics-ui` and `blaise-svg`) and update the
    pinned `<version>` in each, in its own small commit pushed straight to
    `main` (verify with `mvn test` in that downstream module first — it
    should resolve the new artifact from Central). Do this **before**
    running `release:prepare` on any downstream module, so nothing is ever
    released pinned to a stale sibling version. Walk the dependency graph
    top-down (per the ordering in "Repo Overview") so each module is synced
    before its own release.
14. **Publish on Sonatype Central.** After `mvn release:perform` completes
    for a module, its artifacts land in a Central Publishing Portal
    deployment that still needs a manual publish action. Go to
    **https://central.sonatype.com/publishing** and hit the **Publish**
    button for that deployment.
15. **Update the wiki change-log** for each released module (linked from
    README.md) — a manual step for the user; the agent should list which
    change-log pages need an entry in the release checklist.

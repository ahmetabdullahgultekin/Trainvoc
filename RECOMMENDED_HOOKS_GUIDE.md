# Recommended Hooks Guide for Trainvoc
## Project-Specific Hook Configuration

**Date:** January 21, 2026
**Project:** Trainvoc Android App
**Purpose:** Automate quality checks and development workflows

---

## 📋 Overview

This guide recommends **8 essential hooks** tailored for the Trainvoc project based on:
- Current codebase structure
- Identified quality issues
- Development workflow needs
- CI/CD integration requirements

---

## 🎯 Recommended Hooks

### Priority Ranking

| Hook | Type | Priority | Purpose |
|------|------|----------|---------|
| **Pre-commit** | Git | 🔴 CRITICAL | Code quality, lint, format |
| **Pre-push** | Git | 🔴 CRITICAL | Tests, build validation |
| **SessionStart** | Claude Code | 🟠 HIGH | Dev environment setup |
| **Commit-msg** | Git | 🟡 MEDIUM | Conventional commits |
| **Post-merge** | Git | 🟡 MEDIUM | Dependency sync |
| **Pre-build** | Gradle | 🟡 MEDIUM | Resource validation |
| **Post-checkout** | Git | 🔵 LOW | Branch-specific setup |
| **Pre-release** | CI/CD | 🟠 HIGH | Release validation |

---

## 🔴 CRITICAL HOOKS

### 1. Pre-Commit Hook (Git)
**Purpose:** Prevent committing bad code

**What to Check:**
1. ✅ Kotlin lint (ktlint/detekt)
2. ✅ Code formatting
3. ✅ No debug statements (Log.d, println)
4. ✅ No hardcoded secrets
5. ✅ No TODOs in critical files
6. ✅ Accessibility contentDescription present
7. ✅ Theme colors (no hardcoded colors)

**Implementation:**

```bash
#!/bin/bash
# .git/hooks/pre-commit

echo "🔍 Running pre-commit checks..."

# 1. Run ktlint
echo "📝 Checking Kotlin code style..."
./gradlew ktlintCheck
if [ $? -ne 0 ]; then
    echo "❌ Kotlin lint failed. Run './gradlew ktlintFormat' to fix."
    exit 1
fi

# 2. Check for debug statements
echo "🐛 Checking for debug statements..."
if git diff --cached --name-only | grep -E '\.kt$' | xargs grep -n 'System.out.println\|Log.d(' 2>/dev/null; then
    echo "❌ Debug statements found. Please remove before committing."
    exit 1
fi

# 3. Check for hardcoded secrets
echo "🔒 Checking for potential secrets..."
if git diff --cached | grep -iE '(password|api_key|secret|token)\s*=\s*"[^"]+"'; then
    echo "⚠️  WARNING: Possible hardcoded secret detected!"
    echo "Review carefully before committing."
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 4. Check for contentDescription in new Icon/Image composables
echo "♿ Checking accessibility (contentDescription)..."
if git diff --cached | grep -A 3 "Icon(\|Image(" | grep "contentDescription = null"; then
    echo "⚠️  WARNING: Icon/Image with null contentDescription found!"
    echo "Please add proper accessibility descriptions."
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 5. Check for hardcoded colors (Color(0x...))
echo "🎨 Checking for hardcoded colors..."
HARDCODED_COLORS=$(git diff --cached --diff-filter=AM | grep -E "Color\(0x[0-9A-F]{8}\)" | wc -l)
if [ "$HARDCODED_COLORS" -gt 0 ]; then
    echo "⚠️  WARNING: $HARDCODED_COLORS new hardcoded colors found!"
    echo "Consider using MaterialTheme.colorScheme instead."
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo "✅ All pre-commit checks passed!"
exit 0
```

**Enable:**
```bash
chmod +x .git/hooks/pre-commit
```

---

### 2. Pre-Push Hook (Git)
**Purpose:** Prevent pushing broken code

**What to Check:**
1. ✅ Unit tests pass
2. ✅ Project builds successfully
3. ✅ No merge conflicts
4. ✅ Branch naming convention
5. ✅ Commit messages valid

**Implementation:**

```bash
#!/bin/bash
# .git/hooks/pre-push

echo "🚀 Running pre-push checks..."

BRANCH=$(git rev-parse --abbrev-ref HEAD)

# 1. Check branch naming convention
echo "📋 Checking branch name..."
if [[ ! "$BRANCH" =~ ^(main|master|develop|feature/|bugfix/|hotfix/|release/|claude/) ]]; then
    echo "❌ Invalid branch name: $BRANCH"
    echo "Use: feature/, bugfix/, hotfix/, release/, or claude/"
    exit 1
fi

# 2. Prevent pushing to main/master without review
if [[ "$BRANCH" == "main" || "$BRANCH" == "master" ]]; then
    echo "⚠️  WARNING: Pushing directly to $BRANCH!"
    read -p "Are you sure? This should go through PR. (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 3. Run unit tests
echo "🧪 Running unit tests..."
./gradlew test --daemon
if [ $? -ne 0 ]; then
    echo "❌ Unit tests failed!"
    exit 1
fi

# 4. Verify project builds
echo "🔨 Verifying build..."
./gradlew assembleDebug --daemon
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

# 5. Check for merge conflicts
echo "🔍 Checking for merge conflict markers..."
if git diff --check HEAD; then
    :
else
    echo "❌ Merge conflict markers found!"
    exit 1
fi

echo "✅ All pre-push checks passed!"
exit 0
```

---

### 3. SessionStart Hook (Claude Code)
**Purpose:** Set up development environment when Claude starts

**What to Do:**
1. ✅ Check Gradle daemon status
2. ✅ Verify dependencies are up to date
3. ✅ Run code quality checks
4. ✅ Show project health summary
5. ✅ Check for pending TODOs in critical files

**Implementation:**

Create file: `.claude/hooks/session-start.sh`

```bash
#!/bin/bash
# .claude/hooks/session-start.sh
# Runs when Claude Code web session starts

echo "🤖 Claude Code - Trainvoc Session Starting..."
echo "================================================"

# 1. Project Health Check
echo "📊 Project Health Check..."

# Check if Gradle wrapper is executable
if [ ! -x ./gradlew ]; then
    echo "⚠️  WARNING: gradlew is not executable"
    chmod +x ./gradlew
    echo "✅ Fixed gradlew permissions"
fi

# 2. Start Gradle daemon if not running
echo "🔧 Checking Gradle daemon..."
if ! ./gradlew --status | grep -q "IDLE"; then
    echo "Starting Gradle daemon..."
    ./gradlew --daemon &
fi

# 3. Check for outdated dependencies
echo "📦 Checking dependencies..."
# Just verify, don't auto-update
./gradlew dependencyUpdates --quiet || true

# 4. Quick lint check
echo "🔍 Running quick lint check..."
LINT_ERRORS=$(./gradlew lintDebug --quiet 2>&1 | grep -c "Error" || true)
if [ "$LINT_ERRORS" -gt 0 ]; then
    echo "⚠️  $LINT_ERRORS lint errors found"
else
    echo "✅ No lint errors"
fi

# 5. Show critical TODOs
echo ""
echo "🚨 Critical TODOs in codebase:"
grep -r "TODO.*CRITICAL\|FIXME" app/src/main/java --include="*.kt" | head -5 || echo "None found"

# 6. Git status
echo ""
echo "📝 Git Status:"
git status -s | head -10

# 7. Show uncommitted changes count
UNCOMMITTED=$(git status -s | wc -l)
if [ "$UNCOMMITTED" -gt 0 ]; then
    echo "⚠️  $UNCOMMITTED uncommitted changes"
fi

# 8. Show current branch
BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "🌿 Current branch: $BRANCH"

# 9. Check if tests are passing
echo ""
echo "🧪 Running quick test check..."
./gradlew test --dry-run --quiet || echo "⚠️  Tests may need attention"

# 10. Project stats
echo ""
echo "📈 Project Statistics:"
echo "  - Kotlin files: $(find app/src/main/java -name "*.kt" | wc -l)"
echo "  - Total lines: $(find app/src/main/java -name "*.kt" -exec wc -l {} + | tail -1 | awk '{print $1}')"
echo "  - TODOs: $(grep -r "TODO" app/src/main/java --include="*.kt" | wc -l)"

# 11. Show recent commits
echo ""
echo "📜 Recent commits:"
git log --oneline -5

echo ""
echo "✅ Session setup complete!"
echo "================================================"
exit 0
```

**Enable in `.claude/claude.json`:**
```json
{
  "hooks": {
    "session-start": ".claude/hooks/session-start.sh"
  }
}
```

---

## 🟡 MEDIUM PRIORITY HOOKS

### 4. Commit-Msg Hook (Git)
**Purpose:** Enforce conventional commit messages

**Format:** `type(scope): description`

**Valid Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting (not code style)
- `refactor`: Code restructuring
- `test`: Tests
- `chore`: Maintenance

**Implementation:**

```bash
#!/bin/bash
# .git/hooks/commit-msg

COMMIT_MSG=$(cat "$1")

# Check conventional commit format
if ! echo "$COMMIT_MSG" | grep -qE "^(feat|fix|docs|style|refactor|test|chore)(\(.+\))?: .+"; then
    echo "❌ Invalid commit message format!"
    echo ""
    echo "Format: type(scope): description"
    echo ""
    echo "Valid types:"
    echo "  - feat:     New feature"
    echo "  - fix:      Bug fix"
    echo "  - docs:     Documentation"
    echo "  - style:    Formatting"
    echo "  - refactor: Code restructuring"
    echo "  - test:     Tests"
    echo "  - chore:    Maintenance"
    echo ""
    echo "Example: feat(quiz): add multiple choice questions"
    exit 1
fi

exit 0
```

---

### 5. Post-Merge Hook (Git)
**Purpose:** Sync dependencies and clean after merge

**What to Do:**
1. ✅ Clean build cache
2. ✅ Sync Gradle dependencies
3. ✅ Update git submodules (if any)
4. ✅ Notify about conflicts

**Implementation:**

```bash
#!/bin/bash
# .git/hooks/post-merge

echo "🔄 Post-merge tasks..."

# Check if Gradle files changed
if git diff-tree -r --name-only --no-commit-id ORIG_HEAD HEAD | grep -E "(build.gradle|settings.gradle|gradle.properties)"; then
    echo "📦 Gradle files changed, syncing dependencies..."
    ./gradlew --refresh-dependencies
    echo "✅ Dependencies synced"
fi

# Clean build cache if needed
if git diff-tree -r --name-only --no-commit-id ORIG_HEAD HEAD | grep -q "app/src/"; then
    echo "🧹 Cleaning build cache..."
    ./gradlew clean
    echo "✅ Build cache cleaned"
fi

echo "✅ Post-merge tasks complete"
exit 0
```

---

### 6. Pre-Build Hook (Gradle)
**Purpose:** Validate resources before build

**Implementation:**

Add to `app/build.gradle.kts`:

```kotlin
tasks.register("validateResources") {
    doLast {
        // Check for missing string resources
        val stringsFile = file("src/main/res/values/strings.xml")
        if (!stringsFile.exists()) {
            throw GradleException("strings.xml not found!")
        }

        // Check for unused resources
        println("Running unused resource check...")
        exec {
            commandLine("./gradlew", "lintDebug")
        }
    }
}

// Run before build
tasks.named("preBuild") {
    dependsOn("validateResources")
}
```

---

## 🔵 LOW PRIORITY HOOKS

### 7. Post-Checkout Hook (Git)
**Purpose:** Branch-specific setup

**Implementation:**

```bash
#!/bin/bash
# .git/hooks/post-checkout

BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo "📦 Switched to branch: $BRANCH"

# Branch-specific actions
case "$BRANCH" in
    develop|main|master)
        echo "🏠 On main branch - running full checks..."
        ./gradlew check --daemon &
        ;;
    feature/*)
        echo "✨ Feature branch - ready for development"
        ;;
    bugfix/*)
        echo "🐛 Bugfix branch - review related tests"
        ;;
esac

exit 0
```

---

### 8. Pre-Release Hook (CI/CD)
**Purpose:** Comprehensive validation before release

**What to Check:**
1. ✅ All tests pass (unit + integration)
2. ✅ Code coverage > 80%
3. ✅ No lint errors
4. ✅ No security vulnerabilities
5. ✅ Version number incremented
6. ✅ Changelog updated
7. ✅ APK size check
8. ✅ ProGuard rules valid

**Implementation:**

Create `.github/workflows/pre-release.yml`:

```yaml
name: Pre-Release Validation

on:
  push:
    branches:
      - release/*

jobs:
  validate:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Cache Gradle packages
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}

      - name: Run all tests
        run: ./gradlew test connectedAndroidTest

      - name: Run lint
        run: ./gradlew lint

      - name: Check code coverage
        run: ./gradlew koverHtmlReport

      - name: Build release APK
        run: ./gradlew assembleRelease

      - name: Check APK size
        run: |
          SIZE=$(stat -f%z app/build/outputs/apk/release/*.apk || stat -c%s app/build/outputs/apk/release/*.apk)
          MAX_SIZE=$((50 * 1024 * 1024))  # 50MB
          if [ $SIZE -gt $MAX_SIZE ]; then
            echo "❌ APK too large: $(($SIZE / 1024 / 1024))MB"
            exit 1
          fi
          echo "✅ APK size OK: $(($SIZE / 1024 / 1024))MB"

      - name: Verify version bump
        run: |
          git fetch origin main:main
          CURRENT=$(grep "versionCode" app/build.gradle.kts | grep -o '[0-9]\+')
          git checkout main
          MAIN=$(grep "versionCode" app/build.gradle.kts | grep -o '[0-9]\+')
          git checkout -
          if [ "$CURRENT" -le "$MAIN" ]; then
            echo "❌ Version code not incremented!"
            exit 1
          fi
          echo "✅ Version bumped: $MAIN -> $CURRENT"
```

---

## 🎛️ Hook Management Scripts

### Enable All Recommended Hooks

Create `scripts/setup-hooks.sh`:

```bash
#!/bin/bash
# scripts/setup-hooks.sh

echo "🔧 Setting up Git hooks for Trainvoc..."

HOOKS_DIR=".git/hooks"

# Pre-commit
cat > "$HOOKS_DIR/pre-commit" << 'EOF'
[Pre-commit hook content from above]
EOF
chmod +x "$HOOKS_DIR/pre-commit"
echo "✅ Pre-commit hook installed"

# Pre-push
cat > "$HOOKS_DIR/pre-push" << 'EOF'
[Pre-push hook content from above]
EOF
chmod +x "$HOOKS_DIR/pre-push"
echo "✅ Pre-push hook installed"

# Commit-msg
cat > "$HOOKS_DIR/commit-msg" << 'EOF'
[Commit-msg hook content from above]
EOF
chmod +x "$HOOKS_DIR/commit-msg"
echo "✅ Commit-msg hook installed"

# Post-merge
cat > "$HOOKS_DIR/post-merge" << 'EOF'
[Post-merge hook content from above]
EOF
chmod +x "$HOOKS_DIR/post-merge"
echo "✅ Post-merge hook installed"

# Post-checkout
cat > "$HOOKS_DIR/post-checkout" << 'EOF'
[Post-checkout hook content from above]
EOF
chmod +x "$HOOKS_DIR/post-checkout"
echo "✅ Post-checkout hook installed"

# Claude Code hooks
mkdir -p .claude/hooks
cat > ".claude/hooks/session-start.sh" << 'EOF'
[SessionStart hook content from above]
EOF
chmod +x ".claude/hooks/session-start.sh"
echo "✅ Claude Code session-start hook installed"

cat > ".claude/claude.json" << 'EOF'
{
  "hooks": {
    "session-start": ".claude/hooks/session-start.sh"
  }
}
EOF
echo "✅ Claude Code config created"

echo ""
echo "🎉 All hooks installed successfully!"
echo ""
echo "To disable a hook temporarily:"
echo "  chmod -x .git/hooks/<hook-name>"
echo ""
echo "To bypass hooks (use sparingly):"
echo "  git commit --no-verify"
echo "  git push --no-verify"
```

### Disable All Hooks

Create `scripts/disable-hooks.sh`:

```bash
#!/bin/bash
# scripts/disable-hooks.sh

HOOKS_DIR=".git/hooks"

for hook in pre-commit pre-push commit-msg post-merge post-checkout; do
    if [ -f "$HOOKS_DIR/$hook" ]; then
        chmod -x "$HOOKS_DIR/$hook"
        echo "🔇 Disabled $hook"
    fi
done

echo "✅ All Git hooks disabled"
```

---

## 📊 Hook Performance Benchmarks

Expected execution times on typical hardware:

| Hook | Time | Impact |
|------|------|--------|
| Pre-commit | 5-15s | Medium |
| Pre-push | 30-60s | High |
| SessionStart | 10-20s | Low (background) |
| Commit-msg | <1s | None |
| Post-merge | 10-30s | Low (async) |
| Pre-build | 5-10s | Medium |
| Post-checkout | <1s | None |
| Pre-release | 5-10min | N/A (CI) |

---

## 🎯 Quick Start

### 1. Install All Hooks
```bash
chmod +x scripts/setup-hooks.sh
./scripts/setup-hooks.sh
```

### 2. Test Hooks
```bash
# Test pre-commit
git add .
git commit -m "test: hook validation"

# Test pre-push
git push origin feature/test-hooks
```

### 3. Bypass Hooks (Emergency)
```bash
git commit --no-verify -m "emergency: critical hotfix"
git push --no-verify
```

---

## 🔧 Customization

### Project-Specific Rules

Edit hooks to match your team's needs:

1. **Adjust Thresholds:**
   - APK size limit (currently 50MB)
   - Code coverage target (currently 80%)
   - Max lint errors (currently 0)

2. **Add Custom Checks:**
   - Screenshot tests
   - Performance benchmarks
   - Accessibility audits

3. **Integration:**
   - Slack notifications
   - Jira ticket validation
   - API health checks

---

## 📚 Resources

### Git Hooks Documentation
- https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks

### Claude Code Hooks
- https://docs.anthropic.com/claude/docs/hooks

### Gradle Build Hooks
- https://docs.gradle.org/current/userguide/build_lifecycle.html

---

## 🎓 Best Practices

### DO ✅
- Keep hooks fast (<1 minute for pre-commit/pre-push)
- Provide clear error messages
- Allow bypass for emergencies (--no-verify)
- Run in background when possible
- Cache results to speed up repeated runs

### DON'T ❌
- Block developers with slow checks
- Auto-fix without confirmation
- Require perfect code (allow warnings)
- Run expensive operations on every commit
- Make hooks mandatory without team consensus

---

**Document Status:** ✅ COMPLETE
**Last Updated:** January 21, 2026
**Next Review:** After team feedback

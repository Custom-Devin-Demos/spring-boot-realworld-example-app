---
description: When Devin Creates a PR, go through the checklist with this workflow
---

# Coding Agent PR Review Workflow

**Parameter:** `PR_NUMBER` – the pull request number to review (e.g. `1` for `.../pull/1`)

---

## 1. Checkout the PR branch

Use the GitHub CLI or raw Git to fetch and switch to the PR branch.
** Don't use the browser, use CLI/API/MCP instead

```bash
# Option A: GitHub CLI
gh pr checkout ${PR_NUMBER}

# Option B: raw Git
git fetch origin pull/${PR_NUMBER}/head:pr-${PR_NUMBER}
git checkout pr-${PR_NUMBER}
```

## 2. Explore the PR, the diff, and IMPORTANT-Locate the Checklist

Find the "Review & Testing Checklist for Human" section in your codebase.

```
gh pr view ${PR_NUMBER} --json body --jq '.body'
```

```
gh pr diff ${PR_NUMBER}
```

^Take the time to analyze the PR before reporting back to user

## 3. List these items and also give a summary of what is in that main PR comment.

## 4. Propose options for next steps to arrive at a complete review based on the Review & Testing Checklist for Human in the PR Commit Description, like a choose your own adventure
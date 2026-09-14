# Git Privacy and Commit Identity Guide

## 1. Configure GitHub's private noreply email

Use the noreply email shown in your GitHub account settings:

```powershell
git config --global user.email "YOUR_GITHUB_ID+YOUR_USERNAME@users.noreply.github.com"
```

Set your commit name:

```powershell
git config --global user.name "Your GitHub Name"
```

Do not use your personal email address for public repositories unless you intentionally want it exposed.

## 2. Verify the global configuration

```powershell
git config --global --get user.name
git config --global --get user.email
git config --show-origin --get-regexp "^user\.(name|email)$"
```

The email should end in `@users.noreply.github.com`.

## 3. Check the identity for the current repository

A repository-specific setting can override the global setting. Run:

```powershell
git config user.name
git config user.email
git config --local --get user.email
```

If the local email is wrong, remove it so the global setting is used:

```powershell
git config --unset-local user.email
```

Or set the correct email only for this repository:

```powershell
git config user.email "YOUR_GITHUB_ID+YOUR_USERNAME@users.noreply.github.com"
```

## 4. Verify the identity before committing

```powershell
git var GIT_AUTHOR_IDENT
git var GIT_COMMITTER_IDENT
```

Confirm that the displayed email is the GitHub noreply address.

## 5. Check for personal information before publishing

Review tracked files:

```powershell
git status --short
git ls-files
```

Look for and remove or replace:

- API keys
- Passwords
- Access tokens
- Private keys
- Database connection strings
- Personal email addresses
- Personal usernames or absolute file paths
- Personal logs, screenshots, exports, and usage history

## 6. Ignore private and local files

Add local data to `.gitignore`, for example:

```gitignore
.env
*.local
Data/
```

After adding an ignore rule, confirm it works:

```powershell
git check-ignore -v path/to/file
```

`.gitignore` only prevents future tracking. It does not remove a file that is already tracked.

## 7. Check tracked files for common secrets

If Git Bash or ripgrep is available:

```bash
git grep -n -I -i -E "(api[_-]?key|secret|password|token|bearer|private[_-]?key|client[_-]?secret|access[_-]?key|mongodb\\+srv|https?://[^ ]+:[^ ]+@)" HEAD
```

Also inspect unusual files manually, especially `.env` files, JSON exports, logs, archives, and configuration files.

## 8. Remove sensitive files that are already tracked

```powershell
git rm --cached path/to/private-file
```

Then add the file or pattern to `.gitignore` and commit the change.

## 9. Remember that old commits do not change

Changing `user.email` only affects new commits. Existing commits keep their original author email permanently unless Git history is rewritten.

Inspect commit identities:

```powershell
git log --all --format="%h %an <%ae> %s"
```

Inspect the public commit API:

```text
https://api.github.com/repos/OWNER/REPOSITORY/commits
```

A commit can also expose author information through its GitHub commit page or its `.patch` URL.

## 10. Before making a repository public

Run:

```powershell
git status
git diff --check
git log --all --format="%an <%ae>" | Sort-Object -Unique
git remote -v
```

Confirm that:

- No secrets or personal data are tracked.
- Local runtime data is ignored.
- Future commits use the noreply email.
- You understand which email addresses are present in old history.
- The remote points to the intended GitHub repository.

## 11. If sensitive information was already pushed

Revoke or rotate exposed credentials immediately. Removing a file in a later commit does not remove it from Git history. Rewrite history only after making a backup and understanding that the rewritten history requires a force push and changes commit IDs.

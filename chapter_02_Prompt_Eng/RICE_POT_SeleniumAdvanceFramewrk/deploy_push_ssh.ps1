Param(
    [string]$Name = "Rashmi Rshetty",
    [string]$Email = "rashmirshetty09@gmail.com",
    [string]$Repo = "git@github.com:rashmirshetty09/AITESTER.git",
    [switch]$GenerateKey
)

$cwd = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
Set-Location $cwd

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Error "git is not installed or not on PATH. Install git and re-run."
    exit 1
}

if (-not (Test-Path ".git")) {
    git init
}

git config user.name "$Name"
git config user.email "$Email"

$keyPath = "$env:USERPROFILE\\.ssh\\id_ed25519"
if ($GenerateKey -or -not (Test-Path $keyPath)) {
    Write-Output "Generating SSH key at $keyPath (no passphrase)..."
    ssh-keygen -t ed25519 -f $keyPath -C $Email -N "" | Out-Null
    Write-Output "SSH key generated."
}

$pubPath = "$keyPath.pub"
if (Test-Path $pubPath) {
    $pub = Get-Content $pubPath -Raw
    Write-Output "\n=== Add this public key to GitHub (Settings → SSH and GPG keys) ===\n"
    Write-Output $pub
    Write-Output "\n===============================================================\n"
    Read-Host "Press Enter after you've added the SSH key in GitHub (or Ctrl+C to abort)"
} else {
    Write-Output "Public key not found at $pubPath. If you already have an SSH key, ensure your agent is loaded and the key is added to GitHub."
}

# set remote and push
try {
    git remote remove origin 2>$null
} catch {
}

git remote add origin $Repo

# Stage and commit
git add .
$commitResult = git commit -m "Add Selenium TestNG framework (RICE_POT_SeleniumAdvanceFramewrk)" 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Output "Commit may have failed or nothing to commit:\n$commitResult"
}

git branch -M main

Write-Output "Pushing to $Repo ..."

# Attempt push
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Output "Push successful."
} else {
    Write-Output "Push failed. Check SSH key on GitHub and your network, then re-run the push command."
}

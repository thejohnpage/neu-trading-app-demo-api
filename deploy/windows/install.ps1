$ErrorActionPreference = 'Stop'

$RepoUrl = if ($env:REPO_URL) { $env:REPO_URL } else { 'https://github.com/thejohnpage/neu-trading-app-demo-api.git' }
$InstallDir = if ($env:INSTALL_DIR) { $env:INSTALL_DIR } else { Join-Path $HOME 'neu-trading-app-demo-api' }

function Require-Command($Name) {
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Required command '$Name' was not found on PATH."
    }
}

Require-Command git
Require-Command java
Require-Command mvn

Write-Host '==> Checking Java'
java -version

if (Test-Path (Join-Path $InstallDir '.git')) {
    Write-Host '==> Updating repository'
    git -C $InstallDir pull --ff-only
} else {
    Write-Host '==> Cloning repository'
    git clone $RepoUrl $InstallDir
}

$ConfigFile = Join-Path $InstallDir 'deploy\windows\vm.env.ps1'
if (-not (Test-Path $ConfigFile)) {
@'
# Local VM configuration. Adjust these values for the Neueda VM.
$env:DB_URL = 'jdbc:postgresql://localhost:5432/trading_demo'
$env:DB_USERNAME = 'trading_demo'
$env:DB_PASSWORD = 'trading_demo_change_me'
$env:KAFKA_BOOTSTRAP_SERVERS = 'LINUX_VM_HOSTNAME_OR_IP:9092'
$env:SERVER_PORT = '8080'
'@ | Set-Content -Encoding UTF8 $ConfigFile
    Write-Host "==> Created $ConfigFile"
    Write-Host '    Edit DB credentials and the Linux VM Kafka hostname/IP before deploying.'
}

Write-Host "Repository installed at $InstallDir"
Write-Host 'Next: configure deploy\windows\vm.env.ps1, create the PostgreSQL database/user if required, then run:'
Write-Host '  .\deploy\windows\deploy.ps1'

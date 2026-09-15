$ErrorActionPreference = 'Stop'

$RootDir = Resolve-Path (Join-Path $PSScriptRoot '..\..')
Set-Location $RootDir

$ConfigFile = Join-Path $PSScriptRoot 'vm.env.ps1'
if (-not (Test-Path $ConfigFile)) {
    throw 'deploy\windows\vm.env.ps1 is missing. Run install.ps1 first and configure it.'
}

. $ConfigFile

Write-Host '==> Updating source'
git pull --ff-only

Write-Host '==> Building API'
mvn clean package -DskipTests

$Jar = Get-ChildItem -Path (Join-Path $RootDir 'target') -Filter '*.jar' |
    Where-Object { $_.Name -notlike '*.original' } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $Jar) { throw 'No runnable JAR found in target.' }

$PidFile = Join-Path $RootDir 'trading-api.pid'
if (Test-Path $PidFile) {
    $OldPid = Get-Content $PidFile -ErrorAction SilentlyContinue
    if ($OldPid) {
        $Process = Get-Process -Id $OldPid -ErrorAction SilentlyContinue
        if ($Process) {
            Write-Host "==> Stopping previous API process $OldPid"
            Stop-Process -Id $OldPid -Force
        }
    }
    Remove-Item $PidFile -Force
}

$LogDir = Join-Path $RootDir 'logs'
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null
$Stdout = Join-Path $LogDir 'api.out.log'
$Stderr = Join-Path $LogDir 'api.err.log'

Write-Host "==> Starting $($Jar.Name)"
$Process = Start-Process java -ArgumentList @('-jar', $Jar.FullName) -PassThru -RedirectStandardOutput $Stdout -RedirectStandardError $Stderr
$Process.Id | Set-Content $PidFile

$Port = if ($env:SERVER_PORT) { $env:SERVER_PORT } else { '8080' }
Write-Host '==> Waiting for API health'
for ($i = 0; $i -lt 40; $i++) {
    try {
        $Health = Invoke-RestMethod -Uri "http://localhost:$Port/actuator/health" -TimeoutSec 2
        if ($Health.status -eq 'UP') {
            Write-Host "API is healthy on port $Port (PID $($Process.Id))."
            Invoke-RestMethod -Uri "http://localhost:$Port/api/v1/version"
            exit 0
        }
    } catch { }
    Start-Sleep -Seconds 3
}

Write-Host 'API did not become healthy. Last error log lines:'
if (Test-Path $Stderr) { Get-Content $Stderr -Tail 100 }
exit 1

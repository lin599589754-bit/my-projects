$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$mysqlBase = "C:\Program Files\MySQL\MySQL Server 8.0"
$mysqlBin = Join-Path $mysqlBase "bin"
$mysqld = Join-Path $mysqlBin "mysqld.exe"
$mysql = Join-Path $mysqlBin "mysql.exe"
$ini = Join-Path $projectRoot "mysql-data\my-v2.ini"
$dataDir = Join-Path $projectRoot "mysql-data\Data"
$port = 3307

if (-not (Test-Path -LiteralPath $mysqld)) {
    throw "未找到 mysqld.exe：$mysqld"
}

if (-not (Test-Path -LiteralPath $mysql)) {
    throw "未找到 mysql.exe：$mysql"
}

if (-not (Test-Path -LiteralPath $dataDir)) {
    New-Item -ItemType Directory -Path $dataDir | Out-Null
}

if (-not (Test-Path -LiteralPath $ini)) {
    $mysqlBaseForIni = $mysqlBase -replace "\\", "/"
    $dataDirForIni = $dataDir -replace "\\", "/"
    $logFileForIni = (Join-Path $projectRoot "mysql-data\mysql-v2.err") -replace "\\", "/"
    $pidFileForIni = (Join-Path $projectRoot "mysql-data\mysql-v2.pid") -replace "\\", "/"

    $iniContent = @"
[mysqld]
basedir=$mysqlBaseForIni
datadir=$dataDirForIni
port=$port
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
default-storage-engine=INNODB
mysqlx=0
log-error=$logFileForIni
pid-file=$pidFileForIni

[client]
port=$port
default-character-set=utf8mb4
"@

    Set-Content -LiteralPath $ini -Value $iniContent -Encoding UTF8
}

$autoCnf = Join-Path $dataDir "auto.cnf"
if (-not (Test-Path -LiteralPath $autoCnf)) {
    $existingFiles = Get-ChildItem -LiteralPath $dataDir -Force | Where-Object { $_.Name -ne ".gitkeep" }
    if ($existingFiles.Count -gt 0) {
        throw "数据目录未初始化但不为空：$dataDir"
    }

    $gitkeep = Join-Path $dataDir ".gitkeep"
    if (Test-Path -LiteralPath $gitkeep) {
        Remove-Item -LiteralPath $gitkeep
    }

    Write-Host "正在初始化 MySQL v2 数据目录：$dataDir"
    & $mysqld --defaults-file="$ini" --initialize-insecure --console
    if ($LASTEXITCODE -ne 0) {
        throw "MySQL v2 数据目录初始化失败。"
    }
}

$listening = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
if ($listening) {
    Write-Host "MySQL v2 已在 $port 端口运行。"
} else {
    Write-Host "正在启动 MySQL v2，端口 $port..."
    Start-Process -FilePath $mysqld -ArgumentList "--defaults-file=`"$ini`" --console" -WindowStyle Hidden

    $ready = $false
    for ($i = 0; $i -lt 40; $i++) {
        Start-Sleep -Milliseconds 500
        if (Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue) {
            $ready = $true
            break
        }
    }

    if (-not $ready) {
        throw "MySQL v2 启动失败，请查看日志：$projectRoot\mysql-data\mysql-v2.err"
    }
}

& $mysql --defaults-file="$ini" "--host=127.0.0.1" "--port=$port" --user=root -e "SELECT VERSION() AS mysql_version;"

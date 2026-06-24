$ErrorActionPreference = "Stop"

$mysqlBase = "C:\Program Files\MySQL\MySQL Server 8.0"
$mysqladmin = Join-Path $mysqlBase "bin\mysqladmin.exe"
$port = 3307

if (-not (Test-Path -LiteralPath $mysqladmin)) {
    throw "未找到 mysqladmin.exe：$mysqladmin"
}

$listening = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
if (-not $listening) {
    Write-Host "MySQL v2 当前没有在 $port 端口运行。"
    exit 0
}

Write-Host "正在停止 MySQL v2..."
& $mysqladmin "--host=127.0.0.1" "--port=$port" --user=root shutdown
if ($LASTEXITCODE -ne 0) {
    throw "MySQL v2 停止失败。"
}

Write-Host "MySQL v2 已停止。"

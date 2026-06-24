$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$mysqlBase = "C:\Program Files\MySQL\MySQL Server 8.0"
$mysql = Join-Path $mysqlBase "bin\mysql.exe"
$ini = Join-Path $projectRoot "mysql-data\my-v2.ini"
$schemaSql = Join-Path $projectRoot "database\init-schema.sql"
$dataSql = Join-Path $projectRoot "database\init-data.sql"
$port = 3307

if (-not (Test-Path -LiteralPath $mysql)) {
    throw "未找到 mysql.exe：$mysql"
}

if (-not (Test-Path -LiteralPath $schemaSql)) {
    throw "未找到建表脚本：$schemaSql"
}

if (-not (Test-Path -LiteralPath $dataSql)) {
    throw "未找到演示数据脚本：$dataSql"
}

& (Join-Path $PSScriptRoot "start-mysql-v2.ps1")

Write-Host "正在执行建表脚本..."
& $mysql --defaults-file="$ini" "--host=127.0.0.1" "--port=$port" --user=root --default-character-set=utf8mb4 "--execute=SOURCE $schemaSql"
if ($LASTEXITCODE -ne 0) {
    throw "执行建表脚本失败。"
}

Write-Host "正在执行演示数据脚本..."
& $mysql --defaults-file="$ini" "--host=127.0.0.1" "--port=$port" --user=root --default-character-set=utf8mb4 "--execute=SOURCE $dataSql"
if ($LASTEXITCODE -ne 0) {
    throw "执行演示数据脚本失败。"
}

Write-Host "数据库 fresh_delivery_v2 已重建，当前表和演示数据如下："
& $mysql --defaults-file="$ini" "--host=127.0.0.1" "--port=$port" --user=root --default-character-set=utf8mb4 -e "USE fresh_delivery_v2; SHOW TABLES; SELECT 'category' AS table_name, COUNT(*) AS rows_count FROM category UNION ALL SELECT 'product', COUNT(*) FROM product UNION ALL SELECT 'user', COUNT(*) FROM user UNION ALL SELECT 'address', COUNT(*) FROM address UNION ALL SELECT 'cart', COUNT(*) FROM cart UNION ALL SELECT 'orders', COUNT(*) FROM orders UNION ALL SELECT 'order_item', COUNT(*) FROM order_item;"

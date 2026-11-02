$base      = "src\main\resources\assets\mariamod"
$modelsDir = Join-Path $base "models\item"
$itemsDir  = Join-Path $base "items"
$modId     = "mariamod"

if (-not (Test-Path $modelsDir)) {
    Write-Host "No existe $modelsDir. Corre el script desde la raiz del proyecto." -ForegroundColor Red
    exit 1
}

New-Item -ItemType Directory -Force -Path $itemsDir | Out-Null
$utf8 = New-Object System.Text.UTF8Encoding($false)   # UTF-8 sin BOM
$creados = 0; $existentes = 0

Get-ChildItem $modelsDir -Filter *.json | ForEach-Object {
    $name = $_.BaseName
    $dest = Join-Path $itemsDir "$name.json"
    if (Test-Path $dest) { $existentes++; return }

    $json = @"
{
  "model": {
    "type": "minecraft:model",
    "model": "${modId}:item/$name"
  }
}
"@
    [System.IO.File]::WriteAllText((Resolve-Path $itemsDir).Path + "\$name.json", $json, $utf8)
    $creados++
}

Write-Host "Creados: $creados | Ya existian: $existentes" -ForegroundColor Green

# Avisa si falta el modelo de algun item fijo
$fijos = "volatile_substrate","effect_substrate","primordial_substrate","seed_bag","puro"
foreach ($f in $fijos) {
    if (-not (Test-Path (Join-Path $modelsDir "$f.json"))) {
        Write-Host "Falta el modelo: models\item\$f.json" -ForegroundColor Yellow
    }
}
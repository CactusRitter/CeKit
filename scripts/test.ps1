<#
  Pasa los tests de unidad de una app Ce. Uso, desde la raiz de la app:
    powershell -NoProfile -ExecutionPolicy Bypass -File CeKit\scripts\test.ps1
  Nunca instala nada en dispositivos (solo tests JVM). Salida recortada: lineas de error y resumen.
#>
$ErrorActionPreference = 'Stop'
$root = (Get-Location).Path
$gradleDir = if (Test-Path (Join-Path $root 'app\build.gradle.kts')) { $root } elseif (Test-Path (Join-Path $root 'android\app\build.gradle.kts')) { Join-Path $root 'android' } else { throw 'Ejecuta el script desde la raiz de la app.' }
Push-Location $gradleDir
try {
    & .\gradlew.bat :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern '^e:|FAILED|BUILD|tests completed' | ForEach-Object { $_.Line.Substring(0, [Math]::Min(200, $_.Line.Length)) }
    exit $LASTEXITCODE
} finally { Pop-Location }

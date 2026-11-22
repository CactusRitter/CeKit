<#
  Publica una versión de una app Ce (Cemail, Cenotes, Celander). SOLO cuando César o la sesión Ce lo ordenen.

  Uso, desde la raíz de la app (donde está su CHANGELOG.md):
    powershell -NoProfile -ExecutionPolicy Bypass -File CeKit\scripts\release.ps1 -Version 2.3.0 [-DryRun]

  Antes hay que escribir release-notes\<versión>.md (en la raíz de la app) con las notas en tres idiomas:
    ## en
    - ...
    ## es
    - ...
    ## de
    - ...
  El script (con -DryRun solo comprueba y cuenta qué haría):
    1. Árbol de git limpio, CHANGELOG.md con «## Sin publicar» que tenga contenido, notas de la versión presentes.
    2. Sube versionCode (+1) y versionName en el build.gradle.kts de la app.
    3. CHANGELOG.md: «Sin publicar» pasa a «## <versión> — <fecha>» y queda una nueva «Sin publicar» vacía.
    4. Antepone «## <versión>» con su idioma a res/raw/changelog.txt, raw-es y raw-de (lo que muestra Ajustes → Actualizaciones).
    5. assembleRelease (falla si falta keystore.properties: nunca firma con la clave de depuración) y comprueba la firma con apksigner.
    6. Copia el APK a G:\Mi unidad\Ce Apps\<App>\<App>-<versión>.apk (no sustituye nada existente).
    7. Commit «Version x.y.z (versionCode n)», push y `gh release create vx.y.z` con el APK y las notas (## en/es/de).
#>
param(
    [Parameter(Mandatory)][ValidatePattern('^\d+\.\d+\.\d+$')][string]$Version,
    [string]$App = (Split-Path -Leaf (Get-Location)),
    [switch]$DryRun
)
$ErrorActionPreference = 'Stop'
function Set-Utf8 { param($Path, $Text) [IO.File]::WriteAllText($Path, $Text, (New-Object Text.UTF8Encoding $false)) }
$root = (Get-Location).Path
function Fail($m) { Write-Error $m; exit 1 }
function Step($m) { Write-Host "» $m" -ForegroundColor Cyan }

# Dónde está el proyecto Gradle (Celander lo tiene en android/).
$gradleDir = if (Test-Path (Join-Path $root 'app\build.gradle.kts')) { $root } elseif (Test-Path (Join-Path $root 'android\app\build.gradle.kts')) { Join-Path $root 'android' } else { Fail 'No encuentro app\build.gradle.kts: ejecuta el script desde la raíz de la app.' }
$buildFile = Join-Path $gradleDir 'app\build.gradle.kts'
$resDir = Join-Path $gradleDir 'app\src\main\res'
$changelog = Join-Path $root 'CHANGELOG.md'
$notesFile = Join-Path $root "release-notes\$Version.md"

Step "Comprobando $App $Version"
if (git status --porcelain) { Fail 'El árbol de git no está limpio: haz commit de lo pendiente antes.' }
if (-not (Test-Path $changelog)) { Fail 'Falta CHANGELOG.md' }
$cl = Get-Content $changelog -Raw -Encoding UTF8
if ($cl -notmatch '(?ms)^## Sin publicar\s*\r?\n(?<body>.*?)(?=^## |\z)') { Fail 'CHANGELOG.md no tiene «## Sin publicar».' }
if ([string]::IsNullOrWhiteSpace($Matches['body'])) { Fail '«Sin publicar» está vacío: no hay nada que publicar.' }
if ($cl -match "(?m)^## $([regex]::Escape($Version))\b") { Fail "CHANGELOG.md ya tiene la versión $Version." }
if (-not (Test-Path $notesFile)) { Fail "Falta $notesFile (notas con ## en, ## es y ## de)." }
$notes = Get-Content $notesFile -Raw -Encoding UTF8
foreach ($lang in 'en', 'es', 'de') { if ($notes -notmatch "(?m)^## $lang\s*$") { Fail "Las notas no tienen la sección ## $lang." } }
$build = Get-Content $buildFile -Raw -Encoding UTF8
if ($build -notmatch 'versionCode\s*=\s*(\d+)') { Fail 'No encuentro versionCode en build.gradle.kts.' }
$oldCode = [int]$Matches[1]; $newCode = $oldCode + 1
if ($build -notmatch 'versionName\s*=\s*"([^"]+)"') { Fail 'No encuentro versionName.' }
$oldName = $Matches[1]
if ($DryRun -eq $false -and -not ((Test-Path (Join-Path $gradleDir "keystore.properties")) -or (Test-Path (Join-Path $gradleDir "keystore\keystore.properties")))) { Fail 'Falta keystore.properties: no se publica sin la firma de release.' }
Write-Host "  versionName $oldName -> $Version · versionCode $oldCode -> $newCode"
$drive = "G:\Mi unidad\Ce Apps\$App"
$apkOut = Join-Path $drive "$App-$Version.apk"
if (Test-Path $apkOut) { Fail "$apkOut ya existe (no se sustituye)." }
if ($DryRun) { Write-Host 'DryRun: todo en orden, no se ha cambiado nada.' -ForegroundColor Green; exit 0 }

Step 'Subiendo versión'
$build = $build -replace 'versionCode\s*=\s*\d+', "versionCode = $newCode" -replace 'versionName\s*=\s*"[^"]+"', "versionName = `"$Version`""
Set-Utf8 $buildFile $build

Step 'CHANGELOG.md y notas de la app'
$date = Get-Date -Format 'yyyy-MM-dd'
$cl = [regex]::Replace($cl, '(?m)^## Sin publicar\s*$', "## Sin publicar`r`n`r`n## $Version — $date", 1)
Set-Utf8 $changelog $cl
function Section($lang) { if ($notes -match "(?ms)^## $lang\s*\r?\n(?<t>.*?)(?=^## |\z)") { $Matches['t'].Trim() } }
foreach ($pair in @(@('en', 'raw'), @('es', 'raw-es'), @('de', 'raw-de'))) {
    $file = Join-Path $resDir "$($pair[1])\changelog.txt"
    $old = if (Test-Path $file) { Get-Content $file -Raw -Encoding UTF8 } else { '' }
    New-Item -ItemType Directory -Force (Split-Path $file) | Out-Null
    Set-Utf8 $file ("## $Version`n$(Section $pair[0])`n`n$old")
}

Step 'assembleRelease'
Push-Location $gradleDir
try {
    & .\gradlew.bat assembleRelease --console=plain
    if ($LASTEXITCODE -ne 0) { Fail 'assembleRelease falló.' }
} finally { Pop-Location }
$apk = Get-ChildItem (Join-Path $gradleDir 'app\build\outputs\apk\release') -Filter *.apk | Where-Object { $_.Name -notmatch 'unsigned' } | Select-Object -First 1
if (-not $apk) { Fail 'No hay APK firmado en app\build\outputs\apk\release.' }
$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$apksigner = Get-ChildItem "$sdk\build-tools" -Recurse -Filter apksigner.bat | Sort-Object FullName -Descending | Select-Object -First 1
if (-not $apksigner) { Fail 'No encuentro apksigner en el SDK.' }
& $apksigner.FullName verify --print-certs $apk.FullName
if ($LASTEXITCODE -ne 0) { Fail 'La firma del APK no es válida.' }

Step "Copiando a $apkOut"
New-Item -ItemType Directory -Force $drive | Out-Null
Copy-Item $apk.FullName $apkOut

Step 'Commit, push y release de GitHub'
git add -A
git commit -m "Version $Version (versionCode $newCode)`n`nCo-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>"
git push
$tmpNotes = Join-Path $env:TEMP "$App-$Version-notes.md"
Set-Utf8 $tmpNotes $notes
$origin = (git remote get-url origin) -replace '^.*github\.com[:/]', '' -replace '\.git$', ''
gh release create "v$Version" $apkOut --title "$App $Version" --notes-file $tmpNotes -R $origin
if ($LASTEXITCODE -ne 0) { Fail "gh release create fallo (repo $origin); el APK ya esta en Drive y el commit en GitHub." }
Write-Host "Publicada $App $Version." -ForegroundColor Green

# CeKit

Módulos comunes de las apps Ce (Cemail, Cenotes, Celander): cristal (`ui/glass`), tema, etc. Paquete `com.ce.kit`.

## Enganchar a una app
```
git submodule add https://github.com/CactusRitter/CeKit.git CeKit
```
En `settings.gradle.kts` de la app: `apply(from = "CeKit/ce-kit-settings.gradle.kts")`.
En `app/build.gradle.kts`: `implementation(project(":kit"))`.
Los textos comunes viven en `kit/src/main/res/values{,-es,-de}` (alemán con «du»); no se duplican en la app.
Regla: lo común nuevo se escribe aquí, nunca en una app. Plan: `docs/CEKIT-PLAN.md` del repo Ce.

## Publicar una versión (solo con orden de César o de la sesión Ce)
`powershell -NoProfile -ExecutionPolicy Bypass -File CeKit\scripts\release.ps1 -Version x.y.z [-DryRun]` desde la raíz de la app.
Antes: `release-notes\x.y.z.md` con `## en`, `## es`, `## de`. El script sube versionCode/Name, mueve «Sin publicar» del CHANGELOG,
antepone las notas a `res/raw*/changelog.txt`, compila `assembleRelease` (falla sin `keystore.properties`), verifica la firma, copia el APK a
`G:\Mi unidad\Ce Apps\<App>\` y crea la release de GitHub. `-DryRun` solo comprueba.

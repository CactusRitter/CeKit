# Plantilla de CLAUDE.md para una app Ce (copiar y rellenar lo entre <>)

# <App>: <qué hace>
App Android (Kotlin + Compose) de la familia Ce. Normas de la familia: `D:\cesar\Documents\Claude Projects\Ce\CLAUDE.md` (leerlo). Lo común vive en el submódulo `CeKit/` (repo CactusRitter/CeKit): **no se copia código común a la app**; se arregla en CeKit y se actualiza el puntero (`git -C CeKit pull origin main`).

- Paquete `<com.x.app>` · versión `<x.y.z>` (versionCode `<n>`) · minSdk 26 · targetSdk 36 · repo `<https://github.com/CactusRitter/<App>>`.
- Gradle: raíz `<.>` (Celander: `android/`). Compilar/tests: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug`; `powershell -File CeKit\scripts\test.ps1`.
- Emulador: solo con el turno (`Ce\scripts\emu-lock.ps1 acquire <Sesion>` / `release`), siempre `adb -s emulator-5554`, nunca `installDebug`/`connectedAndroidTest` sin `ANDROID_SERIAL=emulator-5554`.
- Publicar: solo con orden de César o de la sesión Ce → `CeKit\scripts\release.ps1 -Version x.y.z` (notas en `release-notes\x.y.z.md`). Mientras: «Sin publicar» en CHANGELOG.md.
- Textos solo en `values`, `values-es`, `values-de` (alemán «du»); los comunes están en el kit.
- Diseño: solo CeTheme/glass del kit; sin colores, radios ni animaciones propios.
- Datos de César: migraciones probadas con sus datos reales; sin compatibilidad hacia atrás obligatoria salvo no perder datos.
- Específico de <App>: <lo propio>.

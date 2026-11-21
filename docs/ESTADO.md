# Estado de CeKit (2026-11-21)

Repo: github.com/CactusRitter/CeKit (público). Módulo `:kit`, paquete `com.ce.kit`. Cada app lo lleva como submódulo (`CeKit/`; Celander en `android/CeKit/`) y lo engancha con `apply(from = "CeKit/ce-kit-settings.gradle.kts")` + `implementation(project(":kit"))`. Regla: lo común nuevo se escribe aquí, nunca en una app; cambiar el kit → `git -C <app>/CeKit pull` en cada app, compilar, probar, commit del puntero.

## Qué hay
- `ui/glass`, `ui/theme` (CeTheme, CeAppearance, Skin, tokens), `Haptics`, movimiento (`Motion.kt`), `CeSizes`, `GlassDialog` (NO es ventana: ponerlo en la raíz de la pantalla), `GlassSheet`, `Controls` (chips y botones; en Retro 98 son biselados), `RubberBand`, `WheelPicker`.
- `ui/settings`: SettingsCard, ChoiceChip, SwitchRow, IconBubble, BigRow(titleBadge), `AppearancePage(settings, CeAppearanceActions, extra, styleExtra)`.
- `ui/update`: UpdateBanner, WhatsNew, UpdatesPage, DiagnosticsPage. `log`: AppLog, CrashLog. `update`: Updates, ReleaseNotes. `CeKit.init(context, CeAppConfig(...))` en la Application. FileProvider propio `${applicationId}.kit`.
- `scripts/release.ps1` (publicar con -DryRun). Menús/popups nunca por debajo de 0,92 de opacidad (`GlassTokens.menuAlpha`).

## Pendiente
- Informe UI 2026-11-21: puntos 5 (píldora de búsqueda de Cemail: artefacto) y 6 (sombras con retraso/recortadas) sin repro; 4 resuelto con captura de la página (sin probar con HTML real en móvil).
- GlassDialog en tablet (Cenotes: acotado al panel/columna).
- F4 cuentas/red, F5 resto (firma release en las 3, R8 en Cemail/Cenotes, plantillas CLAUDE.md, test.ps1), F6.
- Textos de Retro 98 en Celander (editor): interruptor «Todo el día», botón Guardar en blanco, selector Evento/Tarea redondo (de Celander).

## Pasada de controles por temas (2026-11-21)
Hecho (kit + apps): GlassMenu (⋮), GlassSwitch (casilla en Retro), SoftButton/AccentButton/SelectableChip/RemovableChip/SegmentedControl con rama Retro, GlassDialog, TaskCheckbox. Muestras vistas en emulador: Glass (todas), Retro (Cemail bienvenida, Celander editor, Cenotes ⋮), Paper y Brutalista (Celander editor); Moderno y AMOLED solo compilan.
Pendiente en el kit (lo detectaron las auditorías): GlassDialog con contenido libre y sin botón de confirmar (listas de opciones, selectores de fecha/lugar, visor de imagen); SoftButton con `enabled`; AccentButton con estado de carga; RemovableChip con estado de error; GlassMenuItem con contenido libre; SelectableChip circular de tamaño fijo (días de la semana de Celander); TaskCheckbox tri-estado; IconButton glass; campo de texto glass (TextField de Cemail); cajón lateral glass; fila de lista clicable. Sin tocar por decisión: IconButton de Material, TextField, ModalNavigationDrawer, filas del cajón.
Recortes de sombra: causa del punto 5 = AnimatedContent recortando (corregido en Cemail ListScreen, con `SizeTransform(clip=false)`; MailApp y ComposeScreen ya lo tenían). Vigilar: Celander PeriodPager clipToBounds por página (deliberado), Cenotes LinkChip (AnimatedVisibility expand), MailMotion clip en la apertura del correo.

## Pendiente tras 2.7.0 / 2.3.0 / 2.2.0 (2026-11-21)
- Comprobar en el OPPO: píldora de búsqueda sin recorte (cabecera ya sin AnimatedContent), menú ⋮ del lector (ventana propia), cajón (GlassKind.Card).
- F4 (cuentas): rama `f4-auth` en Cemail y Celander; pasos 3-5 sin hacer; verificar con sesiones reales antes de publicar.
- F5: hecho release.ps1, test.ps1, plantilla de CLAUDE.md, firma sin caer a debug, tests del kit (cadenas, versiones, notas). R8 probado en ramas `r8-prueba` (Cemail, Cenotes), sin probar IMAP/Gmail/push/OCR/widgets. Tests de migraciones de Cenotes: requieren exportar el esquema de Room (`exportSchema = true` + KSP `room.schemaLocation`) y pruebas instrumentadas (emulador) con datos reales de César: decisión de César.
- F6 (copia de seguridad común, repositorio de ajustes): sin empezar.
- Regla aprendida: GlassKind.Menu con blur usa la curva de hojas; sin blur (ventanas emergentes) el suelo 0,97. Todo lo que se abra sobre un WebView va en `GlassOverlayWindow`.

## Pasada de temas: completada con muestras (2026-11-22)
Moderno y AMOLED vistos en Cemail (Ajustes → Mensajes: interruptores, chips): coherentes. Glass, Retro, Paper, Brutalista vistos antes. Punto 6 (sombras con retraso/recortadas): sin repro en emulador; auditoría estática sin recortes visibles salvo vigilar Celander PeriodPager y Cenotes LinkChip. Avisos/menús/diálogos con blur: opacidad mínima 0,92; sin blur (ventana propia): 1,0.

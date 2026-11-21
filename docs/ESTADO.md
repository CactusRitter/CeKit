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

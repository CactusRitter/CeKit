# Ce Kit: qué hay en los módulos comunes

Inventario (2026-11-27). Mirarlo antes de programar algo que quizá ya exista; actualizarlo en el mismo commit que cambie el kit. Cada app lo usa como submódulo `CeKit` (Celander en `android/CeKit`). El repo es público: nada privado aquí.

| Parte | Qué ofrece | Cemail | Cenotes | Celander |
|---|---|---|---|---|
| kit `ui/glass` | superficies de cristal, GlassSwitch/Slider/Progress, TaskCheckbox, sombras, movimiento | sí | sí | sí |
| kit `ui` | GlassDialog/Sheet/Menu/Drawer/TextField, SoftButton, AccentButton, chips, WheelPicker, Haptics, iconos, CeSizes | sí | sí | sí |
| kit `ui/theme` | CeTheme, Skin, Appearance, acentos | sí | sí | sí |
| kit `ui/settings` | AppearancePage, SettingsCard, SwitchRow, ChoiceChip, IconBubble, BigRow | sí | sí | sí |
| kit `ui/update`, `update` | UpdateBanner, UpdatesPage, DiagnosticsPage, WhatsNew, notas de versión | sí | sí | sí |
| kit `log` | AppLog, CrashLog | sí | sí | sí |
| kit `backup` | BackupFormat, PrefsBackup | ambos | ambos | solo BackupFormat |
| kit `net/CeHttp` | cliente HTTP | sí | no | sí |
| kit `notify` | canales de notificación | sí | no | sí |
| kit `settings/CommonSettings`, `CeKit` | ajustes comunes e inicio en Application | ambos | ambos | solo CeKit |
| `auth` | GoogleAuth, MicrosoftAuth, TokenStore, TokenProvider, TokenFailure, AccountPhotos | sí | no | sí |

La tabla sale de buscar imports y nombres en el código; «no» significa que no se ha encontrado uso.

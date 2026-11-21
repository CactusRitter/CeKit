# F6: propuesta (necesita decisión de César antes de tocar datos)

## Repositorio de ajustes
Hoy: Cemail y Cenotes usan SharedPreferences (clase `SettingsRepository`, `StateFlow<AppSettings>`); Celander usa DataStore. Los ajustes comunes (acento, tema, estilo, skin, 3D, transparencia, comprobar actualizaciones) ya comparten la interfaz `CeAppearance` y `CeAppearanceActions` en el kit.
Propuesta: `CommonSettings` en el kit (data class con esos campos + clave y lectura/escritura sobre una interfaz `KeyValueStore`), con dos adaptadores (SharedPreferences y DataStore). Cada app conserva SU almacén y SUS claves actuales (nada migra: sin riesgo para los datos de César); solo se unifica la lógica de lectura/validación (enumeraciones desconocidas → valor por defecto, rangos, customHue). Beneficio: un solo sitio para arreglar «ajuste corrupto».
Decisión pedida: ¿sí a esto (bajo riesgo), o se descarta F6-ajustes?

## Copia de seguridad
Contenedor ZIP + pantalla (elegir qué incluir, importar, restaurar) es casi igual en las 3 apps; lo propio es el proveedor de datos (Room, imágenes, ajustes, cuentas). Propuesta: `BackupContainer` (zip con manifiesto versionado `backup.json` + entradas) y `BackupPage(provider)` en el kit; cada app implementa `BackupProvider { sections(): List<Section>; export(section, out); import(section, input) }`. El formato de las copias de César no cambia en esta fase (solo se lee con el contenedor nuevo si el manifiesto coincide).
Riesgo: restaurar mal una copia real. Condición: pruebas con copias reales de César en el emulador antes de publicar. Decisión pedida: ¿se hace, y quién aporta copias reales de prueba?

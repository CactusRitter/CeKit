// Solo para compilar el kit solo. Dentro de una app, los plugins los carga el build raíz de la app.
plugins {
    alias(kitLibs.plugins.android.library) apply false
    alias(kitLibs.plugins.kotlin.compose) apply false
}

// Las apps Ce lo enganchan desde su settings.gradle.kts con:   apply(from = "CeKit/ce-kit-settings.gradle.kts")
// (CeKit = el submódulo git). Crea el catálogo `kitLibs` y el módulo `:kit`.
val kitDir = File(settings.settingsDir, "CeKit").takeIf { File(it, "ce-kit-settings.gradle.kts").exists() } ?: settings.settingsDir
dependencyResolutionManagement {
    versionCatalogs {
        create("kitLibs") { from(files(File(kitDir, "gradle/libs.versions.toml"))) }
    }
}
include(":kit")
project(":kit").projectDir = File(kitDir, "kit")
// Solo para apps con cuentas: en su build.gradle.kts, además de project(":kit"), implementation(project(":kitauth")).
include(":kitauth")
project(":kitauth").projectDir = File(kitDir, "auth")

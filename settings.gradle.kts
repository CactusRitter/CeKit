pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com[.]android.*")
                includeGroupByRegex("com[.]google.*")
                includeGroupByRegex("androidx[.].*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Aquí el kit se compila solo (para probarlo). Las apps no usan este archivo: aplican `ce-kit-settings.gradle.kts`.
rootProject.name = "CeKit"
apply(from = "ce-kit-settings.gradle.kts")

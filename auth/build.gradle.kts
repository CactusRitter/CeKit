// Inicio de sesión (Google, Microsoft) y almacén de tokens. Aparte de :kit porque AppAuth exige un placeholder de
// manifiesto (appAuthRedirectScheme) que solo las apps con cuentas tienen (Cemail, Celander); Cenotes no lo usa.
plugins {
    id("com.android.library")
}

android {
    namespace = "com.ce.kit.auth"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions { unitTests.isReturnDefaultValues = true }
}

dependencies {
    api(kitLibs.appauth)
    api(project(":kit"))
    implementation(kitLibs.androidx.core.ktx)
    implementation(kitLibs.kotlinx.coroutines.android)

    testImplementation(kitLibs.junit)
}

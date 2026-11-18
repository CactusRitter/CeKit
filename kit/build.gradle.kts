// Plugins sin versión: vienen del build raíz (de la app o de CeKit).
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.ce.kit"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions { unitTests.isReturnDefaultValues = true }
}

dependencies {
    api(platform(kitLibs.androidx.compose.bom))
    api(kitLibs.androidx.compose.ui)
    api(kitLibs.androidx.compose.ui.graphics)
    api(kitLibs.androidx.compose.foundation)
    api(kitLibs.androidx.compose.material3)
    api(kitLibs.haze)
    api(kitLibs.haze.blur)
    implementation(kitLibs.androidx.core.ktx)
    implementation(kitLibs.androidx.compose.ui.tooling.preview)

    testImplementation(kitLibs.junit)
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.saicmotor.tts.tester"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.saicmotor.tts.tester"
        minSdk = 26
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        abortOnError = false
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    // Meme moteur que :app (voir docs/TTS_ENGINE.md) -- pas de modele embarque
    // ici : les voix/espeak-ng-data sont poussees par adb dans Download/sherpa
    // (voir TesterEngine), comme pour l'ancienne pockettts-tester (eviter de
    // regonfler l'APK avec ~259 Mo de modeles).
    implementation("com.github.k2-fsa:sherpa-onnx:v1.13.8") {
        exclude(group = "androidx.core", module = "core")
        exclude(group = "com.github.k2-fsa.sherpa-onnx", module = "sherpa-onnx-jvm")
    }
    // sherpa-onnx est compile en Kotlin (classes com.k2fsa.sherpa.onnx.*) et a
    // besoin de kotlin-stdlib au runtime (NoClassDefFoundError/
    // "Failed resolution of: Lkotlin/jvm/internal/Intrinsics;" sinon). Dans
    // :app c'est tire transitivement par okhttp/fastjson ; ce module n'a rien
    // d'autre qui le tire, donc declare explicitement.
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
}

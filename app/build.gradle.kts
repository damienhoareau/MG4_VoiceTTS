import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Lecture des coordonnees de signature depuis gradle.properties (voir SIGNING.md).
val keystoreFile = (project.findProperty("MG4_KEYSTORE_FILE") as String?)?.let { file(it) }
val keystoreTypeProp = project.findProperty("MG4_KEYSTORE_TYPE") as String? ?: "jks"
val keystorePassword = project.findProperty("MG4_KEYSTORE_PASSWORD") as String? ?: ""
val keyAliasProp = project.findProperty("MG4_KEY_ALIAS") as String? ?: ""
val keyPasswordProp = project.findProperty("MG4_KEY_PASSWORD") as String? ?: ""
val hasSigningConfig = keystoreFile?.exists() == true && keyAliasProp.isNotBlank()

android {
    namespace = "com.saicmotor.voicetts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.saicmotor.voicetts"
        minSdk = 26
        targetSdk = 28
        versionCode = 7
        versionName = "1.4.2.21"
    }

    // Le code genuinement propre a cette app (garde tel quel, decompile jadx) :
    //   com/saicmotor/**  -> logique SaicMotor d'origine
    //   com/alibaba/**    -> fastjson, bibliotheque Java pure sans dependance Android
    //
    // com/iflytek/** (SDK TTS iFlytek) et com/nuance/** (SDK TTS Nuance/DragonDrive)
    // ont ete SUPPRIMES : les deux moteurs sont remplaces par Pocket TTS (offline,
    // voir docs/POCKET_TTS.md et pockettts-core/). Leurs .so (jniLibs/arm64-v8a/)
    // ont ete retires avec eux.
    //
    // Les paquets android/**, com/android/**, com/google/**, org/**, javax/**, androidx/**,
    // okhttp3/**, okio/** ont ete DEPLACES (pas supprimes) vers ../excluded-framework-sources/
    // a la racine du projet : ce sont des classes internes de la plateforme Android que jadx
    // a decompilees par erreur en les prenant pour du code applicatif. Les garder dans le
    // sourceSet provoquerait des conflits "duplicate class" avec le vrai android.jar public
    // fourni par compileSdk (ou, pour androidx/okhttp, sont remplacees par de vraies
    // dependances Gradle ci-dessous).

    useLibrary("org.apache.http.legacy")

    signingConfigs {
        if (hasSigningConfig) {
            create("aosp") {
                storeFile = keystoreFile
                storeType = keystoreTypeProp
                storePassword = keystorePassword
                keyAlias = keyAliasProp
                keyPassword = keyPasswordProp
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Le vehicule reel est arm64-v8a uniquement (voir docs/TTS_ENGINE.md,
            // specs SAIC EH32 MY24) -- la dependance sherpa-onnx embarque ses .so
            // natifs pour 4 ABI (armeabi-v7a/arm64-v8a/x86/x86_64), inutiles pour
            // les 3 autres ici. Reduit sensiblement la taille de l'APK a installer
            // sur le vehicule. Le build debug (emulateur x86_64) reste multi-ABI,
            // pas de filtre ci-dessous.
            ndk {
                abiFilters += "arm64-v8a"
            }
            if (hasSigningConfig) {
                signingConfig = signingConfigs.getByName("aosp")
            } else {
                logger.warn(
                    "MG4_VoiceTTS: aucun keystore configure (voir SIGNING.md) -- " +
                    "le build 'release' sera signe avec la cle debug par defaut et NE REMPLACERA PAS " +
                    "l'app d'origine sur le vehicule."
                )
            }
        }
        debug {
            if (hasSigningConfig) {
                signingConfig = signingConfigs.getByName("aosp")
            }
        }
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

    // L'AAR sherpa-onnx embarque aussi, en resources jar (pas des assets Android,
    // pas du jniLibs/) les binaires natifs desktop de son module JVM -- macOS et
    // Windows, ~110 Mo au total -- que l'exclude du module "sherpa-onnx-jvm" plus
    // bas ne retire pas puisqu'ils sont empaquetes dans l'AAR Android lui-meme.
    // Jamais charges sur Android (seuls lib/<abi>/*.so le sont) : pur gonflement
    // de l'APK, sur les deux variantes.
    packaging {
        resources {
            excludes += "sherpa-onnx/native/**"
        }
    }
}

dependencies {
    // NB: pas de dependance androidx.core/appcompat ici -- l'app garde son propre jeu de
    // ressources/styles AppCompat decompile en interne (voir styles.xml) pour rester une
    // SEULE source de ressources. Ajouter l'AAR androidx reel declenche un bug de fusion
    // de ressources sous Windows avec AGP 8.13 des qu'il y a 2 sources de resources (cf.
    // "Illegal char <:>" dans mergeXxxResources -- non lie au contenu, reproduit meme
    // avec un styles.xml sans doublon). okhttp est un .jar pur sans res/, donc sans risque.
    implementation(libs.okhttp)
    // com/alibaba/** (fastjson) deplace vers excluded-framework-sources/ : jadx a genere
    // 48 erreurs de syntaxe "??" (generiques non resolus) dans son decompile. fastjson a
    // un artefact Maven public reel, donc on l'utilise directement plutot que de corriger
    // le decompile a la main.
    implementation(libs.fastjson)
    // Moteur TTS (voir docs/TTS_ENGINE.md) : sherpa-onnx + voix Piper, embarque
    // directement (appel direct depuis TtsService, pas d'app separee). Publie
    // uniquement via JitPack (pas de Maven Central), voir settings.gradle.kts.
    // Exclusion androidx.core par precaution : meme bug de fusion de ressources
    // Windows/AGP que pour Pocket TTS/LiteRT (cf. "Illegal char <:>" plus haut) --
    // androidx.core entre en collision avec les ressources internes decompilees
    // de l'app des qu'il y a 2 sources de resources.
    implementation("com.github.k2-fsa:sherpa-onnx:v1.13.8") {
        exclude(group = "androidx.core", module = "core")
        // JitPack publie aussi un module "sherpa-onnx-jvm" (desktop) qui duplique
        // toutes les classes du vrai module Android (l'.aar) -- on ne veut que ce
        // dernier.
        exclude(group = "com.github.k2-fsa.sherpa-onnx", module = "sherpa-onnx-jvm")
    }
}

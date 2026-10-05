pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
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
        // sherpa-onnx (moteur TTS, voir docs/TTS_ENGINE.md) n'est publie que
        // via JitPack (build depuis le tag GitHub), pas Maven Central.
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MG4 Voice TTS"
include(":app")
include(":tts-tester")

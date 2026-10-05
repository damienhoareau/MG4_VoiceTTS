# MG4 Voice TTS

Projet Android Studio reconstruit depuis la décompilation (jadx) de
`com.saicmotor.voicetts` (SaicVoiceTTS_overseas_my24.apk v1.4.2.21), le service de
synthèse vocale de la MG4. But : pouvoir remplacer le moteur iFlytek propriétaire
par un moteur offline, tout en gardant le reste du comportement identique.

## Avertissements et licences

**Projet personnel de reverse engineering / interopérabilité**, non affilié à
SAIC Motor, MG, ni à aucun éditeur des SDK d'origine (iFlytek, Nuance/DragonDrive).
Fourni "tel quel", sans garantie d'aucune sorte ; usage à vos propres risques, sur
votre propre véhicule.

- **`com/saicmotor/**`** (code applicatif d'origine, décompilé via jadx depuis
  l'APK constructeur) reste la propriété de ses auteurs d'origine. Il est conservé
  ici uniquement dans un but d'interopérabilité personnelle (faire fonctionner un
  moteur TTS alternatif sur mon propre véhicule) — aucune licence de redistribution
  n'est accordée par l'ayant droit. Ne pas republier l'APK résultant, ni ce code,
  en dehors d'un usage personnel.
- **`excluded-framework-sources/`** : classes internes de la plateforme Android/AOSP
  (et éventuellement d'ajouts spécifiques constructeur) extraites par erreur par
  jadx — gardées uniquement pour référence, **jamais compilées** dans l'APK final
  (voir ci-dessous).
- **Clé de signature** (`keystore/aosp-platform.jks`) : clé "platform" AOSP publique
  et bien connue (mot de passe `android`), pas un secret — voir `SIGNING.md`.
- **Dépendances tierces** (ajoutées par ce projet, licences permissives) :
  - [sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx) — Apache-2.0 (k2-fsa)
  - [ONNX Runtime](https://github.com/microsoft/onnxruntime) (embarqué dans l'AAR
    sherpa-onnx) — MIT (Microsoft)
  - [Piper](https://github.com/rhasspy/piper) — MIT (voix VITS consommées par
    sherpa-onnx)
  - [espeak-ng](https://github.com/espeak-ng/espeak-ng) — **GPL-3.0-or-later** ;
    seules les données de phonémisation (`espeak-ng-data/`, dictionnaires) sont
    embarquées en assets, pas de code espeak-ng lié directement par ce projet
    (uniquement via sherpa-onnx, qui l'intègre lui-même)
  - OkHttp — Apache-2.0 (Square)
  - fastjson (`com.alibaba`) — Apache-2.0
- **Voix Piper par défaut** (téléchargées depuis le mirroir HuggingFace
  `csukuangfj/vits-piper-*`, voir `docs/TTS_ENGINE.md`) : chaque voix a sa propre
  licence selon le jeu de données vocal d'origine (généralement MIT/CC0/CC-BY selon
  la voix) — vérifier la licence de la voix précise avant toute redistribution ;
  ces modèles ne sont volontairement **pas commités** dans ce dépôt (voir
  `.gitignore`), uniquement téléchargés à la compilation/au premier lancement.

## Ce qui a changé par rapport au décompile brut

L'export jadx produisait ~5765 fichiers, dont la plupart n'étaient pas du vrai code
applicatif mais des classes internes du framework Android mal extraites (l'app étant
un système privilégié compilé contre la plateforme complète, pas le SDK public).
Ces paquets ont été **déplacés** (pas supprimés) vers `excluded-framework-sources/` :

- `android/**`, `com/android/**` — classes internes du framework (conflits
  "duplicate class" avec le vrai `android.jar` public)
- `com/google/**`, `org/**`, `javax/**` — ressources/stubs de plateforme (EGL,
  org.apache.http.legacy, etc.)
- `androidx/**`, `okhttp3/**`, `okio/**` — remplacés par de vraies dépendances Gradle
  (voir `app/build.gradle.kts`) plutôt que gardés en source décompilée
- `com/alibaba/**` (fastjson) — 48 erreurs de syntaxe dans le décompile jadx (génériques
  non résolus, `?? r5 = ...`) ; remplacé par le vrai artefact Maven `com.alibaba:fastjson`
- `ActivityMonitor.java` — code mort, jamais référencé ailleurs dans l'app
- `R.java`/`BuildConfig.java` des 4 anciens modules (`voicetts`, `voice_tts`,
  `iflytek/speech`, `saicmotor/speech/loader`) — ce sont des classes **auto-générées**
  par le build system, jamais du vrai code source ; les garder entrait en collision
  avec le R/BuildConfig régénéré par Gradle

Le vrai code applicatif (`com/saicmotor/**`) est resté tel quel, à une poignée
d'exceptions près : quelques appels à des **API cachées Android** (`@hide`,
invisibles du SDK public — ex. `TimedRemoteCaller`, `CarConfigManager`,
`android.app.backup.FullBackup`) ont été soit convertis en appels par réflexion
(même pattern que `CommonUtils.getPropByKey()` qui le faisait déjà pour
`SystemProperties`), soit remplacés par leur valeur littérale quand c'était une simple
constante — **chaque changement est commenté dans le code** avec le raisonnement.
Aucune logique métier n'a été modifiée (hors remplacement du moteur TTS, voir
ci-dessous).

`com/iflytek/**` (SDK TTS iFlytek) et `com/nuance/**` (SDK TTS Nuance/DragonDrive),
qui faisaient partie du décompile d'origine, ont depuis été **supprimés** avec le
reste du code mort qu'ils laissaient derrière eux (`Config.java`,
`CommonUtils.isThaiVersion()`, les `.so` de `jniLibs/arm64-v8a/`) une fois le moteur
TTS remplacé par Pocket TTS — voir la section suivante.

Ressources : les fichiers `.9.png` (9-patch) issus du décompile jadx ne recompilent pas
(limitation connue de jadx sur ce format) — une poignée de références à ces drawables
dans les styles AppCompat internes ont été redirigées vers `@android:color/transparent`
(cosmétique uniquement, aucun de ces styles n'est utilisé par le service TTS lui-même).

## Prérequis : JDK pour Gradle

La JBR embarquée dans Android Studio sur cette machine est en Java 25, trop récente
pour Gradle 8.13 (requis par AGP 8.13.1). Un JDK 21 portable a été installé à
`C:\Users\damie\jdks\jdk-21.0.12.1+1` (zip Temurin, sans installateur).

Dans Android Studio : **Settings → Build, Execution, Deployment → Build Tools → Gradle
→ Gradle JDK** → pointer vers ce dossier.

## Compiler

```bash
cd C:\Projects\MG4_VoiceTTS
export JAVA_HOME="C:\Users\damie\jdks\jdk-21.0.12.1+1"
./gradlew.bat assembleRelease
```

Marche nativement sous Windows (vérifié avec un `clean` complet puis un build
à froid) ; `:app` est un module Java pur. Nécessite un accès réseau au
premier build pour résoudre la dépendance sherpa-onnx (JitPack, voir
`docs/TTS_ENGINE.md`) -- ensuite mise en cache par Gradle comme toute
dépendance normale.

L'APK signé sort dans `app/build/outputs/apk/release/app-release.apk`, avec la même
signature que l'original (voir `SIGNING.md`) -- vérifié avec `apksigner verify`.

## Moteur TTS : sherpa-onnx + voix Piper (offline, embarqué)

iFlytek et Nuance/DragonDrive sont remplacés par
[sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx) (Apache-2.0, k2-fsa) +
voix [Piper](https://github.com/rhasspy/piper), **embarqué directement** dans
cette app (dépendance Gradle via JitPack, voir `docs/TTS_ENGINE.md`) — une
seule app à installer sur le véhicule, aucun moteur externe à régler. Le
point d'entrée reste `TtsService.java` → `doSpeakInThread()`, qui appelle
maintenant `SherpaVoiceEngine.speakAndWait()`
(`com/saicmotor/voicetts/sherpa/`, nouveau) au lieu de l'ancien
`TtsPlayer`/iFlytek.

Deux approches intermédiaires ont été essayées et abandonnées avant celle-ci :
un moteur neuronal maison (Pocket TTS, Kyutai — bug insoluble sur le modèle
français) puis l'app [SherpaTTS](https://github.com/woheller69/ttsengine)
séparée via l'API standard `android.speech.tts.TextToSpeech` (fonctionnelle,
mais deux apps à installer/régler plutôt qu'une, et licence GPLv3 de l'app
wrapper plutôt qu'Apache-2.0 de la bibliothèque seule). Voir
**`docs/TTS_ENGINE.md`** pour l'architecture actuelle et
`docs/POCKET_TTS.md` pour l'historique Pocket TTS.

À garder en tête si vous retouchez cette zone :
- `stopPrompt()` ne doit jamais couper le message contenant "obey traffic laws"
  (garde-fou déjà en place, ne pas le retirer)
- La gestion du focus audio (`requestFocusSuccessFul`) distingue déjà les prompts de
  navigation (focus exclusif) des prompts système (focus transitoire)
- La langue voix vient de `Settings.System` clé `SAICMOTOR_VOICE_CURRENT_LANGUAGE`
  (voir `SaicmotorVoice.java`) ; `TtsService.localeFor()` la convertit en
  `Locale` pour `TextToSpeech.setLanguage()` (voir docs/TTS_ENGINE.md)

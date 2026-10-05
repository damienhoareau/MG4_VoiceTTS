# Moteur TTS : sherpa-onnx + voix Piper, embarqué directement

**Statut (2026-10-03) : fait, validé de bout en bout sur émulateur.**

Pocket TTS a été abandonné (voir `docs/POCKET_TTS.md` pour tout l'historique :
conversion, optimisation mémoire, specs véhicule réelles). Cause : le modèle
français (24 couches, seule taille que Pocket TTS propose pour le français)
déclenchait sa détection de fin de phrase (EOS) dès la frame 1 pour n'importe
quel texte, systématiquement — vérifié identique en fp16 non quantifié (donc
pas un artefact de quantification de notre export) et avec le padding
`model_recommended_frames_after_eos: 8` du modèle lui-même appliqué (fix
tenté, insuffisant). Résultat : audio quasi silencieux en français quel que
soit le texte.

Une première étape de remplacement a utilisé [SherpaTTS](https://github.com/woheller69/ttsengine)
(`org.woheller69.ttsengine`, GPLv3) comme **app séparée**, `TtsService`
routant via l'API standard `android.speech.tts.TextToSpeech`. Ça fonctionnait
(validé de bout en bout), mais demandait d'installer et régler deux apps sur
le véhicule. **Remplacé** par l'approche actuelle : sherpa-onnx (la
bibliothèque sous-jacente de SherpaTTS, Apache-2.0) **embarqué directement**
dans `com.saicmotor.voicetts`, appelé en direct depuis `TtsService` — une
seule app à installer, pas de réglage "moteur par défaut" à faire, licence
Apache-2.0 uniquement (le wrapper GPLv3 de SherpaTTS n'est plus utilisé).

## Architecture

`TtsService.java` → `doSpeakInThread()` appelle directement
`SherpaVoiceEngine.speakAndWait(text, lang)`
(`com/saicmotor/voicetts/sherpa/SherpaVoiceEngine.java`, nouveau fichier qui
remplace l'ancien `PocketVoiceEngine`). Toute la logique de résolution de
langue existante (`SysLang`, `getCurLangForTTS`, `getCurLangForMap`) est
inchangée — seule la couche moteur change, exactement comme pour les
tentatives précédentes.

`SherpaVoiceEngine` :
- Un `OfflineTts` (classe sherpa-onnx) par langue, chargé paresseusement et
  gardé en cache (`ensureEngine()`).
- Les assets (`model.onnx`/`tokens.txt` par langue + `espeak-ng-data` partagé)
  sont extraits de l'APK vers `getNoBackupFilesDir()` au premier besoin
  (`copyAssetDir()`) : sherpa-onnx et espeak-ng font de vrais appels `fopen()`,
  pas d'accès via `AssetManager` — contrairement à Pocket TTS/LiteRT qui
  pouvait lire direct dans l'APK, il faut de vrais fichiers sur disque ici.
- `speakAndWait(text, lang)` : bloquant, `tts.generate(text, sid=0, speed=1.0)`
  (pas de version streaming/callback utilisable depuis Java — voir plus bas),
  puis écrit le résultat (`GeneratedAudio.samples`, float PCM à
  `tts.sampleRate()`, 22050 Hz pour les voix Piper utilisées) dans un
  `AudioTrack` par `write(..., WRITE_BLOCKING)`.
- `stop()` : coupe la lecture (`AudioTrack.pause()`+`flush()`) immédiatement ;
  ne peut pas interrompre un `generate()` déjà en cours (voir plus bas), mais
  Piper est rapide (un seul passage non autorégressif, pas comme Pocket TTS).
- `langFor(sysLang)` : `SysLang.EXT_<lang3>_<PAYS3>` -> code 2 lettres
  (`en`/`fr`/`de`/`es`), retombe sur l'anglais sinon.

### Piège JNI rencontré : `generateWithCallback` inutilisable depuis Java

`OfflineTts.generateWithCallback(text, sid, speed, callback: (FloatArray) -> Int)`
aurait permis de streamer l'audio au fur et à mesure (comme Pocket TTS) et de
couper une synthèse en cours (barge-in propre). Un lambda Java ciblant ce
callback compile sans erreur mais **plante au runtime** :

```
NoSuchMethodError: no non-static method
"...SherpaVoiceEngine$$ExternalSyntheticLambda0;.invoke([F)Ljava/lang/Integer;"
```

Le code natif fait un lookup JNI direct sur une signature specialisee
(`invoke([F)Ljava/lang/Integer;`, params/retour non-boxes pour le tableau)
que seul le compilateur **Kotlin** sait générer (bridge synthétique
spécifique aux lambdas Kotlin ciblant ce type de fonction) — un lambda Java
cible l'interface `Function1` générique standard (`invoke(Ljava/lang/Object;)
Ljava/lang/Object;`), qui ne matche pas. Contourné en utilisant `generate()`
(bloquant, sans callback) à la place — voir le commentaire dans
`SherpaVoiceEngine.speakAndWait()`.

## Dépendance Gradle

Pas de Maven Central : sherpa-onnx publie un AAR précompilé (JNI inclus) via
JitPack uniquement.

```kotlin
// settings.gradle.kts, dependencyResolutionManagement.repositories
maven { url = uri("https://jitpack.io") }

// app/build.gradle.kts, dependencies
implementation("com.github.k2-fsa:sherpa-onnx:v1.13.8") {
    exclude(group = "androidx.core", module = "core")
    exclude(group = "com.github.k2-fsa.sherpa-onnx", module = "sherpa-onnx-jvm")
}
```

Deux exclusions nécessaires, trouvées en itérant sur des échecs de build :
- `androidx.core` : même bug de fusion de ressources Windows/AGP que pour
  Pocket TTS/LiteRT (`androidx.core` entre en collision avec les ressources
  internes décompilées de l'app dès qu'il y a 2 sources de ressources).
- `sherpa-onnx-jvm` : JitPack publie aussi un module desktop (JVM) qui
  duplique toutes les classes du vrai module Android (`Duplicate class
  com.k2fsa.sherpa.onnx.OfflineTts found in modules sherpa-onnx-jvm-*.jar
  and sherpa-onnx-v*.aar`), sans rapport avec androidx.

## Voix embarquées

Les 4 langues réellement exposées dans le menu "Voix" du véhicule (voir
`docs/POCKET_TTS.md`, le menu n'offre ni italien ni portugais), voix Piper
"medium" :

| Langue | Code | Voix | Dépôt HuggingFace |
|---|---|---|---|
| Anglais | `en` | Lessac | `csukuangfj/vits-piper-en_US-lessac-medium` |
| Français | `fr` | Siwis | `csukuangfj/vits-piper-fr_FR-siwis-medium` |
| Allemand | `de` | Thorsten | `csukuangfj/vits-piper-de_DE-thorsten-medium` |
| Espagnol | `es` | Davefx | `csukuangfj/vits-piper-es_ES-davefx-medium` |

```bash
curl -sL "https://huggingface.co/<repo>/resolve/main/<nom>.onnx" -o model.onnx
curl -sL "https://huggingface.co/<repo>/resolve/main/tokens.txt" -o tokens.txt
```

Placés dans `app/src/main/assets/sherpa/voices/<code>/{model.onnx,tokens.txt}`.
`espeak-ng-data` (phonémisation, partagé entre langues) dans
`app/src/main/assets/sherpa/espeak-ng-data/` — récupéré depuis l'app
SherpaTTS elle-même (`adb pull
/sdcard/Android/data/org.woheller69.ttsengine/files/espeak-ng-data`) lors de
la première étape de ce travail, plutôt que recherché dans le dépôt
sherpa-onnx. **Total assets : ~259 Mo.**

## Choisir/télécharger une voix (outil de test *et* app véhicule)

Au-delà des 4 voix par défaut ci-dessus, Piper propose plusieurs voix par
langue (locuteur/qualité différents — voir le catalogue dans
`VoiceDownloader.java`, dupliqué à l'identique dans `:app`
(`com/saicmotor/voicetts/sherpa/VoiceDownloader.java`) et `:tts-tester`
(`com/saicmotor/tts/tester/VoiceDownloader.java`) puisque ce sont deux
modules application indépendants, sans bibliothèque commune pour l'instant) :
8 voix anglaises (US *lessac/amy/ryan/libritts_r* + GB *alan/jenny/southern
english female/alba*), 5 françaises, 5 allemandes, 5 espagnoles.

**Dans `:tts-tester`** : deux listes déroulantes (langue puis voix) + bouton
"Télécharger" dans l'écran principal. `espeak-ng-data` reste embarqué en
asset (18 Mo, fixe) ; seules les voix (qui changent) sont téléchargées à la
demande, dans le dossier privé de l'app (`getExternalFilesDir()`).

**Dans `:app`** (l'app qui remplace celle du véhicule) : même mécanisme,
mais **la voix par défaut embarquée en asset reste le comportement de base**
— choisir/télécharger une voix dans l'écran de `MainActivity` est optionnel
et n'a d'effet que si elle a été téléchargée avec succès
(`SherpaVoiceEngine.ensureVoiceDir()` vérifie `VoicePrefs` puis retombe sur
l'asset embarqué si la voix choisie est absente du disque). Ce mécanisme ne
peut donc jamais rendre l'app non fonctionnelle hors-ligne. Stockage dans
`getNoBackupFilesDir()/sherpa/voices/<lang>/<voiceId>/`, distinct du chemin
plat `sherpa/voices/<lang>/` qu'utilise la voix par défaut extraite des
assets — les deux coexistent sans collision.

Piège rencontré en testant ce telechargement dans `:tts-tester` (adb push) :
les fichiers poussés par `adb push` directement dans
`Android/data/<pkg>/files/` appartiennent à `shell`, et l'isolation FUSE de
cet émulateur bloque **même l'app elle-même** en lecture (`Permission
denied`, vérifié aussi bien directement qu'en passant par `run-as`, qui n'a
pas le même contexte de montage qu'un vrai processus app lancé normalement).
Un fichier *téléchargé par l'app elle-même* (HTTP, pas adb) n'a pas ce
problème : c'est tout l'intérêt de ce mécanisme par rapport à pousser des
modèles à la main.

## Validation de bout en bout (émulateur, 2026-10-03)

Smoke-test AIDL temporaire depuis `MainActivity` (retiré après coup — même
pattern que pour les tentatives précédentes) : `promptCommonWords("Bonjour
Damien, ceci est un test en francais.", ...)` → logs `extracting voice
'fr'...` → `extracting espeak-ng-data...` → `engine 'fr' ready in 3436ms,
sampleRate=22050` → `end prompt...`, **son entendu par l'utilisateur**, aucune
erreur. Chaîne complète AIDL → `TtsService` → `SherpaVoiceEngine` →
sherpa-onnx → `AudioTrack` confirmée.

## Moteur TTS système standard (optionnel)

Au-delà de son propre AIDL `ITtsService` (ce que le véhicule utilise déjà),
`com.saicmotor.voicetts` peut **aussi** s'enregistrer comme moteur TTS
système standard, sélectionnable dans Paramètres > Synthèse vocale > Moteur
préféré — exactement comme le faisait l'app SherpaTTS séparée, mais
maintenant porté par ce même module. Inspiré de l'implémentation de
SherpaTTS (`com/k2fsa/sherpa/onnx/tts/engine/TtsService.kt` et les 3
activités `GetSampleText`/`CheckVoiceData`/`InstallVoiceData` du dépôt
[woheller69/ttsengine](https://github.com/woheller69/ttsengine)), porté en
Java :

- **`SherpaTextToSpeechService.java`** (`com/saicmotor/voicetts/sherpa/`) :
  étend `android.speech.tts.TextToSpeechService`. `onIsLanguageAvailable`/
  `onLoadLanguage` convertissent le code ISO3 reçu (ex. `"fra"`, conversion
  ISO2→ISO3 faite par Android lui-même) via le nouveau
  `SherpaVoiceEngine.langForIso3()`. `onSynthesizeText` appelle
  `SherpaVoiceEngine.synthesize()` (nouvelle méthode, factorisée hors de
  `speakAndWait()` pour être réutilisée ici), convertit les échantillons
  float en PCM 16 bits (`floatToPcm16`, même logique que
  `floatArrayToByteArray` côté SherpaTTS) et les renvoie à
  `SynthesisCallback.audioAvailable()` par blocs bornés par
  `getMaxBufferSize()`.
- **Pas de `generateWithCallback()`** ici non plus (même piège JNI que
  `SherpaVoiceEngine.speakAndWait`, confirmé par un commentaire identique
  dans le `TtsService.kt` amont : *"FIX: Use a function reference (::ttsCallback)
  instead of an inline lambda. This forces the Kotlin compiler to generate
  the correct JNI signature"* — un reference de méthode **Kotlin** fait ce
  qu'aucun lambda/reference Java ne peut reproduire). Génération en un seul
  bloc puis renvoi fragmenté — latence ajoutée négligeable, Piper/VITS n'étant
  pas autorégressif.
- **3 activités** (`GetSampleTextActivity`, `CheckVoiceDataActivity`,
  `InstallVoiceDataActivity`) + **`res/xml/tts_engine.xml`**
  (`settingsActivity` pointant vers `MainActivity`, qui sert donc aussi
  d'écran de configuration du moteur) + un second `<intent-filter>`
  (`CONFIGURE_ENGINE`) sur `MainActivity`.
- Déclaré dans le manifest comme un **second** `<service>`, indépendant de
  `TtsService` (AIDL) — les deux coexistent, rien n'empêche le véhicule de
  continuer à utiliser l'AIDL pendant que ce moteur système est aussi
  disponible pour d'autres apps.

**Validé (émulateur, 2026-10-04)** : `adb shell settings put secure
tts_default_synth com.saicmotor.voicetts` → Paramètres affiche bien
"Preferred engine: SaicService", les 4 langues (English/French/German/Spanish)
apparaissent dans le sélecteur de langue, `CHECK_TTS_DATA` répond PASS sans
erreur. Un vrai client `android.speech.tts.TextToSpeech` (smoke-test
temporaire depuis `MainActivity`, retiré après coup) confirme la chaîne
complète : `init status=0 engine=com.saicmotor.voicetts` →
`setLanguage(FRENCH)=0` → `speak()=0` → `SherpaVoiceEngine: engine 'fr' ready
in 3631ms` → **son entendu par l'utilisateur**. Seule anomalie non résolue :
le bouton "Play" (aperçu) de l'écran Paramètres reste grisé même après
sélection explicite d'une langue supportée — sans incidence sur la synthèse
réelle (confirmée fonctionnelle par le test ci-dessus), cause non
identifiée (peut-être une spécificité de cette version d'Android/cet
émulateur plutôt qu'un vrai problème côté moteur).

## Nettoyage effectué

- Pocket TTS entièrement retiré (voir `docs/POCKET_TTS.md`) : `pockettts-core/`,
  `pockettts-tester/`, `PocketVoiceEngine.java`, ~968 Mo d'assets, dépendances
  Kotlin/LiteRT.
- L'étape intermédiaire "API TextToSpeech + app SherpaTTS séparée" également
  retirée : plus d'import `android.speech.tts.*` dans `TtsService.java`, plus
  besoin d'installer/régler `org.woheller69.ttsengine` sur le véhicule.

## Ce qui reste à faire

- **Installer sur le vrai véhicule** : juste l'APK `:app` (Download + install
  manuel, comme d'habitude) — une seule app cette fois, pas de réglage moteur
  par défaut à faire. Tout ce qui précède n'a été vérifié que sur l'émulateur
  x86_64.
- Vérifier la vitesse de synthèse sur le vrai CPU arm64 MT2712 (ONNX Runtime,
  profil de performance différent de LiteRT/XNNPACK — à mesurer, pas
  supposé). Piper/VITS n'étant pas autorégressif comme l'était Pocket TTS,
  de bonnes chances d'être nettement plus rapide même sur ce CPU modeste.
- `android:largeHeap` n'a pas été remis (pas nécessaire a priori, ONNX
  Runtime gère sa propre mémoire native) — à surveiller si un `OutOfMemoryError`
  apparaît sur le vrai véhicule malgré tout.

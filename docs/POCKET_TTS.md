> **Abandonné (2026-10-03)** — voir `docs/TTS_ENGINE.md` pour le moteur
> actuellement utilisé (SherpaTTS/sherpa-onnx) et pourquoi Pocket TTS a été
> remplacé (bug de détection de fin de phrase insoluble sur le modèle
> français). Ce document est gardé pour l'historique technique (conversion
> des modèles, optimisation mémoire, specs réelles du calculateur SAIC EH32),
> toujours potentiellement utile même si le moteur qu'il décrit n'est plus
> en service.

# Remplacement des moteurs TTS par Pocket TTS (offline)

But : remplacer iFlytek (`EngineType.MG_VOICE`) et DragonDrive/Nuance
(`NUANCE_FULL`/`NUANCE_HALF`) par [Pocket TTS](https://github.com/kyutai-labs/pocket-tts)
(Kyutai, ~100M params), un moteur neuronal qui tourne entièrement en local, pour
que l'app fonctionne sans connexion.

**Statut (2026-10-02) : fait et testé sur émulateur.** Les 6 langues de Pocket
TTS (anglais, allemand, espagnol, italien, portugais, français) sont
converties, intégrées, l'app compile nativement sous Windows en APK debug et
release installables (`gradlew.bat assembleRelease`, voir "Build complet" plus
bas), et une synthèse vocale de bout en bout a été vérifiée sur un émulateur
Android x86_64 (voir "Test sur émulateur" plus bas). iFlytek/Nuance
entièrement supprimés. WSL2 reste utile uniquement pour reproduire/ajouter une
langue (pipeline Python de conversion des modèles), plus pour le build Android.

**Empreinte mémoire réduite de ~84%** en retirant la signature `prefill`
inutilisée des graphes LM (voir "Empreinte mémoire" plus bas) : ~200 Mo par
moteur à 6 couches (anglais/allemand/espagnol/italien/portugais) au lieu de
1,24 Go, ~480 Mo pour le français (24 couches). `PocketVoiceEngine` limite à 2
le nombre de moteurs gardés en mémoire simultanément (éviction LRU).
**Specs RAM du calculateur réel obtenues (2026-10-02)** : 3,85 Go de RAM
totale + 2 Go de swap -- largement suffisant pour le profil mémoire actuel.
Voir "Empreinte mémoire" plus bas pour le détail et ce qui reste à vérifier
malgré tout (CPU arm64 réel non testé, vrai calculateur occupé par d'autres
services).

## Ce qui est fait

- **Module `pockettts-core`** (nouveau, racine du projet) : vendorisé depuis
  [`geneing/PocketTTS-LiteRT`](https://github.com/geneing/PocketTTS-LiteRT) (MIT,
  voir `pockettts-core/NOTICE.md`). C'est un moteur LiteRT (= TFLite) pur Kotlin,
  sans JNI custom. `minSdk` ramené de 31 à 26 pour rester compatible avec la
  plateforme réelle du véhicule (Android 9 / API 28, pas Android 10 comme indiqué
  par erreur dans une version précédente de ce document -- `targetSdk = 28` dans
  `app/build.gradle.kts` est la source fiable, pas les attributs
  `platformBuildVersionCode`/`platformBuildVersionName` du manifest décompilé,
  qui reflètent l'environnement de compilation de l'APK d'origine, pas forcément
  l'OS réellement flashé sur le véhicule). Rien dans le module n'est gated API 31 ;
  cette limite venait uniquement du support optionnel du NPU Tensor G5 qu'on
  n'utilise pas (voir le commentaire en tête de `pockettts-core/build.gradle.kts`).
- **Placement forcé 100% CPU** (`Placement.GOLD` dans `PocketVoiceEngine.java`) :
  aucun delegate GPU n'est utilisé. Le portage amont est valid é sur deux GPU de
  téléphones récents (Mali, Adreno) avec un bug de qualité audio documenté sur
  l'un des deux (silencieux : le delegate compile sans erreur mais déforme
  l'audio) — risque qu'on refuse de prendre sur le GPU automobile inconnu du
  MG4 (plateforme Android 9 -- encore plus ancienne que ce qu'on pensait au
  départ, ce qui renforce l'argument). Le CPU/XNNPACK élimine ce risque
  entièrement.
- **`com/saicmotor/voicetts/pockettts/PocketVoiceEngine.java`** (nouveau) :
  adaptateur qui charge les modèles depuis les assets de l'APK (offline, pas de
  téléchargement runtime), expose `speakAndWait(text, voice)` (bloquant, avec
  callback de streaming PCM vers un `AudioTrack` float 24 kHz) et `stop()`
  (barge-in).
- **`TtsService.java`** : les trois moteurs d'origine sont court-circuités —
  `engineType` est forcé à `MG_VOICE` dans `onCreate()` quel que soit ce que
  `platformConfig.getEngine()` renvoie, et la branche `MG_VOICE` de
  `doSpeakInThread()` appelle `PocketVoiceEngine` au lieu de l'ancien
  `TtsPlayer`/iFlytek. `privStopPrompt()` et `onDestroy()` sont mis à jour en
  conséquence. Le garde-fou `stopPrompt()` qui ne doit jamais couper le message
  "obey traffic laws" n'a pas été touché (toujours dans `doSpeakInThread`,
  inchangé). Le `ContentObserver` sur `vrVinNumber`/`vrSNNumber` qui ré-initialisait
  l'ancien moteur iFlytek (licence liée au VIN) est devenu un no-op : Pocket TTS
  n'a pas cette dépendance.
- **Multi-langue** : `PocketVoiceEngine` charge un moteur Pocket TTS distinct
  par langue (paresseux, un par langue réellement parlée, ~150 Mo de mémoire
  native chacun), chacun pointant vers son propre sous-dossier d'assets
  (`assets/pockettts/<code>/`). `langAndVoiceFor()` route
  `SysLang.EXT_deu_DEU`/`NAVI_deu_DEU` → allemand (voix `juergen`),
  `*_spa_ESP` → espagnol (`lola`), `*_ita_ITA` → italien (`giovanni`),
  `*_por_PRT` → portugais (`rafael`), `*_fre_FRA` → français (`estelle`, via
  le moteur 24 couches séparé `PocketTtsEngine24`), `*_eng_*` → anglais
  (`alba`). Tout le reste (thaï, et tout ce que Pocket TTS ne couvre pas du
  tout) retombe sur l'anglais. Les 6 langues de Pocket TTS sont maintenant
  toutes câblées -- voir "Langues" plus bas pour le détail par langue.

## Fichiers modèles : produits (2026-10-01)

Les 15 fichiers requis par `PocketTtsEngine.requiredFiles()` en placement 100% CPU
sont maintenant dans `app/src/main/assets/pockettts/` (~145 Mo) : LM int8
(`pt_flowlm_fused_dyn8_all.tflite`, 87.5 Mo), `pt_mimi_dec_tx_fp16.tflite`,
`pt_mimi_deconly_fp16.tflite` + sa variante fenêtrée `_w512` pour le streaming,
les 4 assets hôte (`pt_embed_f16.bin`, `pt_input_linear_f32.bin`,
`pt_bos_input_f32.bin`, `pt_neutral_latent_f32.bin`), `pt_tokenizer.tsv`, et les
6 voix anglaises (`pt_voice_{alba,marius,javert,charles,mary,eve}.bin`).

Produits sous WSL2 (Ubuntu) avec `uv` (venv Python 3.10 géré automatiquement,
pas besoin d'un Python 3.10 système), en suivant `AGENTS.md` du dépôt
`geneing/PocketTTS-LiteRT`. Deux obstacles rencontrés et contournés :

1. **`tokenizers==0.10.3`** (dépendance transitive de `litert-torch` via
   `transformers==4.12.2`) ne compile pas avec un `rustc` récent : son code
   (2021) viole le lint `invalid_reference_casting`, passé en `deny` par défaut
   dans les versions récentes de Rust. Contournement :
   `RUSTFLAGS="--cap-lints warn"` autour de `uv pip install`.
2. **`stage_fused` plante** pendant sa propre vérification de parité interne
   (`RuntimeError: WriteTensorBuffer: source buffer is larger than the tensor
   buffer`) après avoir exporté les fichiers avec succès -- bug dans le script de
   validation, pas dans l'export lui-même. Contournement : relancer les étapes
   suivantes (`dectx`, `deconly`, `assets`, `quant`, `stream`) individuellement
   plutôt que via `all`, puisque chacune ne dépend que des fichiers déjà écrits
   sur disque, pas du bon déroulement de `stage_fused`. Toutes se sont terminées
   sans erreur, avec de bonnes corrélations de parité (ex. `dectx corr 1.000000`,
   LM int8 `lat corr 0.999842`).

Script complet utilisé (adaptable, les chemins sont sous `~/pockettts-build` dans
le WSL de cette machine) :

```bash
# Dans WSL2 :
curl -LsSf https://astral.sh/uv/install.sh | sh
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y

mkdir -p ~/pockettts-build && cd ~/pockettts-build
git clone https://github.com/kyutai-labs/pocket-tts.git references/pocket-tts
git -C references/pocket-tts checkout 001cf6e
git clone --depth 1 https://github.com/geneing/PocketTTS-LiteRT.git litert-repo
cp -r litert-repo/scripts .

uv venv --python 3.10 .venv && source .venv/bin/activate
uv pip install torch==2.12.1 --index-url https://download.pytorch.org/whl/cpu
printf 'torch==2.12.1+cpu\n' > constraints.txt
RUSTFLAGS='--cap-lints warn' uv pip install -c constraints.txt \
    --extra-index-url https://download.pytorch.org/whl/cpu \
    -e references/pocket-tts -r scripts/requirements-convert.txt

export PYTHONPATH=$(pwd)/references/pocket-tts PT_OUT=$(pwd)/scripts/out
# `fused` ecrit pt_flowlm_fused.tflite (et sa variante fp16) AVANT de planter sur
# sa propre verification de parite interne (point 2 ci-dessus) -- le `|| true`
# est attendu, `quant` a seulement besoin du fichier .tflite deja sur disque.
python scripts/build_pockettts.py fused || true
python scripts/build_pockettts.py dectx
python scripts/build_pockettts.py deconly
python scripts/build_pockettts.py assets
PT_QUANT=dyn8_all python scripts/build_pockettts.py quant
python scripts/build_pockettts.py stream   # optionnel
```

## Ce qui n'est pas encore fait

### 1. Build complet : résolu (2026-10-02) -- marche nativement sous Windows

Le vrai problème n'était **pas** le bug Windows `InvalidPathException: Illegal
char <:>` en lui-même -- c'était un symptôme. La cause racine, découverte en
contournant d'abord le symptôme via WSL2 (voir plus bas), était une vraie
collision de ressources : `androidx.core:core:1.9.0`, tiré *transitivement*
par `com.google.ai.edge.litert:litert:2.2.0`, entre en collision avec les
ressources internes décompilées de l'app (qui embarquent leur propre copie
des attrs standard `fontProviderFetchStrategy`/`fontStyle` -- voir le
commentaire dans `app/build.gradle.kts` sur pourquoi ces ressources sont
vendues en interne plutôt que remplacées par un vrai androidx). C'est cette
collision, pas simplement "2 sources de ressources", qui faisait planter le
merger AAPT2 de façon à déclencher le bug de chemin Windows au passage.

Résolu en excluant `androidx.core` de la dépendance `litert` dans
`pockettts-core/build.gradle.kts` :

```kotlin
api(libs.litert) {
    exclude(group = "androidx.core", module = "core")
}
```

Pocket TTS/LiteRT n'a besoin d'aucune classe `androidx.core` au runtime pour
ce qu'on en fait (inférence tensorielle pure) ; si jamais c'était faux, ça se
manifesterait par un `ClassNotFoundException`/`NoSuchMethodError` explicite au
chargement de l'engine, pas un bug silencieux.

**Une fois cette exclusion en place, `:app:assembleDebug` et
`:app:assembleRelease` passent nativement sous Windows** (vérifié avec un
`gradlew clean` complet puis un build à froid, 55/55 et 88/88 tâches
réellement exécutées, aucun résidu de cache) -- le détour par WSL2 n'est **plus
nécessaire** pour le build Android une fois ce correctif en place. Il reste
utile pour le pipeline Python de conversion des modèles (`litert-torch` n'a
toujours pas de roue Windows, voir plus haut), mais pas pour Gradle/AGP.

`app-debug.apk` et `app-release.apk` font ~1 Go chacun (code + ~985 Mo
d'assets Pocket TTS pour les 6 langues). Le release est bien signé avec la clé
plateforme AOSP (`CN=Android, O=Android, Mountain View, CA` -- vérifié avec
`apksigner verify --print-certs`), donc en état de remplacer l'app d'origine
sur le véhicule comme prévu par `SIGNING.md`.

Chemin parcouru pour y arriver, pour mémoire :
1. Bump AGP 8.13.1 -> 8.13.2 : pas d'effet.
2. Bump AGP 8.13 -> 9.4.1 : nécessite Gradle >= 9.6, bond bien plus gros que
   prévu (migration majeure, abandonné).
3. Build depuis WSL2 avec un SDK Android Linux dédié (le SDK Windows ne peut
   pas être réutilisé, ses binaires comme `aapt.exe` ne tournent pas sous
   Gradle Linux) : contourne le symptôme (les `:` sont des caractères de nom
   de fichier valides sous Linux), ce qui a permis de voir la VRAIE erreur
   (la collision `androidx.core`) jusque-là masquée par le crash Windows.
4. Exclusion `androidx.core` -> corrige la cause racine -> le build Windows
   natif marche aussi. Le détour WSL2 (étape 3) a servi à diagnostiquer, pas
   à contourner indéfiniment.

Vérifié avant ce build réel par un `javac` autonome (classpath construit à la
main : `android.jar` API 35, les `.class` compilés de `pockettts-core`, le
jar `litert-api-2.2.0`, `kotlin-stdlib-2.2.20`, okhttp, fastjson) à chaque
étape -- ça n'a jamais pris un faux positif par rapport au build Gradle réel.

### 2. Test sur émulateur (2026-10-02)

Synthèse vocale de bout en bout vérifiée sur un AVD `Medium_Phone_API_36.0`
(x86_64), via un appel temporaire à `ITtsService.promptCommonWords()` depuis
`MainActivity` (retiré après coup -- pas un chemin de test permanent).
Résultat : `stream done: 26783ms textChunks=1 frames=45 firstAudio=18053ms
lm=6204ms decTx=3874ms seanet=9761ms` pour une phrase de 12 mots en anglais,
placement `lm:CPU dectx:CPU dec:CPU` confirmé (aucun delegate GPU utilisé,
comme prévu). Deux `AudioTrack: ... disabled due to previous underrun,
restarting` pendant la lecture -- récupérés automatiquement, synthèse non
affectée ; attendu puisque la génération (CPU émulé, sans accélération
matérielle) est plus lente que le temps réel ici, contrairement à ce qu'on
aurait sur un vrai téléphone ou véhicule.

Deux obstacles rencontrés et corrigés en cours de route :

1. **Stockage insuffisant sur l'AVD** (`INSTALL_FAILED_INSUFFICIENT_STORAGE`) :
   la partition data par défaut (6 Go, déjà aux 2/3 pleine avec les services
   Google) ne suffit pas pour un APK d'~1 Go. Remonté à 12 Go dans
   `~/.android/avd/Medium_Phone.avd/config.ini` (`disk.dataPartition.size`),
   avec un `-wipe-data` au redémarrage.
2. **Crash au lancement, systématique** :
   `NoClassDefFoundError: Landroidx/core/os/HandlerCompat` dans
   `androidx.work.impl.DefaultRunnableScheduler`, appelé par
   `androidx.startup.InitializationProvider` (un `<provider>` que *tout* app
   récupère dès que `androidx.work:work-runtime` traîne sur le classpath,
   qu'on utilise WorkManager ou non). Causé par l'exclusion `androidx.core`
   seule (section précédente) : ça retirait des classes dont WorkManager a
   réellement besoin **au démarrage**, pas juste des ressources. Corrigé en
   excluant aussi `androidx.work:work-runtime` (seul chemin qui tirait
   `androidx.core` en plus de la résolution directe -- vérifié avec
   `gradlew :pockettts-core:dependencies --configuration
   debugRuntimeClasspath`) : plus de `<provider>` WorkManager fusionné dans le
   manifeste, donc plus d'appel à `HandlerCompat`, et la collision de
   ressources reste réglée. Voir le commentaire dans
   `pockettts-core/build.gradle.kts` pour le détail complet.
   **Leçon** : exclure une dépendance transitive pour un problème de
   *ressources* peut casser des usages *runtime* bien réels de cette même
   dépendance ailleurs dans le graphe -- une collision de ressources ne veut
   pas dire que la dépendance entière est inutile.

Un troisième problème, lui lié à la RAM et pas au code, est documenté dans la
section suivante.

### 3. Empreinte mémoire : réduite de ~84% (2026-10-02), specs véhicule toujours inconnues

Pendant le test initial, le premier essai sur l'AVD (RAM par défaut, 2 Go)
est resté bloqué plus d'une minute sans avancer : le thread `pockettts-synth`
était en état **D** (uninterruptible sleep) avec `66%iow` et un swap rempli à
99,98% (`top -H`). Pas un bug de code -- le moteur Pocket TTS, une fois
chargé, retenait ~1,24 Go de mémoire native (`native=1239MiB` dans le log
`PocketTTS: engine ...`), et 2 Go de RAM invité ne suffisaient pas à la fois
pour Android et pour ça. Remonté à 4 Go (`hw.ramSize` dans le `config.ini` de
l'AVD) a fait disparaître ce symptôme précis, mais la vraie réduction est
venue d'ailleurs (voir ci-dessous).

**Cause trouvée et corrigée : la signature `prefill` inutilisée du graphe LM.**
Le fichier `pt_flowlm_fused_dyn8_all.tflite` embarquait deux signatures : la
vraie (`serving_default`, l'étape par token qu'on utilise) et `prefill` (un
traitement par lot du prompt texte, jamais appelé puisque
`PocketTtsConfig.usePrefill = false`). `CompiledModel.create()` compile et
garde en mémoire **toutes** les signatures d'un fichier, utilisées ou non --
le log de build le montrait déjà sans qu'on le remarque :
*"Replacing 7115 out of 7147 node(s) ... subgraph 0 (prefill)"* contre
seulement 668 nœuds pour l'étape réellement utilisée. Repéré en relisant le
log du test émulateur précédent.

Le script de conversion le permettait déjà (`PREFILL_TOKENS = int(os.environ.get("PT_PREFILL_TOKENS", "16"))`,
ligne ~75 de `build_pockettts.py`) : `PT_PREFILL_TOKENS=0` saute tout le bloc
qui ajoute la signature `prefill` à l'export. Le code Kotlin de
`pockettts-core` gérait déjà ce cas sans aucune modification --
`PocketTtsEngine.lmStepIn` essaie la signature d'index 1, et si elle
n'existe pas (fichier à une seule signature), retombe sur l'index 0 (voir le
commentaire *"a single-signature file (older drops) has only index 0"* déjà
présent dans le code vendorisé).

Les 6 langues ont été reconverties (`fused` + `quant` avec `PT_PREFILL_TOKENS=0`),
même recette que précédemment sinon. Parité identique à l'ancien export sur
toutes (chiffres `vs eager fp32` inchangés au chiffre près), comme attendu
puisque les maths de la signature utilisée ne changent pas.

**Résultat mesuré sur émulateur (anglais, 6 couches) :**

| | avec `prefill` | sans `prefill` |
|---|---|---|
| mémoire native | 1239 Mo | **199 Mo** (-84%) |
| temps de chargement | 19-25 s | **4,5 s** |
| taille fichier LM | 87,5 Mo | 85,7 Mo (quasi identique -- les poids sont partagés entre signatures) |

**Français (24 couches)**, même traitement : mémoire native **478 Mo**
(`lmSig=0`, routage `EXT_fre_FRA` → `estelle` vérifié de bout en bout),
synthèse aboutie sans erreur. Pas mesuré *avec* prefill pour comparaison
directe, mais cohérent avec le ratio anglais (~2,4x le chiffre 6 couches,
proche du ratio de taille de fichier LM 24 vs 6 couches).

**Ce que ça change pour le véhicule réel :**
- Un moteur à 6 couches coûte maintenant ~200 Mo (pas 1,24 Go). Le français
  ~480 Mo. Avec l'éviction LRU (`MAX_LOADED_ENGINES = 2`, déjà en place),
  le pire cas (2 moteurs chargés) tombe à moins de 1 Go au lieu de 2,5 Go+.
  Un profil bien plus raisonnable pour un système embarqué partagé.

### Specs RAM/CPU/GPU/NPU réelles du calculateur (2026-10-02)

Lues en direct sur le calculateur réel (SAIC EH32 MY24) via `adb shell cat
/proc/meminfo`, `cat /proc/cpuinfo`, `dumpsys SurfaceFlinger`, `getprop`,
`pm list features` et `lshal`/`service list` -- lecture seule, aucune
commande qui modifie l'état du device :

| | |
|---|---|
| `MemTotal` | **3 939 776 kB (~3,85 Go)** |
| `MemAvailable` | ~2,17 Go (plus pertinent que `MemFree`, qui n'était qu'à ~405 Mo -- le reste est du cache/buffers récupérable) |
| `SwapTotal` / `SwapFree` | 2 Go / 2 Go (0% utilisé au moment de la mesure -- pas de pression mémoire sur le système au repos) |
| CPU | MediaTek MT2712, 6 cœurs (big.LITTLE : 4 cœurs "revision 1" + 2 "revision 3") |
| ABI | **arm64-v8a** |
| GPU | **ARM Mali-T880**, OpenGL ES 3.2, driver `r26p0-01rel0` (architecture Midgard, ~2016) |
| NPU | **Aucun** -- pas de HAL `android.hardware.neuralnetworks` enregistré (`lshal`), pas de service NN (`service list`), rien côté MediaTek APU/MDLA dans les props. Seuls les HAL graphiques standards (allocator/composer/mapper) sont présents. |

**Conclusion RAM : la RAM n'est plus le facteur limitant identifié.** 3,85 Go
avec ~2,17 Go disponibles donne largement de la marge pour 1-2 moteurs
Pocket TTS à 200-480 Mo chacun, même en comptant le reste du système
(navigation, media, cluster, etc.) qui tourne déjà et occupe le `~1,68 Go`
non disponible.

**Conclusion GPU/NPU : `Placement.GOLD` (tout CPU) était le bon choix, pas
une précaution excessive.** Il n'y a pas de NPU à exploiter de toute façon
(`Accel.NPU` ne sera donc jamais sélectionné, cohérent avec le fait qu'on
n'embarque aucun graphe `_g5`/dispatch shim). Côté GPU, le Mali-T880 est une
génération Midgard bien antérieure aux deux GPU sur lesquels le portage
`PocketTTS-LiteRT` amont a été validé (Mali-G715 et Adreno, tous deux
Valhall/récents) -- le bug de dégradation audio silencieuse documenté sur un
Mali *récent* (voir plus haut, section sur le choix du placement) serait
donc au moins aussi probable, sinon plus, sur ce Mali plus ancien. Rien ne
justifie de revenir sur CPU-only pour ce véhicule précis.

Ce qui reste à vérifier malgré cette bonne nouvelle :
- **Toutes les mesures de mémoire/temps de chargement ci-dessus viennent de
  l'émulateur x86_64**, pas du vrai CPU arm64 MT2712. L'empreinte mémoire
  devrait être comparable (ce sont les mêmes graphes LiteRT/XNNPACK), mais
  la vitesse de génération peut différer -- non mesurée sur le calculateur
  réel dans cette session (l'app n'y a pas été installée, uniquement des
  lectures `/proc`).
- Le calculateur était dans un état de fonctionnement normal au moment de la
  lecture (`MemAvailable` ~2,17 Go), pas forcément représentatif d'un
  moment de forte charge (navigation + media + plusieurs services actifs en
  même temps) -- la marge réelle en usage peut être plus faible que ce
  chiffre au repos.

## Nettoyage iFlytek/Nuance (fait)

Une fois Pocket TTS branché, tout ce qui ne servait plus qu'aux deux anciens
moteurs a été supprimé :

- `com/iflytek/**` et `com/nuance/**` (packages entiers) ;
- `app/src/main/jniLibs/arm64-v8a/` (les 8 `.so` : DragonDrive, iFlytek
  `tts_jni_handle_lib`, etc. -- plus aucun `System.loadLibrary()` dans le code
  restant) ;
- `Config.java` (chemins de ressources iFlytek), `CommonUtils.isThaiVersion()` ;
- Dans `TtsService.java` : `initMGVoiceEngine()`, `getCode()`/`setCode()`
  (licence liée au VIN pour iFlytek), `changeNuanceLangToMgVoiceLang()`,
  `switchIntLangToNuanceLang()`, les champs `mPromptCApi`/`mTtsPlayer`/
  `mFlyInitialize`/`mgVoiceHasStartPrompt`, et la branche `NUANCE_FULL`/
  `NUANCE_HALF` de `doSpeakInThread()` (le moteur est maintenant appelé
  inconditionnellement, `engineType` étant forcé à `MG_VOICE` dans
  `onCreate()`) ;
- Dans `MainActivity.java` : la demande de permission `WRITE/READ_EXTERNAL_STORAGE`
  au lancement (ne servait qu'à charger les ressources iFlytek depuis le
  stockage externe) ;
- Dans `AndroidManifest.xml` : les permissions `WRITE_EXTERNAL_STORAGE`/
  `READ_EXTERNAL_STORAGE`.

Laissé en l'état, volontairement hors du périmètre "iFlytek/Nuance" bien que
plus utilisé par aucun code aujourd'hui :
- Les permissions `INTERNET`/`ACCESS_NETWORK_STATE` et la dépendance `okhttp`
  (aucun appel `okhttp3`/`ConnectivityManager` nulle part dans le projet) --
  retirer des permissions réseau déclarées a un impact au-delà du strict
  remplacement de moteur TTS, à confirmer séparément.
- `com/saicmotor/speech/loader/EngineType.java` garde `NUANCE_FULL`/
  `NUANCE_HALF` : `TTSConfigLoadHelper.java` s'en sert encore comme valeur par
  défaut en lisant la config véhicule (même si plus personne n'agit dessus
  ensuite) -- fichier de contrat de config, pas du code moteur.

Vérifié par un `javac` autonome sur les 22 fichiers restants (tout `app/src/main/java`
sauf `MainActivity.java`, qui a besoin de la classe `R` générée par Gradle) avec le
même classpath que précédemment : compile sans erreur, seul le warning
préexistant sur `synchronized (this.isSpeaking)` subsiste. Le build Gradle complet
reste bloqué par le même bug AGP/Windows que précédemment (voir plus haut).

## Langues : les 6 supportées par Pocket TTS sont toutes faites

### Allemand, espagnol, italien, portugais (2026-10-01)

Contrairement à ce qu'indiquait une version précédente de ce document, ces 4
langues existent chez Pocket TTS dans la **même géométrie que l'anglais** (6
couches, `pocket_tts/config/{german,spanish,italian,portuguese}.yaml`, à ne pas
confondre avec leurs variantes `_24l.yaml` plus grosses) — vérifié en lisant
directement la source `kyutai-labs/pocket-tts` (`tts_model.py`,
`default_parameters.py`) plutôt qu'un résumé web. Elles ont donc pu réutiliser
tel quel le pipeline de conversion et le moteur Kotlin de l'anglais.

`scripts/build_pockettts.py` a été patché pour être paramétrable par langue
(`PT_LANGUAGE`, `PT_VOICE`, plus les révisions HuggingFace associées) :
`TTSModel.load_model(language=PT_LANGUAGE)`, et les appels à
`load_voice_state("alba")`/la liste de voix de `stage_assets`/le téléchargement
du tokenizer qui pointaient tous en dur vers `languages/english/...`
redirigent maintenant vers `PT_LANGUAGE`. Voix par défaut utilisées, alignées
sur `DEFAULT_VOICE_FOR_LANGUAGE` de Pocket TTS : allemand `juergen`, espagnol
`lola`, italien `giovanni`, portugais `rafael`.

Même recette que l'anglais (`fused || true`, `dectx`, `deconly`, `assets`,
`quant` avec `PT_QUANT=dyn8_all`), lancée une fois par langue avec
`PT_LANGUAGE=german PT_VOICE=juergen`, etc. Toutes les 4 ont de bonnes
corrélations de parité (ex. allemand : `dectx corr 1.000000`, LM int8
`lat corr 0.999474`). Fichiers copiés dans
`assets/pockettts/{de,es,it,pt}/` (~122 Mo chacune ; l'anglais a aussi été
déplacé de la racine vers `assets/pockettts/en/` pour la cohérence).
Fenêtres de streaming SEANet (`stage stream`) non régénérées pour ces 4
langues (optionnel, juste une histoire de latence du premier son).

**Incident mémoire pendant la conversion du portugais** : WSL2 était plafonné
à 4 Go de RAM (`.wslconfig`), insuffisant pour l'étape `fused` sur cette
machine (16 Go au total). Remonté à 6 Go, daemon Gradle arrêté, et l'utilisateur
a fermé Android Studio -- la conversion est passée une fois ~5,4 Go libres côté
Windows. `.wslconfig` reste à `memory=6GB` ; à surveiller si ça recoince pour
le français (modèle 4x plus gros en couches).

`PocketVoiceEngine` (Java) a été réécrit en conséquence : un moteur
`PocketTtsEngine` distinct par langue (chargé paresseusement, gardé en cache,
~150 Mo de mémoire native chacun tant qu'il est chargé), chacun avec son propre
`AssetModelSource` pointant vers `assets/pockettts/<code>/`. Aucun changement
dans `pockettts-core` (Kotlin) n'a été nécessaire : les noms de fichiers restent
ceux que `PocketTts.kt` attend, seul le sous-dossier d'assets change. Vérifié
par le même `javac` autonome que précédemment (compile sans erreur).

### Français (2026-10-02)

Fait. Pocket TTS n'a **aucune** variante française à 6 couches -- seulement
`french_24l.yaml` (24 couches), avec un message d'erreur explicite dans
`tts_model.py` qui le confirme : *"For technical reasons, only a larger
24-layer model is available for French."* Contrairement aux 4 langues
ci-dessus, ça a demandé de toucher le script de conversion ET
`pockettts-core`, mais au final moins de surface que redouté :

- **Côté script Python** : `N_LAYERS = 6` n'était en fait qu'une seule
  constante en tête de fichier (`build_pockettts.py`) -- tout le reste du
  script la référence déjà par calcul (`G_KV = N_LAYERS * N_HEADS * HD`, etc.),
  sans valeur magique dupliquée ailleurs (vérifié par recherche exhaustive).
  Changée en `N_LAYERS = int(os.environ.get("PT_LAYERS", "6"))`. Avec
  `PT_LAYERS=24 PT_LANGUAGE=french_24l PT_VOICE=estelle`, le pipeline
  (`fused || true`, `dectx`, `deconly`, `assets`, `quant`) a tourné sans
  modification supplémentaire et avec une **bonne parité, comparable aux
  langues à 6 couches** : LM int8 `lat corr 0.999946`, `k corr 0.999947` --
  les astuces de ré-écriture GPU/CPU (GELU en polynôme tanh,
  dé-entrelacement RoPE) se sont donc transposées sans ajustement numérique.
  Le codec Mimi (`dectx`/`deconly`) n'était, comme prévu, pas affecté par la
  profondeur du LM.
- **Côté `pockettts-core` (Kotlin)** : `PocketTts.kt`/`PocketTtsEngine.kt`/
  `PocketTtsSession.kt` sont dupliqués en `PocketTts24.kt`/
  `PocketTtsEngine24.kt`/`PocketTtsSession24.kt` (`LAYERS = 24`, le reste
  identique), générés par substitution textuelle (`PocketTts.` ->
  `PocketTts24.`, etc.) plutôt que par une refonte en profondeur des classes
  partagées par les 5 langues à 6 couches déjà en place -- pour ne prendre
  aucun risque de régression sur ce qui marchait. `Voice`, `VoiceCatalog`,
  `PocketTtsConfig`, `Placement`, `SpTokenizer`, `ModelSource` etc. restent
  partagés tels quels : ce sont des conventions de noms de fichiers, pas des
  tailles de tenseurs, donc geometry-agnostic (`VoiceCatalog` découvre
  "estelle" automatiquement via sa liste `KNOWN`, sans modification).
  `PocketVoiceEngine.java` route `fr` vers un `PocketTtsEngine24` séparé
  (champ dédié, pas dans la `Map<String, PocketTtsEngine>` des 6 couches).
  Compile sans erreur (même `javac` autonome).
- Fichiers copiés dans `assets/pockettts/fr/` (358 Mo -- LM int8 322 Mo,
  nettement plus gros que les 87 Mo des langues à 6 couches, cohérent avec
  4x plus de couches). **Total des assets Pocket TTS : ~985 Mo pour les 6
  langues.** À comparer aux contraintes de stockage réelles du véhicule.

**Deux incidents mémoire pendant la conversion française**, plus sévères que
pour le portugais vu la taille du modèle (le `fused` fp32 fait 1,25 Go contre
339 Mo en 6 couches) : le process s'est fait tuer deux fois de suite par
manque de mémoire (une fois pendant `fused`, une fois au tout début de
`dectx`/`deconly`/`assets`), à chaque fois parce qu'Android Studio avait été
rouvert et retenait ~2 Go. Reparti une fois Android Studio refermé et ~5 Go
libres côté Windows. Aucun changement supplémentaire à `.wslconfig` (resté à
`memory=6GB`) n'a été nécessaire une fois la RAM hôte dégagée -- la vraie
contrainte ici est la RAM physique de la machine (16 Go) partagée avec les
autres applications ouvertes, pas la configuration WSL2 en elle-même.

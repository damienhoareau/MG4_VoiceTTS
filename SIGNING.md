# Signature

Cette app doit être signée avec la **clé "platform" AOSP** pour que `adb install -r`
la remplace en place sur le véhicule (même `packageName` + même certificat = mise à
jour acceptée par PackageManager, sans besoin de root).

## Provenance

- Toutes les apps système de la MG4 (CarService, SystemUI, le launcher, `voicetts`...)
  sont signées avec l'une des clés de test publiques d'AOSP (`testkey`, `platform`,
  `shared` ou `media`) — jamais une clé privée SAIC. Ce sont des clés **volontairement
  publiques**, publiées par Google dans le dépôt source AOSP pour que quiconque
  construit AOSP puisse signer ses builds de test.
- `com.saicmotor.voicetts` est signée avec la clé **`platform`** précisément — vérifié
  en comparant l'empreinte SHA-256 du certificat installé (`c8a2e9bc...`) avec celles
  des 4 clés officielles téléchargées depuis
  `https://android.googlesource.com/platform/build/+/refs/heads/main/target/product/security/`.

## Keystore fourni

`keystore/aosp-platform.jks` (format PKCS12) contient la paire clé privée/certificat
`platform` officielle. Mot de passe magasin et clé : `android` (alias `platform`).

## Comment le régénérer

```bash
curl -s "https://android.googlesource.com/platform/build/+/refs/heads/main/target/product/security/platform.x509.pem?format=TEXT" | base64 -d > platform.x509.pem
curl -s "https://android.googlesource.com/platform/build/+/refs/heads/main/target/product/security/platform.pk8?format=TEXT" | base64 -d > platform.pk8
openssl pkey -in platform.pk8 -inform DER -out platform.pem
openssl pkcs12 -export -in platform.x509.pem -inkey platform.pem -out aosp-platform.jks -name platform -password pass:android
```

Vérifier l'empreinte avant de faire confiance au résultat :

```bash
openssl x509 -in platform.x509.pem -noout -fingerprint -sha256
# doit afficher C8:A2:E9:BC:CF:59:7C:2F:B6:DC:66:BE:E2:93:FC:13:F2:FC:47:EC:77:BC:6B:2B:0D:52:C1:1F:51:19:2A:B8
```

## Installer sur le véhicule

`com.saicmotor.voicetts` n'est **pas persistent** (contrairement à `adapterservice`
par exemple), donc pas besoin de root + remplacement de fichier :

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

Si `adb install -r` échoue avec `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, c'est que la
signature ne correspond pas — revérifier avec :

```bash
apksigner verify --print-certs app-release.apk
```

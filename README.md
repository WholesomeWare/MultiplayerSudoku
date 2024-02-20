# Multiplayer Sudoku

Határidő: Március 20.

| Követelmény           | Appunk                          |
| --------------------- | ------------------------------- |
| Adatbázis             | ✅ Firebase Firestore Database   |
| Google szolgáltatások | ✅ Firebase Auth, Google sign-in |
| Szenzorok             | ❌ Nálunk nem lesz               |

Egyéb technikák, amitől bőven jó beadandó lesz ez a projekt:

- UI system: Jetpack Compose
- Intent-ek komolyabb használata, pl.: meghívó kódok megosztása közeli eszközökkel vagy más appokon keresztül

## Projekt részei

### Android app

Nyilván. Ebben fogsz segíteni, ezen fogunk együtt dolgozni.

### Firebase projekt

Minden szerver oldali cucc. Körbevezetlek majd, de úgy tervezem, hogy ehhez neked ne kelljen sokszor
nyúlnod. De ha meg szeretnéd tanulni akkor adhatok majd ehhez is hozzáférést.

```
Ha kell hozzáférés, rakok ide linket.
```

### GitHub repo

Itt vagy most! Ebben a repoban oldjuk meg a közös munkát és itt tárolunk minden fájlt, kódot,
jegyzetet. Ezt is fogjuk a tanárnak elküldeni, hisz link formában kérik a beadandót.

```
https://github.com/CsakiTheOne/MultiplayerSudoku
```

## Android projekt felépítése

- app
  - **manifest/AndroidManifest.xml**: app metaadatai. név, ikon, engedélyek, stb.
  - **kotlin+java**: minden logika és Compose esetén a UI is
    - **{package_name}**: az app azonosítója
      - **MainActivity.kt**: első felület, ami megjelenik
      - ui
        - **theme**: színek, stílusok, témák
        - **components**: újrahasználható UI elemek
      - **firebase**: minden szerverrel kapcsolatos kód
  - **res**: képek, ikonok, szövegek, stb.
- **Gradle Scripts**: ez build-eli az appot és kezeli a külső kódokat

| Technológiák      | Amit mi használunk                                                                                          | Amit a tanár tanítana                                          |
| ----------------- | ----------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------- |
| Nyelv             | [Kotlin](https://youtu.be/xT8oP0wy-A0?si=zEUFU8Q9PKs410wv)                                                  | [Java](https://youtu.be/m4-HM_sCvtQ?si=AGjvO837VE8ae9Bc)       |
| UI system         | [Jetpack Compose](https://youtube.com/playlist?list=PLQkwcJG4YTCSpJ2NLhDTHhi6XBNfk9WiC&si=KlFPtTBE798Rhhp8) | Hagyományos XML                                                |
| Design nyelv      | [Material 3 (You)](https://youtu.be/UHQPdP8qgrk?si=In52HxRv-RPCS1Ho)                                        | [Material 2](https://youtu.be/6HCeBHVPxEg?si=aAPgZZ_-QcnAIkd9) |
| Auth és adatbázis | [Firebase](https://youtu.be/vAoB4VbhRzM?si=zKo3aOUPglVwACgd)                                                | -                                                              |
| Build system      | [Gradle](https://youtu.be/kNswjy2hPHI?si=Uzcj6_JKzQv-8NAm)                                                  | Gradle                                                         |

## Kérdések, amiket meg kéne dumálni

### Legyen többnyelvűség támogatás?

Szeretnék ilyet, mert akkor nem csak magyaroknak terjeszthetném. Annyi plusz meló, hogy a szövegeket
nem kódba írjuk, hanem nyelv fájlokba és a kódban csak hivatkozunk a szövegekre. Picit macera, de
nem bonyolult. Ezt beadás után egyedül is meg tudnám csinálni ha nem szeretnél plusz munkát.

### Hány játékos játszhat együtt és hogyan?

1-2? 1-4? Csak 2? Lehessen egyáltalán egyedül? Vagy legyen felső határ? Talán az a legegyszerűbb,
ha egyedül is lehet és nincs felső határ. Talán azt a legkönnyebb lekódolni.
Arra gondoltam, hogy ilyen party rendszer lehetne, meghívó kódokkal.

## Környezet

Ezek kellenek, hogy a projektet futtatni tudd.

- Android Studio Iguana 2023.2.1 RC2 vagy újabb
- `google-services.json` fájl a Firebase projektből az `app` mappába (majd rakok ide linket hozzá,
  mert tilos GitHub-ra feltölteni)

## Bónusz tartalmak csak úgy szórakozásnak

- [Material 3](https://m3.material.io/)
  - [[Eszköz] Theme Builder](https://m3.material.io/theme-builder#/custom)
  - [[Videó] The perfect imperfection of Google's Material You](https://youtu.be/k7pks7yqQOc?si=vui2N3OHUH8apymD)
- [[Játék] Sudoku - The Clean One](https://play.google.com/store/apps/details?id=ee.dustland.android.dustlandsudoku)
- [[Videók] #BestPhonesForever](https://youtube.com/playlist?list=PLnKtcw5mIGUR-aMBz9AphxHzEH7Kt-azY&si=qRH-o3z5-3HMfC9n)
- [[Eszköz] Kotlin Playground](https://play.kotlinlang.org/)
- [[Eszköz] Google's Material Design icon library](https://fonts.google.com/icons)
- [[Eszköz] Pictogrammers (extended Material Design icon library)](https://pictogrammers.com/library/mdi/)

## Sudoku kutatás

- [Medium: Let’s make the Sudoku generator library in Kotlin](https://medium.com/@typical.dev/lets-make-the-sudoku-generator-library-in-kotlin-8e0dd45c72b6)
- [Android Sudoku Tutorial in Kotlin](https://youtube.com/playlist?list=PLJSII25WrAz72NhnBitybKMMX0_f1UEym&si=Pcc-ViwZYxzFOU8S)
  - [GitHub repo](https://github.com/patrickfeltes/sudoku-android-kotlin)

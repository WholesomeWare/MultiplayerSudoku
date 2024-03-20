# Multiplayer Sudoku

- [Multiplayer Sudoku](#multiplayer-sudoku)
  - [Projekt beállítása és futtatása](#projekt-beállítása-és-futtatása)
  - [Felosztás](#felosztás)
    - [Viki](#viki)
    - [Csáki](#csáki)

## Projekt beállítása és futtatása

A fejlesztői környezeten, külső könyvtárakon és generált fájlokon kívül minden megtalálható a Moodle-be feltöltött fájlok között.

- Android Studio Iguana 2023.2.1 vagy újabb
- `google-services.json` az `app` mappába, hogy működjön az authentikálás és az adatbázis elérés
- `debug.keystore` a `C:\Users\<felhasználónév>\.android` mappába vagy build-eléskor az appot ezzel aláírva, hogy működjön a Google bejelentkezés (enélkül is használható az alkalmazás, de csak email-jelszó páros bejelentkezési lehetőséggel)

## Felosztás

Mikor összeültünk, hogy közösen dolgozzunk, előfordult, hogy egymás laptopját használtuk,
ezért a GitHub statisztikái nem tükrözik teljesen a munka megoszlását.

Minden feladat mellett ott van, hogy kb. melyik fájlokat érinti az adott feladat.
A Kotlin fájlok elérési útja a következő helyről kiindulva vannak megadva:
`app/src/main/java/com.wholesomeware.multiplayersudoku/`

### Viki

- UI téma elkészítése `ui/theme/*`
- Bejelentkező felület és működése `LoginActivity.kt`
- Többi activity UI vázlat verziónak elkészítése
- Email-jelszó kombós bejelentkezés API kódja `firebase/Auth.kt`
- Néhány egyszerűbb adatbázis függvény `firebase/Firestore.kt`
- Többnyelvűség felületbe építése és magyar string-ek `res/values-hu/strings.xml`
- Játék felülete a sudoku tábla kivételével `GameActivity.kt`
- Szoba adatszerkezet `model/Room.kt`

### Csáki

- Projekt és GitHub repo elkészítése, beállítása `AndroidManifest.xml`, projekt és modul szintű `build.gradle.kts`, `settings.gradle.kts`
- Activity-k felületének véglegesítése és saját elemek készítése `ui/components/*`
- Sudoku játék logika és sudoku tábla UI `sudoku/*`, `GameActivity.kt`
- Google bejelentkezés API kódja `firebase/Auth.kt`
- Bonyolultabb adatbázis függvények és listener-ek `firebase/Firestore.kt`
- Ikon elkészítése `res/mipmap/*`
- Többnyelvűség előkészítése és angol string-ek `res/values/strings.xml`
- Adatszerkezetek kialakítása `model/*`, `Room.kt`-ban segítettem

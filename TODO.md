# Feladatok

## Viki

Android Studio-ban van egy `TODO` panel, amiben láthatod, hogy milyen feladatok vannak a projektben és hol. Ezt fölső menüben a `View` -> `Tool Windows` -> `TODO` menüponttal tudod megnyitni. Vannak feladatok, amiket magamnak írtam föl, de van, ami neked szól. Azért ide is próbálom felírni a dolgokat.

- [ ] Frontend
  - [x] Nézegesd meg a [Material 3 elemeit itt](https://m3.material.io/components) (minden elemnél meg lehet nézni a hozzá tartozó Jetpack Compose doksit is, ahol példa kódok is vannak)
  - [x] `LoginActivity.kt`
  - [x] `MainActivity.kt`
    - [x] Alap kinézet a fő funkcióknak
    - [x] Fiók törlés lehetőség (lehetőleg egy dialógus is legyen ott, ne lehessen olyan könnyen véletlen törölni)
  - [x] `LobbyActivity.kt` elkészítése (ehez lehet majd rajzolok)
  - [ ] `GameActivity.kt`
  - [ ] Többnyelvűség: összekötés felülettel és magyar string-ek megírása (az app alapértelmezett nyelve angol lesz, szóval a sima `strings.xml`-ben angol szövegek lesznek)
- [x] API
  - [x] Kaptál Firebase hozzáférést. Nézd meg email-jeid és ha megvan, fogadd el
  - [x] Regisztrálás és bejelentkezés email-jelszó kombóval: `firebase/Auth.kt` függvényeit használva a `LoginActivity.kt`-ben
  - [x] Kijelentkezés lehetőség a menüben: `firebase/Auth.kt`-ből a `signOut()` függvényt használva a `MainActivity.kt`-ben
  - [x] Firestore adatbázishoz tartozó kódokban segítség
  - Google bejelentkezés: ennek még nem írtam meg a kódját, de te fogod ezt is összekötni a hozzá tartozó gombbal a `LoginActivity.kt`-ben

## Csáki

- [x] Frontend
  - [x] Figma: `LobbyActivity.kt`
  - [x] Márka implementálása
- [ ] Adatszerkezetek kialakítása
  - [x] Player
  - [ ] Room
- [x] Adatbázis létrehozása
  - [x] Firestore adatbázis létrehozása Firebase projektben
  - [x] Biztonsági szabályok megírása
- [ ] API kódok írása
  - [ ] Auth
    - [x] Email-jelszó kombós regisztrálás, bejelentkezés, kijelentkezés kódja
    - [ ] Google bejelentkezés kódja
  - [x] Firestore
    - [x] Listener függvények a játék követéséhez

## Bónusz tartalmak csak úgy szórakozásnak

- [Material 3](https://m3.material.io/)
  - [[Eszköz] Theme Builder](https://m3.material.io/theme-builder#/custom)
  - [[Videó] The perfect imperfection of Google's Material You](https://youtu.be/k7pks7yqQOc?si=vui2N3OHUH8apymD)
- [[Játék] Sudoku - The Clean One](https://play.google.com/store/apps/details?id=ee.dustland.android.dustlandsudoku)
- [[Videók] #BestPhonesForever](https://youtube.com/playlist?list=PLnKtcw5mIGUR-aMBz9AphxHzEH7Kt-azY&si=qRH-o3z5-3HMfC9n)
- [[Eszköz] Kotlin Playground](https://play.kotlinlang.org/)
- [[Eszköz] Google's Material Design icon library](https://fonts.google.com/icons)
- [[Eszköz] Pictogrammers (extended Material Design icon library)](https://pictogrammers.com/library/mdi/)
- [Jetpack Compose kurzus Google-től](https://developer.android.com/courses/android-basics-compose/unit-1), de úgyis megdumálunk mindent meg szívesen megtanítok mindent én is
- Segítségért zaklass vagy ha épp nem vagyok elérhető, ezt ajánlom: [Jetpack Compose tutorial lejátszási lista](https://youtube.com/playlist?list=PLQkwcJG4YTCSpJ2NLhDTHhi6XBNfk9WiC&si=B8b-Gfaqi5kq7B4x) (kicsit régi, de a legtöbb dolog még mindig ugyanúgy működik)

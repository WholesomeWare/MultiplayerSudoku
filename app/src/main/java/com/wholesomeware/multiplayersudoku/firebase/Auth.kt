package com.wholesomeware.multiplayersudoku.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Auth {
    companion object {

        private val auth = Firebase.auth

        /**
         * Az aktuálisan bejelentkezett felhasználó. Ha nincs bejelentkezett felhasználó, akkor `null`.
         */
        val currentUser = auth.currentUser

        /**
         * Igaz, ha van bejelentkezett felhasználó, egyébként hamis.
         */
        val isSignedIn= currentUser != null

        /**
         * Regisztrál egy új felhasználót email cím és jelszó alapján.
         * @param email A felhasználó email címe.
         * @param password A felhasználó jelszava.
         * @param onResult Függvény, ami lefut a regisztráció után. Paraméterként kapja a regisztráció sikerességét.
         */
        fun registerWithEmailAndPassword(
            email: String,
            password: String,
            onResult: (Boolean) -> Unit,
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    onResult(it.isSuccessful)
                }
        }

        /**
         * Bejelentkeztet egy felhasználót email cím és jelszó alapján.
         * @param email A felhasználó email címe.
         * @param password A felhasználó jelszava.
         * @param onResult Függvény, ami lefut a bejelentkezés után. Paraméterként kapja a bejelentkezés sikerességét.
         */
        fun signInWithEmailAndPassword(
            email: String,
            password: String,
            onResult: (Boolean) -> Unit,
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    onResult(it.isSuccessful)
                }
        }

        /**
         * Bejelentkeztet egy felhasználót a Google fiókjával.
         */
        fun signInWithGoogle() {
            //TODO Csákinak: Google bejelentkezés
            throw NotImplementedError()
        }

        /**
         * Kijelentkezteti a felhasználót.
         */
        fun signOut() {
            auth.signOut()
        }

    }
}
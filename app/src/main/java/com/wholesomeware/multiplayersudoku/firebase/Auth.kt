package com.wholesomeware.multiplayersudoku.firebase

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wholesomeware.multiplayersudoku.R

class Auth {
    companion object {

        private val auth = Firebase.auth
        private val REQUEST_CODE_GOOGLE_ONE_TAP = 100

        /**
         * Az aktuálisan bejelentkezett felhasználó. Ha nincs bejelentkezett felhasználó, akkor `null`.
         */
        fun getCurrentUser() = auth.currentUser

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
            if (email.isBlank() || password.isBlank()) {
                onResult(false)
                return
            }
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
            if (email.isBlank() || password.isBlank()) {
                onResult(false)
                return
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    onResult(it.isSuccessful)
                }
        }

        /**
         * Bejelentkeztet egy felhasználót a Google fiókjával.
         */
        fun signInWithGoogle(activity: Activity) {
            val oneTapClient = Identity.getSignInClient(activity)
            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(activity.getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()
            oneTapClient.beginSignIn(signInRequest)
                .addOnCompleteListener {
                    try {
                        val result = it.result
                        activity.startIntentSenderForResult(
                            result.pendingIntent.intentSender,
                            REQUEST_CODE_GOOGLE_ONE_TAP,
                            null,
                            0,
                            0,
                            0,
                            null
                        )
                    } catch (e: Exception) {
                        Log.d("Auth", "Error starting Google One Tap: $e")
                    }
                }
        }

        fun onActivityResult(
            activity: Activity,
            requestCode: Int,
            resultCode: Int,
            data: Intent?,
            onResult: (Boolean) -> Unit = {},
        ) {
            if (requestCode != REQUEST_CODE_GOOGLE_ONE_TAP) return

            val oneTapClient = Identity.getSignInClient(activity)

            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                            .addOnCompleteListener {
                                onResult(it.isSuccessful)
                            }
                    }
                    else -> {
                        // Shouldn't happen.
                        Log.d("Auth", "No ID token!")
                        onResult(false)
                    }
                }
            } catch (e: ApiException) {
                Log.d("Auth", "Error getting credential: ${e.statusCode}")
                onResult(false)
            }
        }

        /**
         * Kijelentkezteti a felhasználót.
         */
        fun signOut() {
            auth.signOut()
        }

        fun deleteCurrentUser(onResult: (Boolean) -> Unit) {
            auth.currentUser?.delete()
                ?.addOnCompleteListener {
                    onResult(it.isSuccessful)
                    signOut()
                }
        }

    }
}
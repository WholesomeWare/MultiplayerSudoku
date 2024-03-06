package com.wholesomeware.multiplayersudoku.firebase

import com.wholesomeware.multiplayersudoku.App
import com.wholesomeware.multiplayersudoku.model.Player

class Firestore {
    class Players {
        companion object {

            /**
             * Lekér egy játékost azonosító alapján.
             * @param id A játékos azonosítója. Megegyezik a Firebase uid-vel.
             */
            fun getPlayerById(id: String, onResult: (Player?) -> Unit) {
                App.instance.firestore.collection("players").document(id).get()
                    .addOnSuccessListener {
                        val player = it.toObject(Player::class.java) // Itt lesz az adatból játékor objektum
                            ?.copy(id = it.id) // A játékos objektum id-jét beállítjuk a dokumentum id-jére, mert nem garantált, hogy megegyezik
                        onResult(player)
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }
            }

            /**
             * Beállít egy játékost azonosító alapján. Ha a játékos nem létezik, akkor létrehozza.
             * @param player A beállítandó játékos. Ennek az objektumnak az id-jét használja az azonosításra.
             */
            fun setPlayer(player: Player, onResult: (Boolean) -> Unit = {}) {
                App.instance.firestore.collection("players").document(player.id).set(player)
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

            /**
             * Töröl egy játékost azonosító alapján. A Google és EU szabályai szerint
             * lehetőséget kell biztosítani a felhasználóknak, hogy törölhessék adataikat.
             * @param id A játékos azonosítója. Megegyezik a Firebase uid-vel.
             */
            fun deletePlayerById(id: String, onResult: (Boolean) -> Unit = {}) {
                App.instance.firestore.collection("players").document(id).delete()
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

        }
    }
    class Rooms {
        companion object {

        }
    }
}
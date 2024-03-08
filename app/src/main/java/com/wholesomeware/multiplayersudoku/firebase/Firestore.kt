package com.wholesomeware.multiplayersudoku.firebase

import com.google.firebase.firestore.FieldPath
import com.wholesomeware.multiplayersudoku.App
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.model.Room

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

            fun getPlayersByIds(ids: List<String>, onResult: (List<Player>) -> Unit) {
                //TODO: Játékosok lekérése azonosítók alapján. Ez ahhoz kell,
                // hogy a velünk egy szobában lévő játékosokat lekérjük.
                // Firebase doksi segítségként:
                // https://firebase.google.com/docs/firestore/query-data/queries?authuser=0#kotlin+ktx_1

                App.instance.firestore.collection("players")
                    .whereIn(FieldPath.documentId(), ids).get()
                    .addOnSuccessListener { querySnapshot ->
                        val players = mutableListOf<Player>()

                        for (document in querySnapshot) {
                            val player = document.toObject(Player::class.java)
                                .copy(id = document.id)
                            players.add(player)
                        }
                        onResult(players)
                    }
                    .addOnFailureListener {
                        onResult(emptyList())
                    }
            }

            /**
             * Beállít egy játékost azonosító alapján. Ha a játékos nem létezik, akkor létrehozza.
             * @param player A beállítandó játékos. Ennek az objektumnak az id-jét használja az azonosításra.
             */
            fun setPlayer(player: Player, onResult: (Boolean) -> Unit) {
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
            fun deletePlayerById(id: String, onResult: (Boolean) -> Unit) {
                App.instance.firestore.collection("players").document(id).delete()
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

        }
    }
    class Rooms {
        companion object {

            /**
             * Lekér egy szobát azonosító alapján.
             * @param id A szoba azonosítója. Megegyezik a meghívó kódjával.
             */
            fun getRoomById(id: String, onResult: (Room?) -> Unit) {
                App.instance.firestore.collection("rooms").document(id).get()
                    .addOnSuccessListener {
                        val room = it.toObject(Room::class.java)
                            ?.copy(id = it.id)
                        onResult(room)
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }
            }

            fun setRoom(room: Room, onResult: (Boolean) -> Unit) {
                //TODO: Szoba beállítása. Ezt lehet csinálni a Players.setPlayer mintájára.
                // Ezzel fogjuk frissíteni a sudoku táblát és a játékosok listáját is.

                App.instance.firestore.collection("rooms").document(room.id).set(room)
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

            fun createRoom(onRoomCreated: (Room) -> Unit) {
                val ownerId = Auth.getCurrentUser()?.uid ?: return

                //TODO: Szoba létrehozása. Itt kellene generálnunk egy rövid meghívó kódot is,
                // aztán amint sikerül fölrakni az új szobát adatbázisba, visszaadjuk a felhasználónak.
            }

        }
    }
}
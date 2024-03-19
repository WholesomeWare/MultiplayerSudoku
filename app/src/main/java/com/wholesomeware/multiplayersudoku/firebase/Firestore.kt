package com.wholesomeware.multiplayersudoku.firebase

import android.content.Context
import android.os.Build
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.wholesomeware.multiplayersudoku.App
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.model.SudokuPosition

class Firestore {
    class Players {
        companion object {

            private val playerListeners = mutableListOf<ListenerRegistration>()

            fun addPlayerListener(id: String, listener: (Player?) -> Unit): ListenerRegistration {
                val registration = App.instance.firestore
                    .collection("players")
                    .document(id)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            listener(null)
                            return@addSnapshotListener
                        }

                        val player = snapshot?.toObject(Player::class.java)
                            ?.copy(id = snapshot.id)
                        listener(player)
                    }
                playerListeners.add(registration)
                return registration
            }

            fun removePlayerListener(registration: ListenerRegistration) {
                registration.remove()
                playerListeners.remove(registration)
            }

            fun removeAllPlayerListeners() {
                playerListeners.forEach { it.remove() }
                playerListeners.clear()
            }

            /**
             * Lekér egy játékost azonosító alapján.
             * @param id A játékos azonosítója. Megegyezik a Firebase uid-vel.
             */
            fun getPlayerById(id: String?, onResult: (Player?) -> Unit) {
                if (id.isNullOrBlank()) {
                    onResult(null)
                    return
                }

                App.instance.firestore.collection("players").document(id).get()
                    .addOnSuccessListener {
                        val player =
                            it.toObject(Player::class.java) // Itt lesz az adatból játékor objektum
                                ?.copy(id = it.id) // A játékos objektum id-jét beállítjuk a dokumentum id-jére, mert nem garantált, hogy megegyezik
                        onResult(player)
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }
            }

            fun getPlayersByIds(ids: List<String>, onResult: (List<Player>) -> Unit) {
                if (ids.isEmpty()) {
                    onResult(emptyList())
                    return
                }

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

            fun renamePlayer(id: String, newName: String, onResult: (Boolean) -> Unit) {
                App.instance.firestore.collection("players").document(id)
                    .update("name", newName)
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

            fun setPlayerColor(id: String, color: Int, onResult: (Boolean) -> Unit) {
                App.instance.firestore.collection("players").document(id)
                    .update("color", color)
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

            fun selectCell(playerId: String, position: SudokuPosition?, onResult: (Boolean) -> Unit) {
                App.instance.firestore.collection("players").document(playerId)
                    .update("currentPosition", position)
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

        }
    }

    class Rooms {
        companion object {

            private val roomListeners = mutableListOf<ListenerRegistration>()

            fun addRoomListener(id: String, listener: (Room?) -> Unit): ListenerRegistration {
                val registration = App.instance.firestore
                    .collection("rooms")
                    .document(id)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            listener(null)
                            return@addSnapshotListener
                        }

                        val room = snapshot?.toObject(Room::class.java)
                            ?.copy(id = snapshot.id)
                        listener(room)
                    }
                roomListeners.add(registration)
                return registration
            }

            fun removeRoomListener(registration: ListenerRegistration) {
                registration.remove()
                roomListeners.remove(registration)
            }

            fun removeAllRoomListeners() {
                roomListeners.forEach { it.remove() }
                roomListeners.clear()
            }

            /**
             * Lekér egy szobát azonosító alapján.
             * @param id A szoba azonosítója. Megegyezik a meghívó kódjával.
             */
            fun getRoomById(context: Context, id: String, onResult: (Room?) -> Unit) {
                App.instance.firestore.collection("rooms").document(id).get()
                    .addOnSuccessListener {
                        val gameVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
                        } else {
                            context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
                        }
                        if (!it.exists() || it.getLong("gameVersion") != gameVersion) {
                            onResult(null)
                            return@addOnSuccessListener
                        }
                        val room = it.toObject(Room::class.java)
                            ?.copy(id = it.id)
                        onResult(room)
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }
            }

            /**
             * Beállít egy szobát azonosító alapján. Ha a szoba nem létezik, akkor létrehozza.
             */
            fun setRoom(room: Room, onResult: (Boolean) -> Unit) {
                App.instance.firestore.collection("rooms")
                    .whereEqualTo("ownerId", room.ownerId)
                    .whereNotEqualTo("id", room.id)
                    .get()
                    .addOnCompleteListener { taskDeleteExisting ->
                        App.instance.firestore.runTransaction { transaction ->
                            for (document in taskDeleteExisting.result) {
                                transaction.delete(document.reference)
                            }
                            val newRoomRef = App.instance.firestore.collection("rooms").document(room.id)
                            transaction.set(newRoomRef, room)
                        }
                            .addOnCompleteListener { taskTransactionComplete ->
                                onResult(taskTransactionComplete.isSuccessful)
                            }
                    }
            }

            fun deleteRoomById(id: String, onResult: (Boolean) -> Unit) {
                App.instance.firestore.collection("rooms").document(id).delete()
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

            fun joinRoom(id: String, onResult: (Boolean) -> Unit) {
                if (Auth.getCurrentUser()?.uid.isNullOrBlank() || id.isBlank()) {
                    onResult(false)
                    return
                }

                Players.selectCell(Auth.getCurrentUser()!!.uid, null) {}

                App.instance.firestore.collection("rooms").document(id)
                    .update("players", FieldValue.arrayUnion(Auth.getCurrentUser()!!.uid))
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

            fun leaveRoom(
                context: Context,
                id: String,
                deleteRoomIfEmpty: Boolean = true,
                onResult: (Boolean) -> Unit
            ) {
                if (Auth.getCurrentUser()?.uid.isNullOrBlank() || id.isBlank()) {
                    onResult(false)
                    return
                }

                Players.selectCell(Auth.getCurrentUser()!!.uid, null) {}

                App.instance.firestore.collection("rooms").document(id)
                    .update("players", FieldValue.arrayRemove(Auth.getCurrentUser()!!.uid))
                    .addOnCompleteListener { leaveTask ->
                        onResult(leaveTask.isSuccessful)
                        if (deleteRoomIfEmpty) {
                            getRoomById(context, id) { room ->
                                if (room?.players?.isEmpty() == true) {
                                    deleteRoomById(id) {}
                                }
                            }
                        }
                    }
            }

            fun kickPlayer(roomId: String, playerId: String, onResult: (Boolean) -> Unit) {
                App.instance.firestore.collection("rooms").document(roomId)
                    .update("players", FieldValue.arrayRemove(playerId))
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }
            }

        }
    }
}
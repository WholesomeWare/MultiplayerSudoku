package com.wholesomeware.multiplayersudoku.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.model.SerializableSudoku
import com.wholesomeware.multiplayersudoku.model.SudokuPosition
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku

class RTDB {
    companion object {

        private val db = Firebase.database
        private val root = db.reference
        private val rooms = root.child("rooms")

    }

    class Rooms {
        companion object {

            private val listeners = mutableMapOf<String, ChildEventListener>()

            fun addRoomListener(roomId: String, onRoomChanged: (DataSnapshot?) -> Unit): ChildEventListener {
                if (listeners.containsKey(roomId)) {
                    throw IllegalArgumentException("Listener already exists for room $roomId")
                }

                val listener = rooms
                    .child(roomId)
                    .addChildEventListener(
                        object: ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                getRoomById(roomId) {
                                    onRoomChanged(it)
                                }
                            }
                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                getRoomById(roomId) {
                                    onRoomChanged(it)
                                }
                            }
                            override fun onChildRemoved(snapshot: DataSnapshot) {
                                getRoomById(roomId) {
                                    onRoomChanged(it)
                                }
                            }
                            override fun onChildMoved(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                getRoomById(roomId) {
                                    onRoomChanged(it)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                onRoomChanged(null)
                            }
                        }
                    )
                listeners[roomId] = listener
                return listener
            }

            fun removeRoomListener(roomId: String, listener: ChildEventListener) {
                rooms
                    .child(roomId)
                    .removeEventListener(listener)
                listeners.remove(roomId)
            }

            fun removeAllRoomListeners() {
                listeners.forEach { (roomId, listener) ->
                    rooms
                        .child(roomId)
                        .removeEventListener(listener)
                }
                listeners.clear()
            }

            fun getRoomById(roomId: String, onResult: (DataSnapshot?) -> Unit) {
                rooms
                    .child(roomId)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            onResult(it.result)
                        } else {
                            onResult(null)
                        }
                    }
            }

            fun deleteRoomById(roomId: String, onResult: (Boolean) -> Unit = {}) {
                rooms
                    .child(roomId)
                    .removeValue()
                    .addOnCompleteListener { onResult(it.isSuccessful) }
            }

            fun updateSudoku(
                roomId: String,
                sudoku: Sudoku,
                onResult: (Boolean) -> Unit,
            ) {
                if (roomId.isBlank()) {
                    onResult(false)
                    return
                }
                val room = rooms.child(roomId)
                val serializableSudoku = SerializableSudoku.fromSudoku(sudoku)
                room
                    .child("sudoku")
                    .setValue(serializableSudoku)
                    .addOnCompleteListener { taskUpdateSudoku ->
                        onResult(taskUpdateSudoku.isSuccessful)
                    }
            }

        }
    }

    class Players {
        companion object {

            fun selectCell(
                roomId: String,
                playerId: String,
                position: SudokuPosition? = null,
                onResult: (Boolean) -> Unit = {},
            ) {
                if (roomId.isBlank() || playerId.isBlank()) {
                    onResult(false)
                    return
                }
                val room = rooms.child(roomId)
                room
                    .child("players")
                    .child(playerId)
                    .child("selectedCell")
                    .setValue(position?.toString())
                    .addOnCompleteListener { taskSelectCell ->
                        onResult(taskSelectCell.isSuccessful)
                    }
            }

        }
    }
}
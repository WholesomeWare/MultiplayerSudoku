package com.wholesomeware.multiplayersudoku

import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.model.Room

class RoomManager {
    companion object {

        /**
         * Ez a függvény létrehoz egy új szobát a jelenleg bejelentkezett felhasználóval.
         * @param onResult Függvény, ami lefut a szoba létrehozása után.
         * Paraméterként kapja a szoba meghívó kódját sikeres létrehozás esetén.
         */
        fun createRoom(onResult: (String?) -> Unit) {
            if (Auth.getCurrentUser() == null) {
                onResult(null)
                return
            }

            val inviteCode = generateInviteCode()
            val room = Room(
                id = inviteCode,
                ownerId = Auth.getCurrentUser()!!.uid,
                startTime = System.currentTimeMillis(),
            )
            Firestore.Rooms.setRoom(room) {
                onResult(if (it) inviteCode else null)
            }
        }

        /**
         * Ez a függvény egy random meghívó kódot generál a szobákhoz.
         * @param length A meghívó kód hossza.
         * @return A generált meghívó kód.
         */
        private fun generateInviteCode(
            length: Int = 4,
        ): String {
            val chars: List<Char> = ('A'..'Z') + ('0'..'9') - "BIOSZ".toSet()
            return (1..length)
                .map { chars.random() }
                .joinToString("")
        }

    }
}
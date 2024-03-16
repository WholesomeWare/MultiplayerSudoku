package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ListenerRegistration
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku
import com.wholesomeware.multiplayersudoku.sudoku.SudokuGenerator
import com.wholesomeware.multiplayersudoku.ui.components.SudokuDisplay
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class GameActivity : ComponentActivity() {
    private var room by mutableStateOf(Room())
    private var roomListenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen()
        }

        initializeRoom()
    }

    override fun onDestroy() {
        roomListenerRegistration?.let { Firestore.Rooms.removeRoomListener(it) }
        super.onDestroy()
    }

    private fun initializeRoom() {
        val roomId = intent.getStringExtra(LobbyActivity.EXTRA_ROOM_ID)
        if (roomId == null) {
            Toast.makeText(this, "Nem található a szoba", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Firestore.Rooms.joinRoom(roomId) { isJoinSuccessful ->
            if (!isJoinSuccessful) {
                Toast.makeText(this, "Nem sikerült csatlakozni a szobához", Toast.LENGTH_SHORT)
                    .show()
                finish()
                return@joinRoom
            }

            Firestore.Rooms.getRoomById(roomId) {
                room = it ?: return@getRoomById
            }
            roomListenerRegistration = Firestore.Rooms.addRoomListener(roomId) {
                room = it ?: return@addRoomListener

                // Kirúgás észlelése
                if (!room.players.contains(Auth.getCurrentUser()?.uid)) {
                    finish()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun GameScreen() {
        MultiplayerSudokuTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SudokuDisplay(
                    modifier = Modifier
                        .padding(16.dp),
                    sudoku = room.sudoku.toSudoku(),
                    onCellClick = { row, column ->
                        Log.d("Sudoku", "Cell clicked: $row, $column")
                    },
                    cellBorderColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

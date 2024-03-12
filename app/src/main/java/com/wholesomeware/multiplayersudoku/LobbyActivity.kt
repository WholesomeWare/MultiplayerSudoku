package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class LobbyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LobbyScreen()
        }
    }

    @Preview
    @Composable
    private fun LobbyScreen() {
        MultiplayerSudokuTheme {
            var room by remember { mutableStateOf(Room()) }
            val isOwner by remember(room) {
                mutableStateOf(room.ownerId == Auth.getCurrentUser()?.uid)
            }
            var players by remember { mutableStateOf(emptyList<Player>()) }
            var selectedDifficulty by remember { mutableStateOf(Sudoku.Difficulty.EASY) }

            // Ez az effect akkor fut le, amikor a szobában valami megváltozik.
            LaunchedEffect(room) {
                // Lekérjük a játékosokat id alapján, mert a `room` csak az id-jüket tárolja
                Firestore.Players.getPlayersByIds(room.players) {
                    players = it
                }
            }

            //TODO: játékos lista, kezdés gomb és nehézség állítás a tulajnak

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    Text(
                        text = if (isOwner) "Tulaj vagyok"
                        else "Vendég vagyok"
                    )
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        // Ha meg akarjuk jeleníteni az összes játékost akkor egy ciklussal simán végigmehetünk rajtuk.
                        players.forEach { player ->
                            // Ide kell megadni, hogy hogyan nézzen ki 1 játékos.
                            // Az itteni UI elemek fognak ismétlődni játékosonként.
                        }
                    }
                    // Játékosok alá egy meghívás gomb, ami intent-el megosztja a meghívó kódot
                    // minden appnak, ami tud szöveget fogadni.
                }
            }
        }
    }
}

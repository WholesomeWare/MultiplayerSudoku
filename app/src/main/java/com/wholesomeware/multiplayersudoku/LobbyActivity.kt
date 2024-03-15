package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    @OptIn(ExperimentalMaterial3Api::class)
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
                    CenterAlignedTopAppBar(
                        title = {
                            Text(text = "X szobája")
                        },
                        navigationIcon = {
                            IconButton(onClick = { /* do something */ }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        },
                    )
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Gray,
                        ),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            text = "Hívd meg a barátaidat, és játszatok együtt",
                            modifier = Modifier.padding(8.dp)
                        )
                    }



                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        // Ha meg akarjuk jeleníteni az összes játékost akkor egy ciklussal simán végigmehetünk rajtuk.
                        players.forEach { player ->
                            Row {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                )
                                Text(text = player.name)
                            }

                            // Ide kell megadni, hogy hogyan nézzen ki 1 játékos.
                            // Az itteni UI elemek fognak ismétlődni játékosonként.
                        }
                    }

                    // Játékosok alá egy meghívás gomb, ami intent-el megosztja a meghívó kódot
                    // minden appnak, ami tud szöveget fogadni.

                    BottomAppBar(

                        actions = {
                                  Text(text = "valami")
                        },
                        floatingActionButton = {
                            ExtendedFloatingActionButton(
                                onClick = { /*TODO*/ },

                            ){
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "Csatlakozás ikon"
                                )
                                Text(text = "Indítás")
                            }

                        }

                    )
                }
            }
        }
    }
}

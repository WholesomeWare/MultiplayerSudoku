package com.wholesomeware.multiplayersudoku

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.wholesomeware.multiplayersudoku.ui.components.PlayerDisplay
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class LobbyActivity : ComponentActivity() {
    companion object {
        const val EXTRA_ROOM_ID = "EXTRA_ROOM_ID"
    }

    private var roomId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LobbyScreen()
        }
        roomId = intent.getStringExtra(EXTRA_ROOM_ID)
        if (roomId == null) {
            Toast.makeText(this, "Nem található a szoba", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Firestore.Rooms.joinRoom(roomId!!) {
            if (!it) {
                Toast.makeText(this, "Nem sikerült csatlakozni a szobához", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        Firestore.Rooms.leaveRoom(roomId!!) {}
        super.onDestroy()
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
            var ownerPlayer by remember { mutableStateOf(Player()) }
            var players by remember { mutableStateOf(emptyList<Player>()) }
            var selectedDifficulty by remember { mutableStateOf(Sudoku.Difficulty.EASY) }

            LaunchedEffect(roomId) {
                if (roomId == null) return@LaunchedEffect
                Firestore.Rooms.getRoomById(roomId!!) {
                    room = it ?: return@getRoomById
                }
            }

            // Ez az effect akkor fut le, amikor a szobában valami megváltozik.
            LaunchedEffect(room) {
                // Lekérjük a játékosokat id alapján, mert a `room` csak az id-jüket tárolja
                Firestore.Players.getPlayersByIds(room.players) {
                    ownerPlayer = it.firstOrNull { player -> player.id == room.ownerId } ?: Player()
                    players = it
                }
            }

            //TODO: játékos lista, kezdés gomb és nehézség állítás a tulajnak

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(text = "${ownerPlayer.name} szobája")
                        },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        },
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            Text(
                                text = "Hívd meg a barátaidat, és játszatok együtt!",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        players.forEach { player ->
                            PlayerDisplay(
                                player = player,
                                adminControlsEnabled = isOwner,
                                onKickRequest = {
                                    //TODO: játékos kirúgása
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clickable {
                                    startActivity(
                                        Intent.createChooser(
                                            Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    room.id,
                                                )
                                            },
                                            "Megosztás"
                                        )
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier.padding(8.dp),
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f),
                                    text = "Játékos meghívása",
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    BottomAppBar(
                        actions = {
                            Text(text = "valami")
                        },
                        floatingActionButton = {
                            ExtendedFloatingActionButton(
                                onClick = { /*TODO*/ },

                                ) {
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

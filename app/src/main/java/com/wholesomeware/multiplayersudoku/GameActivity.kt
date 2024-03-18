package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ListenerRegistration
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.model.SerializableSudoku
import com.wholesomeware.multiplayersudoku.sudoku.SudokuSolver
import com.wholesomeware.multiplayersudoku.sudoku.SudokuSolver.Companion.isDone
import com.wholesomeware.multiplayersudoku.ui.components.PlayerDisplay
import com.wholesomeware.multiplayersudoku.ui.components.ShapedButton
import com.wholesomeware.multiplayersudoku.ui.components.SudokuDisplay
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme
import java.util.Timer
import kotlin.concurrent.timerTask

class GameActivity : ComponentActivity() {
    private var room by mutableStateOf(Room())
    private var roomListenerRegistration: ListenerRegistration? = null
    private var currentTimeMillis by mutableLongStateOf(0L)
    private var timer: Timer? = null

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

    override fun onResume() {
        super.onResume()
        timer = Timer().apply {
            scheduleAtFixedRate(timerTask {
                if (room.startTime > 0) {
                    currentTimeMillis = System.currentTimeMillis() - room.startTime
                }
            }, 1000L, 1000L)
        }
    }

    override fun onPause() {
        timer?.cancel()
        timer = null
        super.onPause()
    }

    private fun initializeRoom() {
        val roomId = intent.getStringExtra(LobbyActivity.EXTRA_ROOM_ID)
        if (roomId == null) {
            Toast.makeText(this, getString(R.string.room_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Firestore.Rooms.joinRoom(roomId) { isJoinSuccessful ->
            if (!isJoinSuccessful) {
                Toast.makeText(this, getString(R.string.join_failed), Toast.LENGTH_SHORT)
                    .show()
                finish()
                return@joinRoom
            }

            Firestore.Rooms.getRoomById(roomId) {
                room = it ?: return@getRoomById
            }
            roomListenerRegistration = Firestore.Rooms.addRoomListener(roomId) { newRoom ->
                // Kirúgás észlelése
                if (newRoom?.players?.contains(Auth.getCurrentUser()?.uid) == false) {
                    roomListenerRegistration?.let { Firestore.Rooms.removeRoomListener(it) }
                    finish()
                    return@addRoomListener
                }

                room = newRoom ?: return@addRoomListener
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun GameScreen() {
        MultiplayerSudokuTheme {
            val numberButtonHeight = remember { 92.dp }
            
            var isExitDialogOpen by remember { mutableStateOf(false) }

            val isOwner by remember(room) { mutableStateOf(room.ownerId == Auth.getCurrentUser()?.uid) }
            var players by remember { mutableStateOf(emptyList<Player>()) }

            var sudoku by remember(room) { mutableStateOf(room.sudoku.toSudoku()) }
            var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
            val isCorrect by remember(sudoku) { mutableStateOf(SudokuSolver.isGridCorrect(sudoku.currentGrid)) }
            val isDone by remember(sudoku) { mutableStateOf(sudoku.isDone()) }

            BackHandler {
                isExitDialogOpen = true
            }

            LaunchedEffect(room) {
                // Lekérjük a játékosokat id alapján, mert a `room` csak az id-jüket tárolja
                Firestore.Players.getPlayersByIds(room.players) {
                    //ownerPlayer = it.firstOrNull { player -> player.id == room.ownerId } ?: Player()
                    players = it
                }
            }

            LaunchedEffect(sudoku) {
                if (room.id.isBlank()) {
                    return@LaunchedEffect
                }
                room = room.copy(sudoku = SerializableSudoku.fromSudoku(sudoku))
                Firestore.Rooms.setRoom(room) {}
            }

            if (isExitDialogOpen) {
                AlertDialog(
                    title = { Text(text = stringResource(id = R.string.sure_exit)) },
                    onDismissRequest = { isExitDialogOpen = false },
                    confirmButton = {
                        ShapedButton(onClick = { Firestore.Rooms.leaveRoom(room.id) {} }) {
                            Text(text = stringResource(id = R.string.yes))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { isExitDialogOpen = false }) {
                            Text(text = stringResource(id = R.string.no))
                        }
                    },
                )
            }

            if (isDone) {
                AlertDialog(
                    title = {},
                    onDismissRequest = {},
                    confirmButton = {
                        ShapedButton(onClick = { Firestore.Rooms.leaveRoom(room.id) {} }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    },
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "${currentTimeMillis / 1000 / 60}:" +
                                        (currentTimeMillis / 1000 % 60).toString().padStart(2, '0')
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { isExitDialogOpen = true }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                )
                            }
                        },
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        players.forEach { player ->
                            PlayerDisplay(
                                modifier = Modifier.padding(8.dp),
                                player = player,
                                adminControlsEnabled = isOwner,
                                onKickRequest = {
                                    Firestore.Rooms.kickPlayer(room.id, player.id) {}
                                },
                                isMini = true,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        SudokuDisplay(
                            modifier = Modifier
                                .padding(16.dp),
                            sudoku = room.sudoku.toSudoku(),
                            onCellClick = { row, column ->
                                selectedCell = if (selectedCell == row to column) {
                                    null
                                } else {
                                    row to column
                                }
                            },
                            selectedCells = listOfNotNull(selectedCell),
                            cellBorderColor = if (isCorrect) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 1) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "1")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 2) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "2")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 3) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "3")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 4) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "4")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 5) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "5")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 6) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "6")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 7) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "7")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 8) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "8")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 9) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Text(text = "9")
                        }
                        FloatingActionButton(
                            onClick = { sudoku = sudoku.setCellIfWritable(selectedCell, 0) },
                            modifier = Modifier
                                .weight(1f)
                                .height(numberButtonHeight)
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.Backspace,
                                contentDescription = null
                            )
                        }

                    }
                }

            }
        }
    }
}

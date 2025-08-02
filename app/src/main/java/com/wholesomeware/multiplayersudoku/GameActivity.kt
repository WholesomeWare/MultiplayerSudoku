package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ListenerRegistration
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.firebase.RTDB
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.model.SerializableSudoku
import com.wholesomeware.multiplayersudoku.model.SudokuPosition
import com.wholesomeware.multiplayersudoku.model.SudokuPosition.Companion.toSudokuPosition
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku
import com.wholesomeware.multiplayersudoku.sudoku.SudokuSolver
import com.wholesomeware.multiplayersudoku.sudoku.SudokuSolver.Companion.isDone
import com.wholesomeware.multiplayersudoku.ui.components.BlockableFAB
import com.wholesomeware.multiplayersudoku.ui.components.PlayerDisplay
import com.wholesomeware.multiplayersudoku.ui.components.ShapedButton
import com.wholesomeware.multiplayersudoku.ui.components.sudoku.SudokuDisplay
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
        enableEdgeToEdge()
        setContent {
            GameScreen()
        }

        initializeRoom()
    }

    override fun onDestroy() {
        roomListenerRegistration?.let { Firestore.Rooms.removeRoomListener(it) }
        RTDB.Rooms.removeAllRoomListeners()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        timer = Timer().apply {
            schedule(timerTask {
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

            Firestore.Rooms.getRoomById(this, roomId) {
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
            val sudokuDisplayFocusRequester = remember { FocusRequester() }

            val numberButtonHeight = remember { 72.dp }

            var isExitDialogOpen by remember { mutableStateOf(false) }

            val isOwner by remember(room) { mutableStateOf(room.ownerId == Auth.getCurrentUser()?.uid) }
            var players by remember { mutableStateOf(emptyList<Player>()) }
            var playerPositions by remember {
                mutableStateOf(players.associateWith { SudokuPosition(0, 0) })
            }

            var sudoku by remember { mutableStateOf(Sudoku.EMPTY) }
            var playerSelectedCell by remember { mutableStateOf<SudokuPosition?>(null) }

            BackHandler {
                isExitDialogOpen = true
            }

            LaunchedEffect(room) {
                // Lekérjük a játékosokat id alapján, mert a `room` csak az id-jüket tárolja
                Firestore.Players.getPlayersByIds(room.players) { newPlayers ->
                    //ownerPlayer = it.firstOrNull { player -> player.id == room.ownerId } ?: Player()
                    players = newPlayers

                    RTDB.Rooms.removeAllRoomListeners()
                    RTDB.Rooms.addRoomListener(room.id) { roomSnapshot ->
                        // Player positions
                        playerPositions = players.mapNotNull { player ->
                            val playerSnapshot = roomSnapshot?.child("players")?.child(player.id)
                            if (playerSnapshot == null || !playerSnapshot.exists()) null
                            else player to SudokuPosition.fromString(
                                playerSnapshot.child("selectedCell").getValue(String::class.java)
                            )
                        }.toMap()
                        // Sudoku
                        val serializableSudoku =
                            roomSnapshot?.child("sudoku")?.getValue(SerializableSudoku::class.java)
                        sudoku = serializableSudoku?.toSudoku() ?: sudoku
                    }
                }
            }

            LaunchedEffect(sudoku) {
                if (room.id.isBlank() || room.endTime > 0) {
                    return@LaunchedEffect
                }
                RTDB.Rooms.updateSudoku(room.id, sudoku) {}
                if (sudoku.isDone()) {
                    Firestore.Rooms.setRoom(
                        room.copy(
                            endTime = if (sudoku.isDone()) System.currentTimeMillis() else 0L
                        )
                    ) {}
                }
            }

            LaunchedEffect(playerSelectedCell) {
                if (Auth.getCurrentUser() == null) return@LaunchedEffect

                RTDB.Players.selectCell(room.id, Auth.getCurrentUser()!!.uid, playerSelectedCell)
            }

            if (isExitDialogOpen) {
                AlertDialog(
                    title = { Text(text = stringResource(id = R.string.sure_exit)) },
                    onDismissRequest = { isExitDialogOpen = false },
                    confirmButton = {
                        ShapedButton(onClick = { Firestore.Rooms.leaveRoom(this, room.id) {} }) {
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

            if (sudoku.isDone()) {
                val solveDuration by remember(room) {
                    mutableLongStateOf(room.endTime - room.startTime)
                }

                AlertDialog(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                        )
                    },
                    title = { Text(text = stringResource(id = R.string.congratulations)) },
                    text = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(imageVector = Icons.Default.Timer, contentDescription = null)
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "${solveDuration / 1000 / 60}:" +
                                            (solveDuration / 1000 % 60).toString().padStart(2, '0')
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(imageVector = Icons.Default.People, contentDescription = null)
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "${players.size}",
                                )
                            }
                        }
                    },
                    onDismissRequest = {},
                    confirmButton = {
                        ShapedButton(onClick = { Firestore.Rooms.leaveRoom(this, room.id) {} }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    },
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
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
                                modifier = Modifier.padding(horizontal = 8.dp),
                                player = player,
                                adminControlsEnabled = isOwner,
                                onKickRequest = {
                                    Firestore.Rooms.kickPlayer(room.id, player.id) {}
                                },
                                isMini = true,
                            )
                        }
                        AssistChip(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            onClick = { RoomManager.showInviteSheet(this@GameActivity, room.id) },
                            label = { Text(text = stringResource(id = R.string.invite_player)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                )
                            },
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(max = 720.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(max = 480.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            SudokuDisplay(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .aspectRatio(1f)
                                    .focusable()
                                    .focusRequester(sudokuDisplayFocusRequester)
                                    .onKeyEvent { e ->
                                        Log.d("GameActivity", "Key: ${e.key}")

                                        if (e.type != KeyEventType.KeyUp) return@onKeyEvent false

                                        when (e.key) {
                                            Key.Escape -> {
                                                if (playerSelectedCell != null) playerSelectedCell =
                                                    null
                                                else return@onKeyEvent false
                                            }

                                            Key.NumPad1 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 1)
                                            }

                                            Key.NumPad2 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 2)
                                            }

                                            Key.NumPad3 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 3)
                                            }

                                            Key.NumPad4 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 4)
                                            }

                                            Key.NumPad5 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 5)
                                            }

                                            Key.NumPad6 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 6)
                                            }

                                            Key.NumPad7 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 7)
                                            }

                                            Key.NumPad8 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 8)
                                            }

                                            Key.NumPad9 -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 9)
                                            }

                                            Key.Backspace -> {
                                                sudoku =
                                                    sudoku.setCellIfWritable(playerSelectedCell, 0)
                                            }

                                            else -> return@onKeyEvent false
                                        }
                                        true
                                    },
                                sudoku = sudoku,
                                onCellClick = { row, column ->
                                    playerSelectedCell =
                                        if (playerSelectedCell?.equals(row to column) == true) {
                                            null
                                        } else {
                                            sudokuDisplayFocusRequester.requestFocus()
                                            (row to column).toSudokuPosition()
                                        }
                                },
                                playerPositions = playerPositions,
                                cellBorderColor = if (SudokuSolver.isGridCorrect(sudoku.currentGrid)) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error,
                            )
                        }

                        Column(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .widthIn(max = 480.dp)
                                .align(Alignment.End),
                        ) {
                            Row {
                                BlockableFAB(
                                    enabled = sudoku.count(1) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 1)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "1")
                                }
                                BlockableFAB(
                                    enabled = sudoku.count(2) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 2)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "2")
                                }
                                BlockableFAB(
                                    enabled = sudoku.count(3) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 3)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "3")
                                }
                                BlockableFAB(
                                    enabled = sudoku.count(4) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 4)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "4")
                                }
                                BlockableFAB(
                                    enabled = sudoku.count(5) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 5)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "5")
                                }
                            }

                            Row {
                                BlockableFAB(
                                    enabled = sudoku.count(6) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 6)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "6")
                                }
                                BlockableFAB(
                                    enabled = sudoku.count(7) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 7)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "7")
                                }
                                BlockableFAB(
                                    enabled = sudoku.count(8) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 8)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "8")
                                }
                                BlockableFAB(
                                    enabled = sudoku.count(9) < 9,
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 9)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
                                ) {
                                    Text(text = "9")
                                }
                                BlockableFAB(
                                    onClick = {
                                        sudoku = sudoku.setCellIfWritable(playerSelectedCell, 0)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(numberButtonHeight)
                                        .padding(4.dp),
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
    }
}

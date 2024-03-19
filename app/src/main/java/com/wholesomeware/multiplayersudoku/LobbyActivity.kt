package com.wholesomeware.multiplayersudoku

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ListenerRegistration
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.model.SerializableSudoku
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku
import com.wholesomeware.multiplayersudoku.sudoku.SudokuGenerator
import com.wholesomeware.multiplayersudoku.ui.components.BlockableExtendedFAB
import com.wholesomeware.multiplayersudoku.ui.components.FullscreenLoadingIndicator
import com.wholesomeware.multiplayersudoku.ui.components.PlayerDisplay
import com.wholesomeware.multiplayersudoku.ui.components.ShapedButton
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LobbyActivity : ComponentActivity() {
    companion object {
        const val EXTRA_ROOM_ID = "EXTRA_ROOM_ID"
    }

    private var room by mutableStateOf(Room())
    private var roomListenerRegistration: ListenerRegistration? = null
    private var isLoading by mutableStateOf(true)

    private var isForeground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LobbyScreen()
        }

        initializeRoom()
    }

    override fun onDestroy() {
        roomListenerRegistration?.let { Firestore.Rooms.removeRoomListener(it) }
        Firestore.Rooms.leaveRoom(this, room.id) {}
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
    }

    override fun onPause() {
        isForeground = false
        super.onPause()
    }

    private fun openGameActivity() {
        if (!isForeground) {
            return
        }

        startActivity(
            Intent(
                this@LobbyActivity,
                GameActivity::class.java
            )
                .putExtra(EXTRA_ROOM_ID, room.id)
        )
        isLoading = false
    }

    private fun initializeRoom() {
        val roomId = intent.getStringExtra(EXTRA_ROOM_ID)
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

                if (room.isStarted) {
                    openGameActivity()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun LobbyScreen() {
        MultiplayerSudokuTheme {
            val coroutineScope = rememberCoroutineScope()

            var isExitDialogOpen by remember { mutableStateOf(false) }

            val isOwner by remember(room) {
                mutableStateOf(room.ownerId == Auth.getCurrentUser()?.uid)
            }
            var ownerPlayer by remember { mutableStateOf(Player()) }
            var players by remember { mutableStateOf(emptyList<Player>()) }

            var selectedDifficulty by remember(room) {
                mutableStateOf(Sudoku.Difficulty.entries[room.difficultyId])
            }
            var isDifficultySelectorOpen by remember { mutableStateOf(false) }

            BackHandler {
                isExitDialogOpen = true
            }

            // Ez az effect akkor fut le, amikor a szobában valami megváltozik.
            LaunchedEffect(room) {
                // Kirúgás észlelése
                if (!isLoading && !room.players.contains(Auth.getCurrentUser()?.uid)) {
                    Toast.makeText(
                        this@LobbyActivity,
                        getString(R.string.kicked_out),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                }

                // Lekérjük a játékosokat id alapján, mert a `room` csak az id-jüket tárolja
                Firestore.Players.getPlayersByIds(room.players) {
                    ownerPlayer = it.firstOrNull { player -> player.id == room.ownerId } ?: Player()
                    players = it
                    isLoading = false
                }
            }

            FullscreenLoadingIndicator(isLoading = isLoading)

            if (isExitDialogOpen) {
                AlertDialog(
                    title = { Text(text = stringResource(id = R.string.sure_exit)) },
                    onDismissRequest = { isExitDialogOpen = false },
                    confirmButton = {
                        ShapedButton(onClick = { finish() }) {
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

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(
                                    id = R.string.room_title,
                                    ownerPlayer.name
                                )
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
                                text = stringResource(id = R.string.room_description),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        players.forEach { player ->
                            PlayerDisplay(
                                player = player,
                                adminControlsEnabled = isOwner,
                                onKickRequest = {
                                    Firestore.Rooms.kickPlayer(room.id, player.id) {}
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
                                            getString(R.string.share)
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
                                    text = stringResource(id = R.string.invite_player),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    BottomAppBar(
                        actions = {
                            ExposedDropdownMenuBox(
                                expanded = isDifficultySelectorOpen,
                                onExpandedChange = {
                                    isDifficultySelectorOpen = it
                                },
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .width(200.dp)
                                        .padding(horizontal = 8.dp),
                                    enabled = isOwner,
                                    label = { Text(text = stringResource(id = R.string.difficulty)) },
                                    value = stringResource(id = selectedDifficulty.stringResourceId),
                                    onValueChange = {},
                                    maxLines = 1,
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (isDifficultySelectorOpen) Icons.Default.ArrowDropUp
                                            else Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                        )
                                    },
                                )
                                ExposedDropdownMenu(
                                    expanded = isDifficultySelectorOpen && isOwner,
                                    onDismissRequest = { isDifficultySelectorOpen = false },
                                ) {
                                    Sudoku.Difficulty.entries.forEach { difficulty ->
                                        DropdownMenuItem(
                                            onClick = {
                                                selectedDifficulty = difficulty
                                                isDifficultySelectorOpen = false
                                                Firestore.Rooms.setRoom(
                                                    room.copy(difficultyId = difficulty.ordinal)
                                                ) {}
                                            },
                                            text = {
                                                Text(text = stringResource(id = difficulty.stringResourceId))
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        floatingActionButton = {
                            BlockableExtendedFAB(
                                enabled = isOwner,
                                onClick = {
                                    isLoading = true
                                    coroutineScope.launch(Dispatchers.Default) {
                                        val sudoku = SudokuGenerator.create(selectedDifficulty)
                                        Firestore.Rooms.setRoom(
                                            room.copy(
                                                isStarted = true,
                                                sudoku = SerializableSudoku.fromSudoku(sudoku),
                                                startTime = System.currentTimeMillis(),
                                            )
                                        ) {
                                            if (!it) {
                                                runOnUiThread {
                                                    Toast.makeText(
                                                        this@LobbyActivity,
                                                        getString(R.string.game_error),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    isLoading = false
                                                }
                                            }
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                    )
                                },
                                text = { Text(text = stringResource(id = R.string.start)) },
                            )
                        }
                    )
                }
            }
        }
    }
}

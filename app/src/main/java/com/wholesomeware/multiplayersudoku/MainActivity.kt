package com.wholesomeware.multiplayersudoku

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.csakitheone.wholesomeware_brand.WholesomeWare
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.ui.components.FullscreenLoadingIndicator
import com.wholesomeware.multiplayersudoku.ui.components.ShapedButton
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme
import com.wholesomeware.multiplayersudoku.ui.theme.playerColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }

        openLoginIfNeeded()
    }

    private fun openLoginIfNeeded() {
        if (Auth.getCurrentUser() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun MainScreen() {
        MultiplayerSudokuTheme {
            var player by remember { mutableStateOf<Player?>(null) }
            var isLoading by remember { mutableStateOf(false) }

            var isMenuOpen by remember { mutableStateOf(false) }
            var isEditNicknameDialogOpen by remember { mutableStateOf(false) }
            var isRemoveAccountDialogOpen by remember { mutableStateOf(false) }

            var selectedColor by remember(player) {
                mutableStateOf(
                    Color(
                        player?.color ?: playerColors.first().toArgb()
                    )
                )
            }

            var inviteCode by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(isEditNicknameDialogOpen) {
                Firestore.Players.getPlayerById(Auth.getCurrentUser()?.uid) {
                    if (it == null) {
                        val defaultNewPlayer = Player(
                            id = Auth.getCurrentUser()!!.uid,
                            name = Auth.getCurrentUser()!!.displayName ?: "",
                        )
                        Firestore.Players.setPlayer(defaultNewPlayer) {}
                        player = defaultNewPlayer
                        return@getPlayerById
                    }

                    player = it
                }
            }

            FullscreenLoadingIndicator(isLoading = isLoading)

            if (isEditNicknameDialogOpen) {
                AlertDialog(
                    title = { Text(text = stringResource(id = R.string.edit_nickname)) },
                    text = {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = player?.name ?: "",
                            onValueChange = {
                                player = player?.copy(name = it)
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    isLoading = true
                                    Firestore.Players.setPlayer(player!!) { isSaved ->
                                        if (isSaved) {
                                            isEditNicknameDialogOpen = false
                                            isLoading = false
                                        } else {
                                            Firestore.Players.getPlayerById(Auth.getCurrentUser()?.uid) {
                                                player = it
                                                isLoading = false
                                            }
                                        }
                                    }
                                }
                            ),
                        )
                    },
                    onDismissRequest = { isEditNicknameDialogOpen = false },
                    confirmButton = {
                        ShapedButton(
                            onClick = {
                                isLoading = true
                                Firestore.Players.setPlayer(player!!) { isSaved ->
                                    if (isSaved) {
                                        isEditNicknameDialogOpen = false
                                        isLoading = false
                                    } else {
                                        Firestore.Players.getPlayerById(Auth.getCurrentUser()?.uid) {
                                            player = it
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.save))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { isEditNicknameDialogOpen = false },
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            if (isRemoveAccountDialogOpen) {
                AlertDialog(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                        )
                    },
                    title = { Text(text = stringResource(id = R.string.delete_account)) },
                    text = { Text(text = stringResource(id = R.string.sure_delete_account)) },
                    onDismissRequest = { isRemoveAccountDialogOpen = false },
                    confirmButton = {
                        ShapedButton(
                            onClick = {
                                isLoading = true
                                val currentUser = Auth.getCurrentUser()
                                if (currentUser != null) {
                                    Firestore.Players.deletePlayerById(currentUser.uid) { isSuccess ->
                                        Auth.deleteCurrentUser {
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    LoginActivity::class.java
                                                )
                                            )
                                            finish()
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                            )
                        ) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { isRemoveAccountDialogOpen = false },
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(text = stringResource(id = R.string.app_name))
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .width(48.dp)
                                    .aspectRatio(1f),
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                            )
                        },
                        actions = {
                            IconButton(onClick = { isMenuOpen = true }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                )
                                DropdownMenu(
                                    expanded = isMenuOpen,
                                    onDismissRequest = { isMenuOpen = false }
                                ) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Default.Logout,
                                                contentDescription = null,
                                            )

                                        },
                                        text = { Text(text = stringResource(id = R.string.logout)) },
                                        onClick = {
                                            Auth.signOut()
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    LoginActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.DeleteForever,
                                                contentDescription = null,
                                            )
                                        },
                                        text = { Text(text = stringResource(id = R.string.delete_account)) },
                                        onClick = {
                                            isRemoveAccountDialogOpen = true
                                            isMenuOpen = false
                                        }
                                    )
                                }
                            }
                        },
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                ) {
                    Card(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = stringResource(
                                        id = R.string.hello_player,
                                        player?.name ?: ""
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                IconButton(onClick = { isEditNicknameDialogOpen = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                playerColors.take(5).forEach { color ->
                                    FilledIconButton(
                                        modifier = Modifier.padding(4.dp),
                                        onClick = {
                                            selectedColor = color
                                            Firestore.Players.setPlayerColor(
                                                Auth.getCurrentUser()!!.uid,
                                                selectedColor.toArgb()
                                            ) {}
                                        },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = color,
                                            contentColor = Color.Black,
                                        ),
                                    ) {
                                        if (color == selectedColor) {
                                            Icon(
                                                imageVector = Icons.Default.Done,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                playerColors.takeLast(5).forEach { color ->
                                    FilledIconButton(
                                        modifier = Modifier.padding(4.dp),
                                        onClick = {
                                            selectedColor = color
                                            Firestore.Players.setPlayerColor(
                                                Auth.getCurrentUser()!!.uid,
                                                selectedColor.toArgb()
                                            ) {}
                                        },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = color,
                                            contentColor = Color.Black,
                                        ),
                                    ) {
                                        if (color == selectedColor) {
                                            Icon(
                                                imageVector = Icons.Default.Done,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomStart,
                        ) {
                            Image(
                                modifier = Modifier
                                    .height(140.dp)
                                    .alpha(.3f),
                                painter = painterResource(id = R.drawable.undraw_world_is_mine),
                                contentDescription = null,
                            )
                            Column(
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.new_room),
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = stringResource(id = R.string.new_room_description),
                                    modifier = Modifier.padding(8.dp),
                                )
                                ShapedButton(
                                    onClick = {
                                        isLoading = true
                                        RoomManager.createRoom(this@MainActivity) { id ->
                                            isLoading = false
                                            if (id != null) {
                                                startActivity(
                                                    Intent(
                                                        this@MainActivity,
                                                        LobbyActivity::class.java
                                                    ).putExtra(LobbyActivity.EXTRA_ROOM_ID, id)
                                                )
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    getString(R.string.new_room_error),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.End),
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = null
                                    )
                                    Text(
                                        getString(R.string.create),
                                        modifier = Modifier.padding(start = 8.dp),
                                    )
                                }
                            }
                        }

                    }
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomStart,
                        ) {
                            Image(
                                modifier = Modifier
                                    .height(120.dp)
                                    .alpha(.3f),
                                painter = painterResource(id = R.drawable.undraw_real_time_collaboration),
                                contentDescription = null,
                            )
                            Column(
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.join_room),
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                )

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    value = inviteCode,
                                    onValueChange = { inviteCode = it.uppercase() },
                                    label = { Text(text = stringResource(id = R.string.invite_code)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Password,
                                            contentDescription = null
                                        )

                                    },
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Go,
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onGo = {
                                            if (inviteCode.isNotBlank()) {
                                                startActivity(
                                                    Intent(
                                                        this@MainActivity,
                                                        LobbyActivity::class.java
                                                    ).putExtra(
                                                        LobbyActivity.EXTRA_ROOM_ID,
                                                        inviteCode.trim()
                                                    )
                                                )
                                            }
                                        }
                                    ),
                                    trailingIcon = {
                                        TextButton(
                                            enabled = inviteCode.isNotBlank(),
                                            onClick = {
                                                startActivity(
                                                    Intent(
                                                        this@MainActivity,
                                                        LobbyActivity::class.java
                                                    ).putExtra(
                                                        LobbyActivity.EXTRA_ROOM_ID,
                                                        inviteCode.trim()
                                                    )
                                                )
                                            },
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .align(Alignment.End),
                                        ) {
                                            Icon(
                                                Icons.Filled.ConnectWithoutContact,
                                                contentDescription = null
                                            )
                                            Text(
                                                getString(R.string.join),
                                                modifier = Modifier.padding(start = 8.dp),
                                            )
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.made),
                    )
                    Row {
                        IconButton(
                            onClick = {
                                WholesomeWare.openPlayStore(this@MainActivity)
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = com.csakitheone.wholesomeware_brand.R.drawable.ic_wholesomeware),
                                contentDescription = null,
                            )
                        }
                        IconButton(
                            onClick = {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.instagram.com/viktoriakerecsenyi/")
                                    )
                                )
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.instagram),
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

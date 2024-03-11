package com.wholesomeware.multiplayersudoku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.csakitheone.wholesomeware_brand.WholesomeWare
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.firebase.Firestore
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

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

    @Composable
    private fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        //TODO: gombok letiltása, míg várunk eredményre
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Fiók törlése") },
            text = { Text(text = "Biztosan szeretnéd törölni a fiókodat? Ez a művelet visszafordíthatatlan.") },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text(text = "Törlés")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                ) {
                    Text(text = "Mégse")
                }
            }
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun MainScreen() {
        MultiplayerSudokuTheme {
            //TODO: Szépítgetés, becenév szerkesztés, fiók törlés

            var isMenuOpen by remember { mutableStateOf(false) }
            var inviteCode by rememberSaveable { mutableStateOf("") }
            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                DeleteAccountDialog(
                    onConfirm = {
                        val currentUser = Auth.getCurrentUser()
                        if (currentUser != null) {
                            Firestore.Players.deletePlayerById(currentUser.uid) { isSuccess ->
                                if (isSuccess) {
                                    Auth.deleteCurrentUser {
                                        if (it) {
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    LoginActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    },
                    onDismiss = {
                        showDialog = false
                        isMenuOpen = false
                    }
                )
            }

            // Ez a surface az alkalmazás háttere. Ennek a belsejébe rakd az elemeket.
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(text = "Multiplayer Sudoku")
                        },
                        navigationIcon = {
                            //TODO: app ikon ide
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
                                    //TODO: becenév szerkesztés
                                    DropdownMenuItem(
                                        text = { Text(text = "Kijelentkezés") },
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
                                        text = { Text(text = "Fiók törlése") },
                                        onClick = {
                                            isMenuOpen = false
                                            showDialog = true
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
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text(
                                text = "Új szoba",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "Barátokat meghívni és nehézséget választani tudsz a váróteremben.",
                                modifier = Modifier.padding(8.dp),
                            )
                            Button(
                                onClick = { /*TODO*/ },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.End),
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Csatlakozás ikon"
                                )
                                Text(
                                    "Létrehozás",
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                            }

                        }
                    }
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text(
                                text = "Csatlakozás szobához",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleMedium,
                            )

                            TextField(
                                value = inviteCode,
                                onValueChange = { inviteCode = it },
                                placeholder = {
                                    Text(
                                        "Kód",
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            )

                            Button(
                                onClick = { /*TODO*/ },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.End),
                            ) {
                                Icon(
                                    Icons.Filled.ConnectWithoutContact,
                                    contentDescription = "Csatlakozás ikon"
                                )
                                Text(
                                    "Csatlakozás",
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

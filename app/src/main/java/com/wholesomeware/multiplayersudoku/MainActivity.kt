package com.wholesomeware.multiplayersudoku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
            //TODO: Szépítgetés, kijelentkezés lehetőség

            var isMenuOpen by remember { mutableStateOf(false) }
            var inviteCode by rememberSaveable { mutableStateOf("") }

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
                            //TODO: app icon
                        },
                        actions = {
                            IconButton(onClick = { isMenuOpen = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                )
                                DropdownMenu(
                                    expanded = isMenuOpen,
                                    onDismissRequest = { isMenuOpen = false }
                                ) {
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
                                    modifier = Modifier.padding(6.dp),
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
                                modifier = Modifier.padding(8.dp)
                                    .align(Alignment.End),
                            ) {
                                Icon(
                                    Icons.Filled.ConnectWithoutContact,
                                    contentDescription = "Csatlakozás ikon"
                                )
                                Text(
                                    "Csatlakozás",
                                    modifier = Modifier.padding(6.dp),
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

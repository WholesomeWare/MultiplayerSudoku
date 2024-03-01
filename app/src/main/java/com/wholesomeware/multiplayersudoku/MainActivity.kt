package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Preview
    @Composable
    private fun MainScreen() {
        MultiplayerSudokuTheme {
            //TODO: A MainActivity menüként fog szolgálni a már bejelentkezett felhasználóknak.
            // Próbálj meg egy egyszerű menüt öszerakni. Ezek tuti kellenek:
            // - Gomb, amivel játékot lehet indítani (host-olni)
            // - Szövegdoboz, amibe be lehet írni egy játék (lobby) kódját
            // - Gomb, amivel játékhoz lehet csatlakozni
            // Ezeknek még nem kell működniük, csak próbálgasd a UI-t.
            // Gomb doksija: https://developer.android.com/jetpack/compose/components/button
            // Szövegdoboz doksija: https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#textfield

            // Ez a surface az alkalmazás háttere. Ennek a belsejébe rakd az elemeket.
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // Menü elemei ide. Pl. kezdhetsz egy oszloppal (Column) és abba mehetnek a dolgok.
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 50.dp)) {
                    Text(text = "Sudoku",
                        fontSize = 30.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()) {
                        Row {
                            OutlinedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier
                                    .size(width = 180.dp, height = 200.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                        .padding(top = 10.dp)
                                ) {
                                    Text(text = "Új szoba")
                                    Spacer(modifier = Modifier.weight(1f))
                                    OutlinedButton(
                                        onClick = { /*TODO*/ }) {
                                        Text("Létrehozás")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            OutlinedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier
                                    .size(width = 180.dp, height = 200.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                        .padding(top = 10.dp)
                                ) {
                                    Text(text = "Csatlakozás szobához")
                                    var text by rememberSaveable { mutableStateOf("") }

                                    OutlinedTextField(
                                        value = text,
                                        onValueChange = { text = it },
                                        placeholder = { Text("Kód",
                                                    fontSize = 14.sp) },
                                        modifier = Modifier
                                            .width(150.dp)
                                            .height(60.dp)
                                            .padding(top = 10.dp),
                                        textStyle = TextStyle(fontSize = 14.sp)

                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    OutlinedButton(onClick = { /*TODO*/ }) {
                                        Text("Csatlakozás")
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }
    }


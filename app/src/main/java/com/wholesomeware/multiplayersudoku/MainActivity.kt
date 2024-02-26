package com.wholesomeware.multiplayersudoku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku
import com.wholesomeware.multiplayersudoku.sudoku.SudokuGenerator
import com.wholesomeware.multiplayersudoku.ui.components.SudokuDisplay
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            val coroutineScope = rememberCoroutineScope()
            var sudoku by remember { mutableStateOf(Sudoku.EMPTY) }
            var isReady by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                sudoku = SudokuGenerator.create(Sudoku.Difficulty.EASY)
                isReady = true
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    SudokuDisplay(
                        sudoku = sudoku,
                    )
                    Button(
                        onClick = {
                            isReady = false
                            coroutineScope.launch(Dispatchers.Default) {
                                sudoku = SudokuGenerator.create(Sudoku.Difficulty.EASY)
                                isReady = true
                            }
                        },
                        enabled = isReady,
                    ) {
                        Text(text = "Új feladvány")
                    }
                }
            }
        }
    }
}

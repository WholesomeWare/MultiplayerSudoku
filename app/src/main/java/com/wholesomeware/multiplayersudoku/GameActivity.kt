package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku
import com.wholesomeware.multiplayersudoku.sudoku.SudokuGenerator
import com.wholesomeware.multiplayersudoku.ui.components.SudokuDisplay
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen()
        }
    }

    @Preview
    @Composable
    private fun GameScreen() {
        MultiplayerSudokuTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SudokuDisplay(
                    modifier = Modifier
                        .padding(16.dp),
                    sudoku = Sudoku.EMPTY,
                    onCellClick = { row, column ->
                        Log.d("Sudoku", "Cell clicked: $row, $column")
                    },
                    cellBorderColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

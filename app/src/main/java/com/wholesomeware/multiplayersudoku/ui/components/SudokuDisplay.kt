package com.wholesomeware.multiplayersudoku.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku

@Composable
fun SudokuDisplay(
    modifier: Modifier = Modifier,
    sudoku: Sudoku,
) {
    Box(modifier = modifier) {
        Column {
            sudoku.grid.forEach { row ->
                Row {
                    row.forEach {
                        SudokuCell(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            value = it,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuCell(
    modifier: Modifier = Modifier,
    value: Int,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(visible = value != 0) {
            Text(text = value.toString())
        }
    }
}

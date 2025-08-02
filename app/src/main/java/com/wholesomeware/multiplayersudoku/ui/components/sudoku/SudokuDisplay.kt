package com.wholesomeware.multiplayersudoku.ui.components.sudoku

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.model.Player
import com.wholesomeware.multiplayersudoku.model.SudokuPosition
import com.wholesomeware.multiplayersudoku.sudoku.Sudoku

/**
 * Egy egész sudoku táblázatot megjelenítő komponens.
 * @param sudoku A megjelenítendő [Sudoku].
 */
@Composable
fun SudokuDisplay(
    modifier: Modifier = Modifier,
    sudoku: Sudoku,
    onCellClick: (Int, Int) -> Unit,
    playerPositions: Map<Player, SudokuPosition> = emptyMap(),
    cellBorderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val sectionBorderThickness = 3.dp

    Box(
        modifier = modifier
            .border(sectionBorderThickness, cellBorderColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(modifier = Modifier
                .height(sectionBorderThickness)
                .fillMaxWidth()
                .background(cellBorderColor))
            Box(modifier = Modifier
                .height(sectionBorderThickness)
                .fillMaxWidth()
                .background(cellBorderColor))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(modifier = Modifier
                .width(sectionBorderThickness)
                .fillMaxHeight()
                .background(cellBorderColor))
            Box(modifier = Modifier
                .width(sectionBorderThickness)
                .fillMaxHeight()
                .background(cellBorderColor))
        }
        Column {
            sudoku.currentGrid.forEachIndexed { rowIndex, row ->
                Row {
                    row.forEachIndexed { columnIndex, column ->
                        val position = SudokuPosition(rowIndex, columnIndex)
                        SudokuCell(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            value = column,
                            onClick = { onCellClick(rowIndex, columnIndex) },
                            isWritable = sudoku.startingGrid[rowIndex][columnIndex] == 0,
                            selection = playerPositions.filter { it.value == position }.keys.firstOrNull(),
                            borderColor = cellBorderColor,
                        )
                    }
                }
            }
        }
    }
}


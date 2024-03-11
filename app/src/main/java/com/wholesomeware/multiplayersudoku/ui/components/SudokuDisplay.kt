package com.wholesomeware.multiplayersudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    cellBorderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    //TODO Csákinak: cella kiválasztás és írható cellák megkülönböztetése

    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(modifier = Modifier.height(2.dp).fillMaxWidth().background(cellBorderColor))
            Box(modifier = Modifier.height(2.dp).fillMaxWidth().background(cellBorderColor))
        }
        Row(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(cellBorderColor))
            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(cellBorderColor))
        }
        Column {
            sudoku.grid.forEachIndexed { rowIndex, row ->
                Row {
                    row.forEachIndexed { columnIndex, column ->
                        SudokuCell(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            value = column,
                            onClick = { onCellClick(rowIndex, columnIndex) },
                            borderColor = cellBorderColor,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Egy cella a sudoku táblázatban.
 * @param value A cella értéke. 0 esetén üres cellát jelent.
 */
@Composable
fun SudokuCell(
    modifier: Modifier = Modifier,
    value: Int,
    onClick: () -> Unit,
    borderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Box(
        modifier = modifier
            .border(.01f.dp, borderColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (value != 0) {
            Text(text = value.toString())
        }
    }
}

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

/**
 * Egy egész sudoku táblázatot megjelenítő komponens.
 * @param sudoku A megjelenítendő [Sudoku].
 */
@Composable
fun SudokuDisplay(
    modifier: Modifier = Modifier,
    sudoku: Sudoku,
) {
    Box(modifier = modifier) {
        //TODO: Cellák közti vastagabb vonalakat valahogy itt kellene megoldani.
        // A Box elemben minden egymásban van, szóval itt meg lehet oldani, hogy a vonalak a cellák mögött legyenek.
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

/**
 * Egy cella a sudoku táblázatban.
 * @param value A cella értéke. 0 esetén üres cellát jelent.
 */
@Composable
fun SudokuCell(
    modifier: Modifier = Modifier,
    value: Int,
) {
    //TODO: Cellák közti vékony vonalakat itt kell megcsinálni.
    // Szerintem a legegyszerűbb az itteni Box elemre valami körvonalat rakni.
    // Bár ugyanezt meg lehet oldani a SudokuDisplay-ben is ahol meghívjuk ezt a függvényt.
    // Hát ahol jobbnak érezzük...
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (value != 0) {
            Text(text = value.toString())
        }
    }
}

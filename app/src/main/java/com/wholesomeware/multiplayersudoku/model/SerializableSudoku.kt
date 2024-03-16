package com.wholesomeware.multiplayersudoku.model

import com.wholesomeware.multiplayersudoku.sudoku.Sudoku

/**
 * Ez az osztály felelős azért, hogy egy sudoku tábla adatbázisban tárolható legyen.
 * Játékmenet közben, generáláskor és ellenőrzéskor a [Sudoku] osztályt használjuk.
 */
data class SerializableSudoku(
    val startingGrid: Map<String, List<Int>> = emptyMap(),
    val currentGrid: Map<String, List<Int>> = emptyMap(),
) {
    companion object {
        fun fromSudoku(sudoku: Sudoku) = SerializableSudoku(
            startingGrid = sudoku.startingGrid
                .mapIndexed { index, ints -> index.toString() to ints.toList() }
                .toMap(),
            currentGrid = sudoku.currentGrid
                .mapIndexed { index, ints -> index.toString() to ints.toList() }
                .toMap(),
        )
    }

    fun toSudoku() = Sudoku(
        startingGrid = startingGrid.map { it.value.toIntArray() }.toTypedArray(),
        currentGrid = currentGrid.map { it.value.toIntArray() }.toTypedArray(),
    )
}
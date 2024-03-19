package com.wholesomeware.multiplayersudoku.model

import com.wholesomeware.multiplayersudoku.sudoku.Sudoku

/**
 * Ez az osztály felelős azért, hogy egy sudoku tábla adatbázisban tárolható legyen.
 * Játékmenet közben, generáláskor és ellenőrzéskor a [Sudoku] osztályt használjuk.
 */
data class SerializableSudoku(
    val startingGrid: String = "",
    val currentGrid: String = "",
) {
    companion object {
        fun fromSudoku(sudoku: Sudoku) = SerializableSudoku(
            startingGrid = sudoku.startingGrid
                .joinToString(separator = ",") {
                    it.joinToString(separator = "")
                },
            currentGrid = sudoku.currentGrid
                .joinToString(separator = ",") {
                    it.joinToString(separator = "")
                },
        )
    }

    fun toSudoku() = Sudoku(
        startingGrid = startingGrid
            .split(',')
            .map { row -> row.toList().map { it.digitToInt() }.toIntArray() }
            .toTypedArray(),
        currentGrid = currentGrid
            .split(',')
            .map { row -> row.toList().map { it.digitToInt() }.toIntArray() }
            .toTypedArray(),
    )
}
package com.wholesomeware.multiplayersudoku.model

import com.wholesomeware.multiplayersudoku.sudoku.Sudoku

/**
 * Ez az osztály felelős azért, hogy egy sudoku tábla adatbázisban tárolható legyen.
 * Játékmenet közben, generáláskor és ellenőrzéskor a [Sudoku] osztályt használjuk.
 */
data class SerializableSudoku(
    val serializableGrid: Map<String, List<Int>> = emptyMap(),
) {
    companion object {
        fun fromSudoku(sudoku: Sudoku) = SerializableSudoku(
            serializableGrid = sudoku.grid.mapIndexed { index, ints -> index.toString() to ints.toList() }.toMap()
        )
    }

    fun toSudoku() = Sudoku(
        grid = serializableGrid.map { it.value.toIntArray() }.toTypedArray()
    )
}
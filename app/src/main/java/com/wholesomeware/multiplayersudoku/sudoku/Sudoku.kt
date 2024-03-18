package com.wholesomeware.multiplayersudoku.sudoku

import com.wholesomeware.multiplayersudoku.R

/**
 * A sudoku tábla reprezentációja. Adatbázisban nem tárolható,
 * helyette a [com.wholesomeware.multiplayersudoku.model.SerializableSudoku] használatos!
 */
class Sudoku(
    val startingGrid: Array<IntArray>,
    val currentGrid: Array<IntArray> = startingGrid.map { it.clone() }.toTypedArray(),
) {
    fun isCellWritable(location: Pair<Int, Int>?): Boolean {
        if (location == null) {
            return false
        }
        return startingGrid[location.first][location.second] == 0
    }

    /**
     * Egy cella értékét beállítja, ha az írható és visszaadja az új [Sudoku] objektumot.
     */
    fun setCellIfWritable(location: Pair<Int, Int>?, value: Int): Sudoku {
        if (location == null || !isCellWritable(location)) {
            return this
        }

        return Sudoku(
            startingGrid,
            currentGrid.map { it.clone() }.toTypedArray().also {
                it[location.first][location.second] = value
            }
        )
    }

    companion object {
        val EMPTY = Sudoku(Array(9) { IntArray(9) { 0 } })
    }

    enum class Difficulty(
        val stringResourceId: Int,
        val filledCellsCount: Int,
    ) {
        EASY(
            R.string.difficulty_easy,
            50,
        ),
        MEDIUM(
            R.string.difficulty_medium,
            40,
        ),
        HARD(
            R.string.difficulty_hard,
            30,
        ),
    }
}
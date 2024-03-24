package com.wholesomeware.multiplayersudoku.sudoku

import com.wholesomeware.multiplayersudoku.R
import com.wholesomeware.multiplayersudoku.model.SudokuPosition

/**
 * A sudoku tábla reprezentációja. Adatbázisban nem tárolható,
 * helyette a [com.wholesomeware.multiplayersudoku.model.SerializableSudoku] használatos!
 */
class Sudoku(
    val startingGrid: Array<IntArray>,
    val currentGrid: Array<IntArray> = startingGrid.map { it.clone() }.toTypedArray(),
) {
    val currentList = currentGrid.map { it.toList() }.toList().flatten()

    fun count(digit: Int): Int = currentList.count { it == digit }
    fun count(predicate: (Int) -> Boolean): Int = currentList.count(predicate)

    fun isCellWritable(position: SudokuPosition?): Boolean {
        if (position == null) {
            return false
        }
        return startingGrid[position.row][position.column] == 0
    }

    /**
     * Egy cella értékét beállítja, ha az írható és visszaadja az új [Sudoku] objektumot.
     */
    fun setCellIfWritable(position: SudokuPosition?, value: Int): Sudoku {
        if (position == null || !isCellWritable(position)) {
            return this
        }

        return Sudoku(
            startingGrid,
            currentGrid.map { it.clone() }.toTypedArray().also {
                it[position.row][position.column] = value
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

        ONE_MISSING(
            R.string.difficulty_easy,
            9 * 9 - 2,
        ),
    }
}
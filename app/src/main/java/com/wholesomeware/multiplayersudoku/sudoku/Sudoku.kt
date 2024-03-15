package com.wholesomeware.multiplayersudoku.sudoku

import com.wholesomeware.multiplayersudoku.R

/**
 * A sudoku tábla reprezentációja. Adatbázisban nem tárolható,
 * helyette a [com.wholesomeware.multiplayersudoku.model.SerializableSudoku] használatos!
 */
class Sudoku(
    var grid: Array<IntArray>,
) {
    companion object {
        val EMPTY = Sudoku(Array(9) { IntArray(9) { 0 } })
    }

    enum class Difficulty(
        val stringResourceId: Int,
        val filledCellsCount: Int,
    ) {
        EASY(
            R.string.difficulty_easy,
            35,
        ),
        MEDIUM(
            R.string.difficulty_medium,
            25,
        ),
        HARD(
            R.string.difficulty_hard,
            15,
        ),
    }
}
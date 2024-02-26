package com.wholesomeware.multiplayersudoku.sudoku

class Sudoku(
    var grid: Array<IntArray>,
) {
    enum class Difficulty(
        val filledCellsCount: Int,
    ) {
        ONE_MISSING(9 * 9 - 1),
        EASY(35),
        MEDIUM(25),
        HARD(15),
    }
}
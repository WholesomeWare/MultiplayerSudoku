package com.wholesomeware.multiplayersudoku.sudoku

class Sudoku(
    var grid: Array<IntArray>,
) {
    companion object {
        val EMPTY = Sudoku(Array(9) { IntArray(9) { 0 } })
    }

    enum class Difficulty(
        val filledCellsCount: Int,
    ) {
        EASY(35),
        MEDIUM(25),
        HARD(15),
    }
}
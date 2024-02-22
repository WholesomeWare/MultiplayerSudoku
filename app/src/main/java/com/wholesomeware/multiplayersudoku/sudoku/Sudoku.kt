package com.wholesomeware.multiplayersudoku.sudoku

class Sudoku {
    var grid = Array(9) { IntArray(9) }
        private set

    enum class Difficulty(
        val filledCellsCount: Int,
    ) {
        EASY(35),
        MEDIUM(25),
        HARD(15),
    }
}
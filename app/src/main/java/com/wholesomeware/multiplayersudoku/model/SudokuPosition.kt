package com.wholesomeware.multiplayersudoku.model

data class SudokuPosition(
    var row: Int = 0,
    var column: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        when (other) {
            is Pair<*, *> -> {
                val (otherRow, otherColumn) = other
                return row == otherRow && column == otherColumn
            }
            is SudokuPosition -> {
                return row == other.row && column == other.column
            }
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return Pair(row, column).hashCode()
    }

    companion object {
        fun Pair<Int, Int>.toSudokuPosition() = SudokuPosition(first, second)
    }
}

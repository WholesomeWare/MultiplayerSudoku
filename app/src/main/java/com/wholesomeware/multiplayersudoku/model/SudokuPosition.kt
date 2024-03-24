package com.wholesomeware.multiplayersudoku.model

data class SudokuPosition(
    var row: Int = 0,
    var column: Int = 0
) {
    override fun toString(): String {
        return "($row, $column)"
    }

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
        fun fromString(string: String?): SudokuPosition {
            if (string.isNullOrBlank()) return SudokuPosition()

            val (row, column) = string
                .removeSurrounding("(", ")")
                .split(",")
                .map { it.trim().toInt() }
            return SudokuPosition(row, column)
        }

        fun Pair<Int, Int>.toSudokuPosition() = SudokuPosition(first, second)
    }
}

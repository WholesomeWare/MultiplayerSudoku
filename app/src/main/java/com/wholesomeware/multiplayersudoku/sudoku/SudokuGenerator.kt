package com.wholesomeware.multiplayersudoku.sudoku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

class SudokuGenerator {
    companion object {

        private const val GRID_SIZE = 9
        private const val GRID_SIZE_SQUARE_ROOT = 3
        private const val MIN_DIGIT_VALUE = 1
        private const val MAX_DIGIT_VALUE = 9
        private const val MIN_DIGIT_INDEX = 0
        private const val MAX_DIGIT_INDEX = 8

        private lateinit var grid: Array<IntArray>

        var isGenerating by mutableStateOf(false)
            private set

        suspend fun create(difficulty: Sudoku.Difficulty): Sudoku {
            grid = Array(GRID_SIZE) { IntArray(GRID_SIZE) }
            fillGrid(difficulty)
            return Sudoku(grid)
        }

        private fun setCell(row: Int, column: Int, value: Int) {
            grid[row][column] = value
        }

        private fun fillGrid(difficulty: Sudoku.Difficulty) {
            fillDiagonalBoxes()
            fillRemaining(0, GRID_SIZE_SQUARE_ROOT)
            removeDigits(difficulty)
        }

        private fun fillDiagonalBoxes() {
            for (i in 0 until GRID_SIZE step GRID_SIZE_SQUARE_ROOT) {
                fillBox(i, i)
            }
        }

        private fun fillBox(row: Int, column: Int) {
            var generatedDigit: Int

            for (i in 0 until GRID_SIZE_SQUARE_ROOT) {
                for (j in 0 until GRID_SIZE_SQUARE_ROOT) {
                    do {
                        generatedDigit = generateRandomInt(MIN_DIGIT_VALUE, MAX_DIGIT_VALUE)
                    } while (!isUnusedInBox(row, column, generatedDigit))

                    setCell(row + i, column + j, generatedDigit)
                }
            }
        }

        private fun generateRandomInt(min: Int, max: Int) = Random.nextInt(min, max + 1)

        private fun isUnusedInBox(rowStart: Int, columnStart: Int, digit: Int) : Boolean {
            for (i in 0 until GRID_SIZE_SQUARE_ROOT) {
                for (j in 0 until GRID_SIZE_SQUARE_ROOT) {
                    if (grid[rowStart + i][columnStart + j] == digit) {
                        return false
                    }
                }
            }
            return true
        }

        private fun fillRemaining(i: Int, j: Int) : Boolean {
            var i = i
            var j = j

            if (j >= GRID_SIZE && i < GRID_SIZE - 1) {
                i += 1
                j = 0
            }
            if (i >= GRID_SIZE && j >= GRID_SIZE) {
                return true
            }
            if (i < GRID_SIZE_SQUARE_ROOT) {
                if (j < GRID_SIZE_SQUARE_ROOT) {
                    j = GRID_SIZE_SQUARE_ROOT
                }
            } else if (i < GRID_SIZE - GRID_SIZE_SQUARE_ROOT) {
                if (j == (i / GRID_SIZE_SQUARE_ROOT) * GRID_SIZE_SQUARE_ROOT) {
                    j += GRID_SIZE_SQUARE_ROOT
                }
            } else {
                if (j == GRID_SIZE - GRID_SIZE_SQUARE_ROOT) {
                    i += 1
                    j = 0
                    if (i >= GRID_SIZE) {
                        return true
                    }
                }
            }

            for (digit in 1..MAX_DIGIT_VALUE) {
                if (isSafeToPutIn(i, j, digit)) {
                    setCell(i, j, digit)
                    if (fillRemaining(i, j + 1)) {
                        return true
                    }
                    setCell(i, j, 0)
                }
            }
            return false
        }

        private fun isSafeToPutIn(row: Int, column: Int, digit: Int) =
            isUnusedInBox(findBoxStart(row), findBoxStart(column), digit)
                    && isUnusedInRow(row, digit)
                    && isUnusedInColumn(column, digit)

        private fun findBoxStart(index: Int) = index - index % GRID_SIZE_SQUARE_ROOT

        private fun isUnusedInRow(row: Int, digit: Int) : Boolean {
            for (i in 0 until GRID_SIZE) {
                if (grid[row][i] == digit) {
                    return false
                }
            }
            return true
        }

        private fun isUnusedInColumn(column: Int, digit: Int) : Boolean {
            for (i in 0 until GRID_SIZE) {
                if (grid[i][column] == digit) {
                    return false
                }
            }
            return true
        }

        private fun removeDigits(difficulty: Sudoku.Difficulty) {
            var digitsToRemove = GRID_SIZE * GRID_SIZE - difficulty.filledCellsCount

            while (digitsToRemove > 0) {
                val randomRow = generateRandomInt(MIN_DIGIT_INDEX, MAX_DIGIT_INDEX)
                val randomColumn = generateRandomInt(MIN_DIGIT_INDEX, MAX_DIGIT_INDEX)

                if (grid[randomRow][randomColumn] != 0) {
                    val digitToRemove = grid[randomRow][randomColumn]
                    setCell(randomRow, randomColumn, 0)
                    if (!SudokuSolver.isGridSolvable(grid)) {
                        setCell(randomRow, randomColumn, digitToRemove)
                    } else {
                        digitsToRemove--
                    }
                }
            }
        }

    }
}
package com.wholesomeware.multiplayersudoku.model

data class Room(
    val id: String = "",
    val players: List<String> = emptyList(),
    val ownerId: String = "",
    val difficultyId: Int = 0,
    @JvmField
    val isStarted: Boolean = false,
    val sudoku: SerializableSudoku = SerializableSudoku(),
    val startTime: Long = 0,
    val startingHintsCount: Int = 0,
)
package com.wholesomeware.multiplayersudoku.model

data class Room(
    val id: String = "",
    val players: List<String> = emptyList(),
    val ownerId: String = "",
    val isStarted: Boolean = false,
    val currentGrid: List<List<Int>> = emptyList(),
    val startTime: Long = 0,
    val startingHintsCount: Int = 0,
)
package com.wholesomeware.multiplayersudoku.model

data class Room(
    val id: String = "",
    val gameVersion: Long = 0,
    val players: List<String> = emptyList(),
    val ownerId: String = "",
    val difficultyId: Int = 0,
    @JvmField
    val isStarted: Boolean = false,
    val startTime: Long = 0,
    val endTime: Long = 0,
    val startingHintsCount: Int = 0,
)
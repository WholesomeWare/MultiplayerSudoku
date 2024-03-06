package com.wholesomeware.multiplayersudoku.model

//TODO mindenkinek: Találjuk ki együtt, hogy pontosan mi kell egy "szobába".
data class Room(
    val id: String = "",
    val players: List<Player> = emptyList(),
    val ownerId: String = "",
    val isStarted: Boolean = false,
    val currentGrid: List<List<Int>> = emptyList(),
    val startTime: Long = 0,
)
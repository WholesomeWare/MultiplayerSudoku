package com.wholesomeware.multiplayersudoku.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Egy játékos adatait tartalmazó osztály.
 * @param id A játékos azonosítója. Megegyezik a Firebase uid-vel.
 * @param name A játékos beceneve, ami megjelenik a játékban.
 */
data class Player(
    val id: String = "",
    val name: String = "",
    val color: Int = Color.Gray.toArgb(),
)
package com.wholesomeware.multiplayersudoku.model

/**
 * Egy játékos adatait tartalmazó osztály.
 * @param id A játékos azonosítója. Megegyezik a Firebase uid-vel.
 * @param name A játékos beceneve, ami megjelenik a játékban.
 */
data class Player(
    val id: String = "",
    val name: String = "",
)
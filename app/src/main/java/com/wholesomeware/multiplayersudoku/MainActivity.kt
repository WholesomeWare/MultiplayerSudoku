package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Preview
    @Composable
    private fun MainScreen() {
        MultiplayerSudokuTheme {
            //TODO: A MainActivity menüként fog szolgálni a már bejelentkezett felhasználóknak.
            // Próbálj meg egy egyszerű menüt öszerakni. Ezek tuti kellenek:
            // - Gomb, amivel játékot lehet indítani (host-olni)
            // - Szövegdoboz, amibe be lehet írni egy játék (lobby) kódját
            // - Gomb, amivel játékhoz lehet csatlakozni
            // Ezeknek még nem kell működniük, csak próbálgasd a UI-t.
            // Gomb doksija: https://developer.android.com/jetpack/compose/components/button
            // Szövegdoboz doksija: https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#textfield

            // Ez a surface az alkalmazás háttere. Ennek a belsejébe rakd az elemeket.
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // Menü elemei ide. Pl. kezdhetsz egy oszloppal (Column) és abba mehetnek a dolgok.
            }
        }
    }
}

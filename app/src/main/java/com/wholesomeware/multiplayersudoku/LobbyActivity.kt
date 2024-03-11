package com.wholesomeware.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wholesomeware.multiplayersudoku.firebase.Auth
import com.wholesomeware.multiplayersudoku.model.Room
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class LobbyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LobbyScreen()
        }
    }

    @Preview
    @Composable
    private fun LobbyScreen() {
        MultiplayerSudokuTheme {
            var room by remember { mutableStateOf(Room()) }

            //TODO: játékos lista, kezdés gomb és nehézség állítás a tulajnak

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = if (room.ownerId == Auth.getCurrentUser()?.uid) "Tulaj vagyok"
                    else "Vendég vagyok"
                )
            }
        }
    }
}

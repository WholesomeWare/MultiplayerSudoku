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
import com.wholesomeware.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }

    @Preview
    @Composable
    private fun LoginScreen() {
        MultiplayerSudokuTheme {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                //TODO: Bejelentkezési felület. Csak hogy meglegyen a kellő komplexitás,
                // legyen email-jelszó kombós és Google bejelentkezés.
                // Ezekhez összerakom a backend-et, próbálj csinálni valami UI-t neki.
            }
        }
    }
}

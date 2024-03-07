package com.wholesomeware.multiplayersudoku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wholesomeware.multiplayersudoku.firebase.Auth
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
            var errorMessage by remember { mutableStateOf("") }

            //TODO: szépítgetés
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        isError = errorMessage.isNotBlank(),
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        supportingText = {Text(errorMessage)},
                        isError = errorMessage.isNotBlank(),
                    )
                    Row(){
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = {
                                Auth.registerWithEmailAndPassword(email, password) {
                                    if(it){
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        finish()
                                    }
                                    else{
                                        errorMessage = "Hiba!"
                                    }
                                }
                            },
                        ) {
                            Text("Regisztráció")
                        }

                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = {
                                Auth.signInWithEmailAndPassword(email, password) {
                                    if(it){
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        finish()
                                    }
                                    else{
                                        errorMessage = "Hiba!"
                                    }
                                }
                            },
                        ) {
                            Text("Bejelentkezés")
                        }
                    }



                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            /*TODO Auth függvény meghívása*/
                        },
                    ) {
                        Text("Google bejelentkezés")
                    }
                }
            }
        }
    }
}

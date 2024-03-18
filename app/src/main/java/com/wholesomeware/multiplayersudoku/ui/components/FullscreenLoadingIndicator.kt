package com.wholesomeware.multiplayersudoku.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun FullscreenLoadingIndicator(
    isLoading: Boolean,
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = {},
        ) {
            CircularProgressIndicator()
        }
    }
}
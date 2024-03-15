package com.wholesomeware.multiplayersudoku.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun BlockableExtendedFAB(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean = true,
    expanded: Boolean = true,
    shape: Shape = FloatingActionButtonDefaults.extendedFabShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        text = text,
        icon = icon,
        onClick = { if (enabled) onClick() },
        expanded = expanded,
        shape = shape,
        containerColor = if (enabled) containerColor else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (enabled) contentColor else MaterialTheme.colorScheme.onSurfaceVariant,
        elevation = if (enabled) elevation else FloatingActionButtonDefaults.loweredElevation(
            defaultElevation = 0.dp,
        ),
        interactionSource = interactionSource,
    )
}
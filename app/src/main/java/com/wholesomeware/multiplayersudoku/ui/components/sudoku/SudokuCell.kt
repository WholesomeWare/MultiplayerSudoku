package com.wholesomeware.multiplayersudoku.ui.components.sudoku

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.wholesomeware.multiplayersudoku.model.Player

/**
 * Egy cella a sudoku táblázatban.
 * @param value A cella értéke. 0 esetén üres cellát jelent.
 */
@Composable
fun SudokuCell(
    modifier: Modifier = Modifier,
    value: Int,
    onClick: () -> Unit,
    isWritable: Boolean,
    selection: Player? = null,
    borderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Box(
        modifier = modifier
            .border(.01f.dp, borderColor)
            .clickable { onClick() }
            .background(
                if (!isWritable) Color.Gray.copy(alpha = .2f)
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(visible = selection != null) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(selection?.color ?: Color.Gray.toArgb())),
            )
        }
        if (value != 0) {
            Text(
                modifier = Modifier.alpha(if (isWritable) 1f else .8f),
                text = value.toString(),
            )
        }
    }
}

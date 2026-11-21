package com.ce.kit.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ce.kit.ui.theme.LocalGlass
import com.ce.kit.ui.theme.LocalSkin

/**
 * The menu that opens from a "more" button. A popup window cannot blur what is behind it, so it uses the menu fill
 * (never clearer than [com.ce.kit.ui.glass.GlassTokens.MENU_MIN_ALPHA]) and the corners of the current design
 * (square in Retro 98), the same in every app.
 */
@Composable
fun GlassMenu(expanded: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = LocalSkin.current.shape(RoundedCornerShape(20.dp)),
        containerColor = LocalGlass.current.menu,
        content = content,
    )
}

/** One entry of a [GlassMenu]: text, optional icon. [destructive] draws it in the error colour. */
@Composable
fun GlassMenuItem(text: String, onClick: () -> Unit, icon: ImageVector? = null, destructive: Boolean = false, enabled: Boolean = true) {
    val tint = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    DropdownMenuItem(
        text = { Text(text, color = tint) },
        onClick = onClick,
        enabled = enabled,
        leadingIcon = icon?.let { { Icon(it, null, tint = tint) } },
    )
}

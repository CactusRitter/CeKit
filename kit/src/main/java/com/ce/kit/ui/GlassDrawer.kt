package com.ce.kit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ce.kit.ui.glass.GlassKind
import com.ce.kit.ui.glass.glass

/**
 * The side menu (☰) of every Ce app, to use as `drawerContent` of a ModalNavigationDrawer: a transparent full-height sheet
 * (a tap outside the panel calls [onDismiss]) holding one frosted-glass panel (the card fill with real blur, never an opaque
 * white sheet) that scrolls. Put the rows in [content].
 */
@Composable
fun GlassDrawerSheet(onDismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    ModalDrawerSheet(
        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
        drawerContainerColor = Color.Transparent,
        windowInsets = WindowInsets(0),
    ) {
        Column(
            Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(12.dp)
                .fillMaxWidth(0.82f)
                // Swallows taps on the panel so only the area around it closes the drawer.
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .glass(RoundedCornerShape(28.dp), GlassKind.Card, elevation = 12.dp)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

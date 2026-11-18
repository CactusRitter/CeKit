package com.ce.kit.ui

import androidx.compose.ui.unit.dp

/** Measures shared by the Ce apps' controls, dialogs and sheets. */
object CeSizes {
    val touchTarget = 48.dp
    val iconSmall = 16.dp
    val icon = 22.dp
    val hairline = 1.dp

    /** Sheets, dialogs' larger cousins and settings pages never get wider than this. */
    val maxFormWidth = 640.dp

    /** A sheet floating in a wide window stays a card instead of growing to the full height. */
    val maxFloatingSheetHeight = 860.dp
}

/**
 * Window size classes (Material breakpoints). Layouts go by the window, not the device's
 * orientation, so tablets, foldables and split screen get the layout that fits their space.
 */
object WindowTokens {
    val mediumWidth = 600.dp
    val expandedWidth = 840.dp

    /** Below this height (phones in landscape) every bit of height counts: no floating sheets. */
    val mediumHeight = 480.dp
}

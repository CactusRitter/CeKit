package com.ce.kit.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.ce.kit.ui.glass.glassTile
import com.ce.kit.ui.theme.LocalSkin

/**
 * The text field of every Ce app: a glass tile (same fill as cells and cards, never a Material underline) with the corners
 * of the current design, a hairline outline in the design's colour, the app's text style and a hint. Uniform in all designs.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    /** Small title above the text (a field with a label, like Material's). */
    label: String? = null,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = MaterialTheme.colorScheme
    val shape = LocalSkin.current.shape(RoundedCornerShape(16.dp))
    androidx.compose.foundation.layout.Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = CeSizes.touchTarget)
            .glassTile(shape)
            .border(CeSizes.hairline, if (isError) colors.error else colors.outlineVariant, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        Column {
        if (label != null) Text(label, style = MaterialTheme.typography.labelMedium, color = if (isError) colors.error else colors.onSurfaceVariant, maxLines = 1)
        Box {
        if (value.isEmpty() && placeholder != null) {
            Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = colors.onSurfaceVariant, maxLines = 1)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
        )
        }
        }
        }
        trailingIcon?.invoke()
    }
}

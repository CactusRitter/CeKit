package com.ce.kit.ui.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ce.kit.R
import com.ce.kit.ui.glass.GlassKind
import com.ce.kit.ui.glass.GlassSlider
import com.ce.kit.ui.glass.bounceSpring
import com.ce.kit.ui.glass.glass
import com.ce.kit.ui.glass.pressScale
import com.ce.kit.ui.theme.Accent
import com.ce.kit.ui.theme.CeAppearance
import com.ce.kit.ui.theme.LocalGlass
import com.ce.kit.ui.theme.Skin
import com.ce.kit.ui.theme.ThemeMode
import com.ce.kit.ui.theme.ThemeStyle
import com.ce.kit.ui.theme.customPalette
import java.text.NumberFormat
import kotlin.math.roundToInt

/** What the Appearance page changes. Each app implements it on its own settings store (and refreshes its widgets there). */
interface CeAppearanceActions {
    fun setSkin(skin: Skin)
    fun setThemeMode(mode: ThemeMode)
    fun setDepth3d(on: Boolean)
    fun setThemeStyle(style: ThemeStyle)
    fun setAccent(accent: Accent)
    fun setCustomHue(hue: Float)
    fun setTransparency(percent: Int)
}

/**
 * Settings → Appearance, the same in every Ce app: design, light/dark, 3D shadow, style, colour (eight, or any hue) and
 * glass transparency with a preview. [extra] puts the app's own cards (e.g. which side the list goes on) after the 3D shadow.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppearancePage(settings: CeAppearance, actions: CeAppearanceActions, extra: @Composable () -> Unit = {}) {
    val glassLook = settings.skin == Skin.GLASS
    val dark = LocalGlass.current.dark
    SettingsCard {
        Text(stringResource(R.string.skin_title), style = MaterialTheme.typography.titleSmall)
        Text(stringResource(R.string.skin_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Skin.entries.forEach { skin ->
                ChoiceChip(stringResource(skin.label), settings.skin == skin) { actions.setSkin(skin) }
            }
        }
        if (settings.skin.forcedDark != null) {
            // Only one mode exists for this design: no light/dark choice to offer.
            Text(stringResource(R.string.skin_always_dark), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
            Text(stringResource(R.string.theme_mode_title), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    ThemeMode.SYSTEM to R.string.theme_system,
                    ThemeMode.LIGHT to R.string.theme_light,
                    ThemeMode.DARK to R.string.theme_dark,
                ).forEach { (mode, label) ->
                    ChoiceChip(stringResource(label), settings.themeMode == mode) { actions.setThemeMode(mode) }
                }
            }
        }
    }
    if (glassLook) SwitchRow(R.string.depth_title, R.string.depth_body, settings.depth3d, actions::setDepth3d)
    extra()
    SettingsCard {
        if (glassLook) {
            Text(stringResource(R.string.style_title), style = MaterialTheme.typography.titleSmall)
            Text(stringResource(R.string.style_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeStyle.entries.forEach { style ->
                    ChoiceChip(stringResource(style.label), settings.themeStyle == style) { actions.setThemeStyle(style) }
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }
        Text(stringResource(R.string.accent_title), style = MaterialTheme.typography.titleSmall)
        Text(stringResource(R.string.accent_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Accent.entries.forEach { accent ->
                AccentDot(if (dark) accent.dark else accent.light, stringResource(accent.label), selected = settings.customHue == null && settings.accent == accent) {
                    actions.setAccent(accent)
                }
            }
        }
        Text(stringResource(R.string.accent_custom), Modifier.padding(top = 4.dp), style = MaterialTheme.typography.titleSmall)
        HueBar(settings.customHue) { hue -> actions.setCustomHue(hue) }
    }
    if (glassLook) SettingsCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.settings_transparency_title), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Text(
                NumberFormat.getPercentInstance().format(settings.transparency / 100.0),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(stringResource(R.string.settings_transparency_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        GlassSlider(
            value = settings.transparency.toFloat(),
            onValueChange = { actions.setTransparency(it.roundToInt()) },
            valueRange = 0f..100f,
        )
        // How the glass looks right now: coloured bars behind a floating pill.
        Box(
            Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.background)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)) // tinted like the background of the app
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val c = MaterialTheme.colorScheme
                listOf(
                    c.primary to 0.5f, c.error to 0.8f,
                    (if (dark) Accent.TEAL.dark else Accent.TEAL.light) to 0.35f,
                    (if (dark) Accent.AMBER.dark else Accent.AMBER.light) to 0.65f,
                ).forEach { (color, part) ->
                    Box(Modifier.fillMaxWidth(part).height(14.dp).clip(CircleShape).background(color.copy(alpha = 0.8f)))
                }
            }
            Text(
                stringResource(R.string.preview),
                Modifier.align(Alignment.Center).glass(CircleShape, GlassKind.Chrome, elevation = 6.dp).padding(horizontal = 22.dp, vertical = 10.dp),
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

/** One accent colour to pick: a round swatch, with a check and a ring when it is the one in use. */
@Composable
private fun AccentDot(color: Color, name: String, selected: Boolean, onClick: () -> Unit) {
    val ring by animateDpAsState(if (selected) 3.dp else 0.dp, bounceSpring(), label = "ring")
    Box(
        Modifier
            .pressScale(0.9f)
            .size(44.dp)
            .then(if (ring > 0.dp) Modifier.border(ring, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f), CircleShape) else Modifier)
            .padding(ring + 2.dp)
            .clip(CircleShape)
            .background(color)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = name },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) Icon(Icons.Filled.Check, name, Modifier.size(20.dp), tint = Color.White)
    }
}

/**
 * Any colour: a rainbow bar to tap or drag along. The knob shows the chosen hue; without one (an accent of the list is
 * in use) there is no knob.
 */
@Composable
private fun HueBar(hue: Float?, onHue: (Float) -> Unit) {
    // The same colours the custom accent will have, so the bar shows exactly what each point gives.
    val stops = remember { (0..24).map { customPalette((it * 15f).coerceAtMost(359.9f)).light } }
    GlassSlider(
        value = hue,
        onValueChange = onHue,
        valueRange = 0f..359.9f,
        track = Brush.horizontalGradient(stops),
        knobColor = hue?.let { customPalette(it).light } ?: Color.Transparent,
    )
}

package com.ce.kit.settings

import android.content.SharedPreferences
import com.ce.kit.ui.theme.Accent
import com.ce.kit.ui.theme.AccentPalette
import com.ce.kit.ui.theme.CeAppearance
import com.ce.kit.ui.theme.Skin
import com.ce.kit.ui.theme.ThemeMode
import com.ce.kit.ui.theme.ThemeStyle
import com.ce.kit.ui.theme.customPalette

/** The names under which an app stores the settings every Ce app has (each app keeps its current keys: nothing migrates). */
data class CommonKeys(
    val accent: String = "accent",
    val customHue: String = "accent_hue",
    val themeMode: String = "theme_mode",
    val themeStyle: String = "theme_style",
    val skin: String = "skin",
    val depth3d: String = "depth_3d",
    val transparency: String = "transparency",
    val checkUpdates: String = "check_updates",
)

/** The settings every Ce app has, read with the same validation everywhere (unknown names give the default; transparency 0 to 100). */
data class CommonSettings(
    override val accent: Accent = Accent.INDIGO,
    override val customHue: Float? = null,
    override val themeMode: ThemeMode = ThemeMode.SYSTEM,
    override val themeStyle: ThemeStyle = ThemeStyle.AURORA,
    override val skin: Skin = Skin.GLASS,
    override val depth3d: Boolean = false,
    override val transparency: Int = 65,
    val checkUpdates: Boolean = true,
) : CeAppearance {
    override val accentPalette: AccentPalette get() = customHue?.let { customPalette(it) } ?: accent.palette

    companion object {
        /** Reads the common settings from [prefs] under [keys]. A hue below 0 means no custom colour. */
        fun read(prefs: SharedPreferences, keys: CommonKeys = CommonKeys()) = CommonSettings(
            accent = enumPref(prefs, keys.accent, Accent.INDIGO),
            customHue = prefs.getFloat(keys.customHue, -1f).takeIf { it >= 0f },
            themeMode = enumPref(prefs, keys.themeMode, ThemeMode.SYSTEM),
            themeStyle = enumPref(prefs, keys.themeStyle, ThemeStyle.AURORA),
            skin = enumPref(prefs, keys.skin, Skin.GLASS),
            depth3d = prefs.getBoolean(keys.depth3d, false),
            transparency = prefs.getInt(keys.transparency, 65).coerceIn(0, 100),
            checkUpdates = prefs.getBoolean(keys.checkUpdates, true),
        )

        private inline fun <reified E : Enum<E>> enumPref(prefs: SharedPreferences, key: String, default: E): E =
            prefs.getString(key, null)?.let { name -> enumValues<E>().firstOrNull { it.name == name } } ?: default
    }
}

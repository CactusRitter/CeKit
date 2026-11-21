package com.ce.kit.ui.theme

/**
 * The look settings [CeTheme] needs. Each app's own settings class implements it (`data class AppSettings(...) : CeAppearance`),
 * so the theme does not depend on any app.
 */
interface CeAppearance {
    val accent: Accent
    val customHue: Float?
    val themeMode: ThemeMode
    val themeStyle: ThemeStyle
    val skin: Skin
    /** Solid "3D" shadow under every glass surface. */
    val depth3d: Boolean
    /** 0 = solid glass, 100 = fully see-through. */
    val transparency: Int
    /** The app's colour in its four shades (the custom one, or the chosen accent). */
    val accentPalette: AccentPalette
}

/** The defaults of every Ce app (used by previews and as the starting point). */
object DefaultAppearance : CeAppearance {
    override val accent = Accent.INDIGO
    override val customHue: Float? = null
    override val themeMode = ThemeMode.SYSTEM
    override val themeStyle = ThemeStyle.AURORA
    override val skin = Skin.GLASS
    override val depth3d = false
    override val transparency = 65
    override val accentPalette = Accent.INDIGO.palette
}

package com.ce.kit.ui

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import com.ce.kit.ui.glass.pressScale
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Whether this colour scheme is a dark one (the look may be dark without the phone being dark: AMOLED). */
val ColorScheme.isDark: Boolean get() = background.luminance() < 0.5f

/**
 * Motion settings. Containers move on springs, so movements can be interrupted, keep their velocity
 * and settle naturally; fades stay short tweens. With [reduced] every movement becomes a short fade
 * or snaps.
 */
@Immutable
data class MotionConfig(val reduced: Boolean) {
    /** State changes inside a component (expand, resize, move, color). */
    fun <T> standard(): FiniteAnimationSpec<T> =
        if (reduced) snap() else spring(MotionTokens.STANDARD_DAMPING, MotionTokens.STANDARD_STIFFNESS)

    /** Containers appearing: a soft spring with a hint of overshoot. */
    fun <T> enter(): FiniteAnimationSpec<T> =
        if (reduced) tween(MotionTokens.SHORT, easing = FastOutSlowInEasing) else spring(MotionTokens.ENTER_DAMPING, MotionTokens.ENTER_STIFFNESS)

    /** Containers leaving: a stiffer spring without overshoot, so exits finish sooner than entries. */
    fun <T> exit(): FiniteAnimationSpec<T> =
        if (reduced) tween((MotionTokens.SHORT * MotionTokens.EXIT_FACTOR).toInt(), easing = MotionTokens.ExitEasing) else spring(Spring.DampingRatioNoBouncy, MotionTokens.EXIT_STIFFNESS)

    /** Panels following a gesture: quick and without overshoot, so no edge ever shows past its place. */
    fun <T> page(): FiniteAnimationSpec<T> =
        if (reduced) snap() else spring(Spring.DampingRatioNoBouncy, MotionTokens.PAGE_STIFFNESS)

    /** Sliding from one page to the next; a gesture (predictive back) can still scrub it. */
    fun <T> navigation(): FiniteAnimationSpec<T> =
        if (reduced) snap() else tween(MotionTokens.NAVIGATION, easing = FastOutSlowInEasing)

    fun <T> fade(durationMillis: Int = MotionTokens.SHORT): FiniteAnimationSpec<T> = tween(durationMillis)

    fun <T> sheetSpring(): FiniteAnimationSpec<T> =
        if (reduced) tween(MotionTokens.SHORT) else spring(MotionTokens.SHEET_DAMPING, MotionTokens.SHEET_STIFFNESS)

    fun <T> bouncy(): FiniteAnimationSpec<T> =
        if (reduced) snap() else spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)

    /** A section that opens in place. Reduced: a short fade, the space appears at once. */
    fun expandIn(): EnterTransition = if (reduced) fadeIn(tween(MotionTokens.SHORT)) else fadeIn(enter()) + expandVertically(enter())

    fun shrinkOut(): ExitTransition = if (reduced) fadeOut(tween(MotionTokens.SHORT)) else fadeOut(exit()) + shrinkVertically(exit())

    /** Floating content entering from below (or above with a negative [fraction]). Reduced: fade only. */
    fun riseIn(fraction: Int = 3): EnterTransition =
        if (reduced) fadeIn(tween(MotionTokens.SHORT)) else fadeIn(enter()) + slideInVertically(enter()) { it / fraction }

    fun sinkOut(fraction: Int = 3): ExitTransition =
        if (reduced) fadeOut(tween(MotionTokens.SHORT)) else fadeOut(exit()) + slideOutVertically(exit()) { it / fraction }

    /** Delay for the n-th item of a list that appears, capped so long lists do not drag. */
    fun staggerDelay(index: Int): Int = if (reduced) 0 else minOf(index, MotionTokens.STAGGER_MAX_ITEMS) * MotionTokens.STAGGER
}

val LocalMotion = staticCompositionLocalOf { MotionConfig(reduced = false) }

/**
 * Springs for everything that moves, durations and easings for fades. Entering containers spring
 * softly with a little overshoot; exits use a stiffer spring without overshoot.
 */
object MotionTokens {
    const val SHORT = 150
    const val MEDIUM = 220
    const val LONG = 300
    const val EXIT_FACTOR = 0.65f
    const val STANDARD_DAMPING = 0.85f
    const val STANDARD_STIFFNESS = 450f
    const val ENTER_DAMPING = 0.78f
    const val ENTER_STIFFNESS = 320f
    const val EXIT_STIFFNESS = 700f

    /** The month's day list following the finger: fast, critically damped. */
    const val PAGE_STIFFNESS = 900f

    /**
     * Sliding between pages (home, settings): time-based and slow to start, so a heavy first frame
     * of the new page does not swallow the movement the way it would a front-loaded spring.
     */
    const val NAVIGATION = 400

    /** Resistance of the elastic overscroll (UIScrollView's constant): the further, the stiffer. */
    const val RUBBER_BAND_RESISTANCE = 0.55f
    const val RUBBER_BAND_STIFFNESS = 400f

    /** A fling bounces at most this share of its container and at most [RubberBandMaxBounce]. */
    const val RUBBER_BAND_MAX_BOUNCE_FRACTION = 0.2f
    val RubberBandMaxBounce = 120.dp

    /** How far a sheet pulled past fully open can stretch. */
    val SheetStretch = 48.dp

    /** A sheet let go at least this fast (per second) moves on to its next height or closes. */
    val SheetFlingVelocity = 500.dp

    /** Swipe up on the month this far, or flick at this speed per second, to open the day list. */
    val PanelSwipeThreshold = 56.dp
    val PanelSwipeVelocity = 900.dp
    const val STAGGER = 35
    const val STAGGER_MAX_ITEMS = 8
    const val SHEET_DAMPING = 0.86f
    const val SHEET_STIFFNESS = 380f
    const val PRESS_SCALE = 0.97f
    const val ZOOM_IN_SCALE = 0.92f
    const val ZOOM_OUT_SCALE = 1.06f

    val EnterEasing: Easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    val ExitEasing: Easing = CubicBezierEasing(0.4f, 0f, 1f, 1f)
}

/**
 * Keeps touches on a floating card from reaching what lies beneath it. Being hit, the card hides
 * them from the content underneath. Presses and releases that nothing inside the card took are
 * consumed after its children had them, so an ancestor such as a dialog's scrim does not read them
 * as a tap on itself. Moves stay untouched: consuming them would cancel buttons and drags inside.
 */
fun Modifier.blockTouchesBelow(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent(PointerEventPass.Main).changes.forEach { change ->
                if (change.pressed != change.previousPressed) change.consume()
            }
        }
    }
}

/** clickable + ripple + press scale; place it before background/clip so the whole element scales. */
@Composable
fun Modifier.springClickable(
    enabled: Boolean = true,
    role: Role? = null,
    onClickLabel: String? = null,
    shape: Shape? = null,
    pressedScale: Float = MotionTokens.PRESS_SCALE,
    onClick: () -> Unit,
): Modifier {
    return pressScale(pressedScale)
        .then(if (shape != null) Modifier.clip(shape) else Modifier)
        .clickable(
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick,
        )
}

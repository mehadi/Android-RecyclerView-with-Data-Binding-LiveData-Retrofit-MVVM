package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/** Animation duration tokens (ms), following the Material 3 motion scale. */
object MotionDuration {
    const val Short = 150
    const val Medium = 300
    const val Long = 450
    const val ExtraLong = 600
}

/** Material 3 easing curves. Prefer Standard for small/utility motion, Emphasized for hero motion. */
object MotionEasing {
    /** Symmetric ease-in-out for most transitions. */
    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Ease-in only — for elements leaving the screen. */
    val StandardAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 1f, 1f)

    /** Ease-out only — for elements entering the screen. */
    val StandardDecelerate: Easing = CubicBezierEasing(0f, 0f, 0f, 1f)

    /** Bolder curve reserved for large or attention-grabbing motion. */
    val Emphasized: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
}

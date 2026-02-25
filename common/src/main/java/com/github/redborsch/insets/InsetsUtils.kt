package com.github.redborsch.insets

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.IntDef
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.internal.EdgeToEdgeUtils

fun Activity.enableEdgeToEdge() {
    applyEdgeToEdgePatches(window)
}

@SuppressLint("RestrictedApi", "WrongConstant")
fun applyEdgeToEdgePatches(window: Window) {
    // WindowCompat.enableEdgeToEdge(window) - Has issues on different Android versions!
    EdgeToEdgeUtils.applyEdgeToEdge(window, true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isNavigationBarContrastEnforced = false
    }
}

private val insetsTypes
    get() = WindowInsetsCompat.Type.systemBars() or
            WindowInsetsCompat.Type.displayCutout() or
            WindowInsetsCompat.Type.mandatorySystemGestures()

@Retention(AnnotationRetention.SOURCE)
@IntDef(InsetLocation.LEFT, InsetLocation.TOP, InsetLocation.RIGHT, InsetLocation.BOTTOM)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.TYPE,
)
annotation class InsetLocation {
    companion object {
        const val LEFT = 1 shl 0
        const val TOP = 1 shl 1
        const val RIGHT = 1 shl 2
        const val BOTTOM = 1 shl 3

        /**
         * For convenience, to avoid prefixing each value. Can be used as follows:
         * ```
         * InsetLocation { LEFT + RIGHT }
         * ```
         */
        inline operator fun invoke(block: Companion.() -> Int): Int {
            return InsetLocation.block()
        }
    }
}


private fun @InsetLocation Int.apply(
    @InsetLocation flag: Int,
    valueIfSelected: Int,
    valueIfUnselected: Int = 0,
): Int =
    if (this and flag == flag) {
        valueIfSelected
    } else {
        valueIfUnselected
    }

fun View.applyInsetsAsPaddings(@InsetLocation locationFlags: Int) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(insetsTypes)

        v.updatePadding(
            left = locationFlags.apply(InsetLocation.LEFT, insets.left),
            top = locationFlags.apply(InsetLocation.TOP, insets.top),
            right = locationFlags.apply(InsetLocation.RIGHT, insets.right),
            bottom = locationFlags.apply(InsetLocation.BOTTOM, insets.bottom),
        )
        windowInsets
    }
}

fun View.applyInsetsAsMargins(@InsetLocation locationFlags: Int, keepExisting: Boolean = false) {
    val existing = if (keepExisting) {
        val existingMargins = layoutParams as ViewGroup.MarginLayoutParams
        existingMargins.run {
            Rect(leftMargin, topMargin, rightMargin, bottomMargin)
        }
    } else {
        Rect()
    }
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(insetsTypes)

        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = locationFlags.apply(InsetLocation.LEFT, existing.left + insets.left, leftMargin)
            topMargin = locationFlags.apply(InsetLocation.TOP, existing.top + insets.top, topMargin)
            rightMargin = locationFlags.apply(InsetLocation.RIGHT, existing.right + insets.right, rightMargin)
            bottomMargin = locationFlags.apply(InsetLocation.BOTTOM, existing.bottom + insets.bottom, bottomMargin)
        }

        windowInsets
    }
}

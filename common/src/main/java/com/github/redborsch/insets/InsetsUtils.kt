package com.github.redborsch.insets

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

fun View.applyInsetsAsPaddings(@InsetLocation locations: Int) {
    applyInsetsHandler(
        InsetsHandler(insetsTypes, PaddingStrategy(), locations),
    )
}

/**
 * The difference with [applyInsetsAsPaddings] is that this function takes into account that
 * bottom sheets potentially might have maximum width not occupy the entire screen width.
 * The paddings are applied only if the bottom sheet occupies the entire window (or screen).
 */
@SuppressLint("RestrictedApi")
fun View.applyBottomSheetPaddings() {
    applyInsetsHandler(
        NonFullScreenInsetsHandler(insetsTypes, PaddingStrategy()),
    )
}

fun View.applyInsetsAsMargins(@InsetLocation locations: Int, keepExisting: Boolean = false) {
    applyInsetsHandler(
        InsetsHandler(
            insetsTypes,
            MarginStrategy(this, keepExisting),
            locations,
        ),
    )
}

// This makes sense as we replace one function with another.
@Suppress("NOTHING_TO_INLINE")
private inline fun View.applyInsetsHandler(insetsHandler: AbstractInsetsHandler) {
    ViewCompat.setOnApplyWindowInsetsListener(this, insetsHandler)
}

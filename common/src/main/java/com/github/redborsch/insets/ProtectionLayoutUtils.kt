package com.github.redborsch.insets

import android.content.Context
import androidx.core.view.insets.ProtectionLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun ProtectionLayout.applyDefaults() {
    setProtections(getDefaultProtectionLayoutProtections(context))
}

internal fun getDefaultProtectionLayoutProtections(context: Context) = listOf(
    BottomSheetBehavior.getDefaultBottomGradientProtection(context)
)

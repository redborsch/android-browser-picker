package com.github.redborsch.insets

import androidx.core.view.insets.ProtectionLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun ProtectionLayout.applyDefaults() {
    setProtections(
        mutableListOf(
            BottomSheetBehavior.getDefaultBottomGradientProtection(context)
        )
    )
}

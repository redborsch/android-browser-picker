package com.github.redborsch.insets

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

internal interface InsetStrategy {
    fun applyInsets(view: View, @InsetLocation locations: Int, insets: Insets)
}

internal class PaddingStrategy : InsetStrategy {

    override fun applyInsets(view: View, locations: Int, insets: Insets) {
        view.updatePadding(
            left = locations.apply(InsetLocation.LEFT, insets.left),
            top = locations.apply(InsetLocation.TOP, insets.top),
            right = locations.apply(InsetLocation.RIGHT, insets.right),
            bottom = locations.apply(InsetLocation.BOTTOM, insets.bottom),
        )
    }
}

internal class MarginStrategy(
    view: View,
    keepExisting: Boolean,
) : InsetStrategy {

    private val existing: Rect = if (keepExisting) {
        val existingMargins = view.layoutParams as ViewGroup.MarginLayoutParams
        existingMargins.run {
            Rect(leftMargin, topMargin, rightMargin, bottomMargin)
        }
    } else {
        Rect()
    }

    override fun applyInsets(view: View, locations: Int, insets: Insets) {
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = locations.apply(InsetLocation.LEFT, existing.left + insets.left, leftMargin)
            topMargin = locations.apply(InsetLocation.TOP, existing.top + insets.top, topMargin)
            rightMargin = locations.apply(InsetLocation.RIGHT, existing.right + insets.right, rightMargin)
            bottomMargin = locations.apply(InsetLocation.BOTTOM, existing.bottom + insets.bottom, bottomMargin)
        }
    }
}

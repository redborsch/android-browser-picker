package com.github.redborsch.insets

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.github.redborsch.insets.InsetLocation.Companion.BOTTOM
import com.github.redborsch.insets.InsetLocation.Companion.LEFT
import com.github.redborsch.insets.InsetLocation.Companion.RIGHT
import com.github.redborsch.insets.InsetLocation.Companion.TOP

internal interface InsetStrategy {
    fun applyInsets(view: View, @InsetLocation locations: Int, insets: Insets)
}

internal class PaddingStrategy : InsetStrategy {

    override fun applyInsets(view: View, locations: Int, insets: Insets) {
        with(view) {
            updatePadding(
                left = locations.apply(LEFT, insets.left, paddingLeft),
                top = locations.apply(TOP, insets.top, paddingTop),
                right = locations.apply(RIGHT, insets.right, paddingRight),
                bottom = locations.apply(BOTTOM, insets.bottom, paddingBottom),
            )
        }
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
            leftMargin = locations.apply(LEFT, existing.left + insets.left, leftMargin)
            topMargin = locations.apply(TOP, existing.top + insets.top, topMargin)
            rightMargin = locations.apply(RIGHT, existing.right + insets.right, rightMargin)
            bottomMargin = locations.apply(BOTTOM, existing.bottom + insets.bottom, bottomMargin)
        }
    }
}

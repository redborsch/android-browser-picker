package com.github.redborsch.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.SeekBarPreference
import kotlin.math.roundToInt

/**
 * [SeekBarPreference] that allows to use dimension resources as default values.
 * Useful for preferences having defaults in density-independent pixel (DPs).
 */
class DimensionPreference : SeekBarPreference {
    @Suppress("unused")
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    @Suppress("unused")
    constructor(context: Context) : super(context)

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getDimension(index, 0f).roundToInt()
    }
}

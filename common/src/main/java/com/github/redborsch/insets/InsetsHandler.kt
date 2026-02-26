package com.github.redborsch.insets

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import com.google.android.material.internal.WindowUtils

internal abstract class AbstractInsetsHandler(
    @param:InsetsType
    private val typeMask: Int,
    private val strategy: InsetStrategy,
) : OnApplyWindowInsetsListener {

    override fun onApplyWindowInsets(v: View, windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        val locations = provideLocations(v)
        val insets = windowInsets.getInsets(typeMask)
        strategy.applyInsets(v, locations, insets)
        return windowInsets
    }

    @InsetLocation
    protected abstract fun provideLocations(v: View): Int
}

internal class InsetsHandler(
    @InsetsType
    typeMask: Int,
    strategy: InsetStrategy,
    @param:InsetLocation
    private val locations: Int,
) : AbstractInsetsHandler(typeMask, strategy) {

    override fun provideLocations(v: View): Int = locations
}

internal class NonFullScreenInsetsHandler(
    @InsetsType
    typeMask: Int,
    strategy: InsetStrategy,
) : AbstractInsetsHandler(typeMask, strategy) {

    @SuppressLint("RestrictedApi")
    override fun provideLocations(v: View): Int {
        var locationFlags = InsetLocation.BOTTOM
        val bounds = WindowUtils.getCurrentWindowBounds(v.context)
        if (bounds.width() == v.width) {
            locationFlags += InsetLocation { LEFT + RIGHT }
        }
        return locationFlags
    }
}

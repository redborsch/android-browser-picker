package com.github.redborsch.browserpicker.data

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.shared.model.BrowserData
import kotlinx.coroutines.Job

class InternalBrowserData(
    id: String,
    @param:StringRes
    private val nameResId: Int,
    @param:DrawableRes
    private val iconResId: Int,
) : BrowserData {

    override val packageName: String = Globals.internalAction(id)

    override val isNonBrowserApplication: Boolean
        get() = false

    override fun getName(context: Context): CharSequence =
        context.getText(nameResId)

    override fun loadIcon(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        block: (Drawable?) -> Unit
    ): Job? {
        block(AppCompatResources.getDrawable(context, iconResId))
        return null
    }
}

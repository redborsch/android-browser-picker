package com.github.redborsch.browserpicker.data

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.shared.model.BrowserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    override suspend fun getName(context: Context): CharSequence = withContext(Dispatchers.Default) {
        context.getText(nameResId)
    }

    override suspend fun loadIcon(context: Context): Drawable? = withContext(Dispatchers.IO) {
        AppCompatResources.getDrawable(context, iconResId)
    }
}

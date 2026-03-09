package com.github.redborsch.browserpicker.shared.repository.installed

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import com.github.redborsch.browserpicker.shared.model.BrowserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ActivityInfoBrowserData(
    private val activityInfo: ActivityInfo,
    override val isNonBrowserApplication: Boolean,
) : BrowserData {

    override val packageName: String
        get() = activityInfo.packageName

    override suspend fun getName(context: Context): CharSequence = withContext(Dispatchers.Default) {
        activityInfo.loadLabel(context.packageManager)
    }

    override suspend fun loadIcon(context: Context): Drawable? = context.loadIcon()

    @JvmName("loadIconFromContext")
    private suspend fun Context.loadIcon() = withContext(Dispatchers.Default) {
        activityInfo.loadIcon(packageManager)
    }
}

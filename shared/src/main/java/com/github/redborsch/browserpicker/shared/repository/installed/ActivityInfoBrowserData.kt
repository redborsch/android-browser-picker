package com.github.redborsch.browserpicker.shared.repository.installed

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.LifecycleOwner
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.lifecycle.launchOnEachStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

internal class ActivityInfoBrowserData(
    packageManager: PackageManager,
    private val activityInfo: ActivityInfo,
    override val isNonBrowserApplication: Boolean,
) : BrowserData {

    private val name: CharSequence = activityInfo.loadLabel(packageManager)

    override val packageName: String
        get() = activityInfo.packageName

    override fun getName(context: Context): CharSequence = name

    override fun loadIcon(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        block: (Drawable?) -> Unit
    ): Job = lifecycleOwner.launchOnEachStart {
        block(context.loadIcon())
    }

    private suspend fun Context.loadIcon() = withContext(Dispatchers.Default) {
        activityInfo.loadIcon(packageManager)
    }
}

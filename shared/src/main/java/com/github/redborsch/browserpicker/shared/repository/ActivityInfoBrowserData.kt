package com.github.redborsch.browserpicker.shared.repository

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.github.redborsch.browserpicker.shared.model.BrowserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

internal class ActivityInfoBrowserData(
    private val packageManager: PackageManager,
    private val activityInfo: ActivityInfo,
) : BrowserData {

    @Volatile
    private var cachedIcon: Drawable? = null

    override val name: String by lazy {
        activityInfo.loadLabel(packageManager).toString()
    }
    override val icon: Flow<Drawable?>
        get() = cachedIcon?.let {
            flowOf(it)
        } ?: loadIconFlow().onEach {
            if (it != null) {
                cachedIcon = it
            }
        }
    override val packageName: String
        get() = activityInfo.packageName

    private fun loadIconFlow() = flow<Drawable?> {
        emit(
            activityInfo.loadIcon(packageManager)
        )
    }.flowOn(Dispatchers.Default)
}

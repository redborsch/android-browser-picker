package com.github.redborsch.browserpicker.shared.repository.installed

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import com.github.redborsch.browserpicker.shared.system.createViewIntent
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.model.BrowserListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstalledBrowserRepository(
    private val context: Context,
    private val packageToFilterOut: String? = null,
) : BrowserListRepository {

    override suspend fun queryBrowserList(uri: Uri): List<BrowserData> = withContext(
        Dispatchers.Default
    ) {
        val intent = createViewIntent(uri)
        val packageManager = context.packageManager
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .asSequence()
            .maybeFilterOutPackage()
            .map {
                ActivityInfoBrowserData(
                    packageManager,
                    it.activityInfo
                )
            }
            .toList()
    }

    private fun Sequence<ResolveInfo>.maybeFilterOutPackage() =
        if (packageToFilterOut != null) {
            filterNot {
                it.activityInfo.packageName == packageToFilterOut
            }
        } else {
            this
        }
}
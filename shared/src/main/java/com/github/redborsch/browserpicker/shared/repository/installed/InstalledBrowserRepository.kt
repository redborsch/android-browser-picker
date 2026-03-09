package com.github.redborsch.browserpicker.shared.repository.installed

import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.common.AbstractBrowserListRepository
import com.github.redborsch.browserpicker.shared.system.createViewIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias InstalledBrowserFilter = (ResolveInfo) -> Boolean

class InstalledBrowserRepository(
    private val context: Context,
    private val filters: List<InstalledBrowserFilter> = emptyList(),
) : AbstractBrowserListRepository() {

    override suspend fun queryIntermediate(uri: Uri): Sequence<BrowserData> = withContext(
        Dispatchers.Default
    ) {
        val intent = createViewIntent(uri)
        val packageManager = context.packageManager
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .asSequence()
            .applyFilters()
            .map {
                ActivityInfoBrowserData(
                    it.activityInfo,
                    it.isNonBrowserApp(),
                )
            }
    }

    private fun Sequence<ResolveInfo>.applyFilters(): Sequence<ResolveInfo> =
        if (filters.isNotEmpty()) {
            filters.fold(this) { result, filter ->
                result.filterNot(filter)
            }
        } else {
            this
        }

    companion object {
        fun filterOutPackage(packageToFilterOut: String): InstalledBrowserFilter = {
            it.activityInfo.packageName == packageToFilterOut
        }

        fun filterOutNonBrowserApps(): InstalledBrowserFilter = {
            it.isNonBrowserApp()
        }

        private fun ResolveInfo.isNonBrowserApp(): Boolean =
            match and IntentFilter.MATCH_CATEGORY_PATH == IntentFilter.MATCH_CATEGORY_PATH
    }
}

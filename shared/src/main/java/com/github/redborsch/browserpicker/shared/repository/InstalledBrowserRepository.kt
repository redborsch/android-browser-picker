package com.github.redborsch.browserpicker.shared.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import com.github.redborsch.browserpicker.shared.model.BrowserData

class InstalledBrowserRepository(
    private val context: Context,
    private val packageToFilterOut: String? = null,
) : BrowserListRepository {

    override suspend fun queryBrowserList(uri: Uri): List<BrowserData> {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = uri
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_ALL
        } else {
            0
        }
        val packageManager = context.packageManager
        return packageManager.queryIntentActivities(intent, flags)
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
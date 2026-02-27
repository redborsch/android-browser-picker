package com.github.redborsch.browserpicker.shared.system

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri

fun createViewIntent(uri: Uri): Intent = Intent(Intent.ACTION_VIEW).apply {
    data = uri
}

fun createViewIntent(uri: Uri, packageName: String): Intent = createViewIntent(uri).apply {
    setPackage(packageName)
}

fun isDefaultBrowser(context: Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.roleManager.isDefaultBrowser
    } else {
        queryDefaultBrowserPackageName(context) == context.packageName
    }

private fun queryDefaultBrowserPackageName(context: Context): String {
    val intent = createViewIntent("https://".toUri())
    val resolveInfo = context.packageManager.resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    return resolveInfo?.run { activityInfo.packageName } ?: "unknown"
}

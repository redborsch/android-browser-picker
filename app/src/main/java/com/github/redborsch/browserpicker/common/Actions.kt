package com.github.redborsch.browserpicker.common

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.github.redborsch.browserpicker.ChooserActivity
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings

fun Activity.tryChooser(customSettings: BrowserListSettings? = null) {
    startActivity(
        ChooserActivity.createIntent(this, customSettings = customSettings),
    )
}

fun Activity.closeChooser() {
    if (Settings.getInstance(this).keepInRecents) {
        finish()
    } else {
        finishAndRemoveTask()
    }
}

fun Intent.toSystemChooser(context: Context, callback: IntentSender? = null): Intent =
    Intent.createChooser(this, null, callback).apply {
        val excludedComponentNames = arrayOf(
            ComponentName(context.packageName, ChooserActivity::class.qualifiedName!!),
        )
        putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames)
    }

package com.github.redborsch.browserpicker.common

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.github.redborsch.browserpicker.ChooserActivity
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings

fun Activity.tryChooser(customSettings: BrowserListSettings? = null) {
    startActivity(
        ChooserActivity.createIntent(this, customSettings = customSettings),
    )
}

fun Intent.toSystemChooser(context: Context): Intent =
    Intent.createChooser(this, null).apply {
        val excludedComponentNames = arrayOf(
            ComponentName(context.packageName, ChooserActivity::class.qualifiedName!!),
        )
        putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames)
    }

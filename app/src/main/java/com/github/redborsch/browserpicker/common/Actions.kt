package com.github.redborsch.browserpicker.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.github.redborsch.browserpicker.ChooserActivity

fun createChooserIntent(
    context: Context,
    url: String = Settings.getInstance(context).testUrl,
): Intent =
    Intent(context, ChooserActivity::class.java).apply {
        action = Intent.ACTION_VIEW
        data = url.toUri()
    }

fun Activity.tryChooser() {
    // TODO pass custom browser list settings?
    startActivity(createChooserIntent(this))
}

package com.github.redborsch.browserpicker.data

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.common.toSystemChooser

class ShareActionHandler : AbstractSingleActionHandler(
    InternalBrowserData(
        Globals.internalAction("share"),
        R.string.internal_action_share,
        R.drawable.outline_share_24,
    )
) {

    override fun handle(fragment: Fragment, uri: Uri) {
        val context = fragment.context ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, uri.toString())
        }.toSystemChooser(context)
        fragment.startActivity(Intent.createChooser(intent,null))
    }
}

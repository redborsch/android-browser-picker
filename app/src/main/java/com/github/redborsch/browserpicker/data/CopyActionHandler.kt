package com.github.redborsch.browserpicker.data

import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.R
import com.github.redborsch.os.requireSystemService

class CopyActionHandler(id: String) : AbstractSingleActionHandler(
    InternalBrowserData(
        id,
        R.string.internal_action_copy,
        R.drawable.outline_content_copy_24,
    )
) {
    override fun handle(fragment: Fragment, uri: Uri) {
        val context = fragment.context ?: return
        val clipboard: ClipboardManager = context.requireSystemService()
        clipboard.setPrimaryClip(ClipData.newPlainText("", uri.toString()))

        Toast.makeText(context, R.string.toast_link_copied_to_clipboard, Toast.LENGTH_LONG).show()
    }
}

package com.github.redborsch.browserpicker.chooser

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.core.net.toUri
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.common.makeCopyWithoutComponent
import com.github.redborsch.browserpicker.shared.system.createViewIntent
import kotlinx.parcelize.Parcelize

interface BrowserIntentFactory : Parcelable {

    val uri: Uri

    fun createBrowserIntent(browserPackage: String): Intent
}

fun BrowserIntentFactory(intent: Intent, useOriginalIntent: Boolean): BrowserIntentFactory? =
    if (intent.action == Intent.ACTION_SEND) {
        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
        val uri = extractUriFromText(text) ?: return null
        OnlyUri(uri)
    } else {
        if (useOriginalIntent) {
            OriginalIntent(intent)
        } else {
            OnlyUri(intent.data ?: return null)
        }
    }

private fun extractUriFromText(text: String): Uri? {
    val result = Globals.urlFindRegex().find(text) ?: return null
    return runCatching {
        result.value.toUri()
    }.getOrNull()
}

@Parcelize
private class OnlyUri(
    override val uri: Uri,
) : BrowserIntentFactory {

    override fun createBrowserIntent(browserPackage: String): Intent =
        createViewIntent(uri, browserPackage)
}

@Parcelize
private class OriginalIntent private constructor(
    private val intent: Intent,
) : BrowserIntentFactory {

    override val uri: Uri
        get() = intent.data!!

    override fun createBrowserIntent(browserPackage: String): Intent {
        return Intent(intent).apply {
            // Reset selector to prevent crashes when setting a specific package
            selector = null
            setPackage(browserPackage)
        }
    }

    companion object {
        operator fun invoke(intent: Intent): OriginalIntent? =
            if (intent.data == null) {
                null
            } else {
                OriginalIntent(intent.makeCopyWithoutComponent())
            }
    }
}

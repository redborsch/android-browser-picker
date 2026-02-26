package com.github.redborsch.browserpicker.chooser

import android.content.Intent
import android.net.Uri
import com.github.redborsch.browserpicker.shared.system.createViewIntent

interface BrowserIntentFactory {

    val uri: Uri

    fun createBrowserIntent(browserPackage: String): Intent
}

fun BrowserIntentFactory(intent: Intent, useOriginalIntent: Boolean): BrowserIntentFactory? =
    if (useOriginalIntent) {
        OriginalIntent(Intent(intent))
    } else {
        intent.data?.let { OnlyUri(it) }
    }

private class OnlyUri(
    override val uri: Uri,
) : BrowserIntentFactory {

    override fun createBrowserIntent(browserPackage: String): Intent =
        createViewIntent(uri, browserPackage)
}

private class OriginalIntent(
    intent: Intent,
) : BrowserIntentFactory {

    private val intent = Intent(intent).apply {
        setComponent(null)
    }

    override val uri: Uri
        get() = intent.data!!

    override fun createBrowserIntent(browserPackage: String): Intent {
        return Intent(intent).apply {
            setPackage(browserPackage)
        }
    }
}

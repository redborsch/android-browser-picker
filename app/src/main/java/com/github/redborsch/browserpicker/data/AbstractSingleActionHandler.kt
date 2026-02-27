package com.github.redborsch.browserpicker.data

import android.net.Uri
import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.chooser.BrowserIntentFactory
import com.github.redborsch.browserpicker.shared.model.BrowserData

abstract class AbstractSingleActionHandler(
    override val browserData: BrowserData,
) : BrowserDataHandler {

    final override fun handles(browserData: BrowserData): Boolean {
        return this.browserData.packageName == browserData.packageName
    }

    final override fun handle(
        browserData: BrowserData,
        intentFactory: BrowserIntentFactory,
        fragment: Fragment
    ) {
        handle(fragment, intentFactory.uri)
    }

    protected abstract fun handle(fragment: Fragment, uri: Uri)
}

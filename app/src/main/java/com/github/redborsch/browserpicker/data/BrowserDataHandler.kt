package com.github.redborsch.browserpicker.data

import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.chooser.BrowserIntentFactory
import com.github.redborsch.browserpicker.shared.model.BrowserData

interface BrowserDataHandler {

    val browserData: BrowserData?

    fun handles(browserData: BrowserData): Boolean

    fun handle(
        browserData: BrowserData,
        intentFactory: BrowserIntentFactory,
        fragment: Fragment,
    )
}

package com.github.redborsch.browserpicker.chooser

import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.data.BrowserDataHandler
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.ui.OnBrowserSelectedListener

class BrowserSelectedListener(
    private val handlers: List<BrowserDataHandler>,
    private val intentFactory: BrowserIntentFactory,
    private val fragment: Fragment,
) : OnBrowserSelectedListener {

    override fun onBrowserSelected(browserData: BrowserData) {
        handlers.firstOrNull {
            it.handles(browserData)
        }?.handle(browserData, intentFactory, fragment)
    }
}

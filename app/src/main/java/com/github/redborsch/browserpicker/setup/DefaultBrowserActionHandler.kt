package com.github.redborsch.browserpicker.setup

import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.shared.system.browser.DefaultBrowserAction
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog

class DefaultBrowserActionHandler(
    private val fragment: Fragment,
) {

    private val defaultBrowserAction = DefaultBrowserAction(fragment).apply {
        onSettingsLaunchFailed {
            fragment.childFragmentManager.showDialog(fragment.defaultFragmentTag) {
                OpenSettingsErrorDialogFragment()
            }
        }
    }

    fun launchDefaultBrowserSettings(force: Boolean = false) {
        defaultBrowserAction.launchDefaultBrowserSettings(force)
    }
}

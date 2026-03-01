package com.github.redborsch.browserpicker.shared.system.browser

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.redborsch.log.getLogger

/**
 * Open default apps settings and allow the user to choose the browser.
 */
internal class DefaultStrategy(
    private val fragment: Fragment,
) : DefaultBrowserActionStrategy {

    private val log = getLogger()

    private var onSettingsLaunchFailed: OnSettingsLaunchFailed? = null

    override fun launchDefaultBrowserSettings(force: Boolean) {
        // The following logic was adapted from Firefox Focus: https://github.com/mozilla-mobile/focus-android
        // Not sure directing specifically to the browser settings (still) actually still works...
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
            putExtra(
                SETTINGS_SELECT_OPTION_KEY,
                DEFAULT_BROWSER_APP_OPTION,
            )
            putExtra(
                SETTINGS_SHOW_FRAGMENT_ARGS,
                bundleOf(SETTINGS_SELECT_OPTION_KEY to DEFAULT_BROWSER_APP_OPTION),
            )
        }

        try {
            fragment.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            log.e(e) { "Error opening settings" }
            onSettingsLaunchFailed?.invoke(e)
        }
    }

    override fun onSettingsLaunchFailed(block: OnSettingsLaunchFailed) {
        onSettingsLaunchFailed = block
    }

    companion object {
        private const val SETTINGS_SELECT_OPTION_KEY = ":settings:fragment_args_key"
        private const val SETTINGS_SHOW_FRAGMENT_ARGS = ":settings:show_fragment_args"
        private const val DEFAULT_BROWSER_APP_OPTION = "default_browser"
    }
}
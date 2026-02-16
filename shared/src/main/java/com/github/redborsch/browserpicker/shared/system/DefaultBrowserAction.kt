package com.github.redborsch.browserpicker.shared.system

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

class DefaultBrowserAction(
    strategy: DefaultBrowserActionStrategy,
) : DefaultBrowserActionStrategy by strategy

fun DefaultBrowserAction(fragment: Fragment): DefaultBrowserAction {
    var strategy: DefaultBrowserActionStrategy = DefaultStrategy(fragment)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        strategy = RoleManagerStrategy(fragment, strategy)
    }
    return DefaultBrowserAction(strategy)
}

typealias OnBrowserRequestSucceeded = () -> Unit
typealias OnSettingsLaunchFailed = (e: ActivityNotFoundException) -> Unit

interface DefaultBrowserActionStrategy {
    fun launchDefaultBrowserSettings()
    fun onBrowserRequestSucceeded(block: OnBrowserRequestSucceeded)
    fun onSettingsLaunchFailed(block: OnSettingsLaunchFailed)
}

/**
 * Open default apps settings and allow the user to choose the browser.
 */
private class DefaultStrategy(
    private val fragment: Fragment,
) : DefaultBrowserActionStrategy {

    private var onSettingsLaunchFailed: OnSettingsLaunchFailed? = null

    override fun launchDefaultBrowserSettings() {
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
            onSettingsLaunchFailed?.invoke(e)
        }
    }

    override fun onBrowserRequestSucceeded(block: () -> Unit) {
        // We cannot determine in this strategy whether the request has succeeded
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

@RequiresApi(Build.VERSION_CODES.Q)
private class RoleManagerStrategy(
    private val fragment: Fragment,
    private val backupStrategy: DefaultBrowserActionStrategy,
) : DefaultBrowserActionStrategy {

    private val defaultBrowserRequest = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_CANCELED) {
            val hasAppeared = appearanceMonitor.consumeHasAppeared()
            if (!hasAppeared) {
                // Role Manager UI was not shown, which indicates that the user has probably checked
                // "Don't ask again" checkbox. However, as we are taking an explicit action here -
                // let's open the default apps settings and allow the user to choose the browser.
                backupStrategy.launchDefaultBrowserSettings()
            }
        } else {
            onBrowserRequestSucceeded?.invoke()
        }
    }

    private var onBrowserRequestSucceeded: OnBrowserRequestSucceeded? = null

    private val appearanceMonitor = RoleManagerAppearanceMonitor()

    override fun launchDefaultBrowserSettings() {
        val context = fragment.context ?: return
        appearanceMonitor.startMonitoring()
        defaultBrowserRequest.launch(context.roleManager.createBrowserRoleIntent())
    }

    override fun onBrowserRequestSucceeded(block: () -> Unit) {
        onBrowserRequestSucceeded = block
    }

    override fun onSettingsLaunchFailed(block: OnSettingsLaunchFailed) {
        // Role Manager should, by idea, be always available, so we just protect the default
        // settings launch.
        backupStrategy.onSettingsLaunchFailed(block)
    }

    /**
     * Monitors on demand whether a Role Manager dialog has appeared on the screen. Essentially -
     * how long it takes before we receive the Activity result. If it's below some threshold -
     * we can be quite sure the Role Manager dialog has not appeared.
     */
    private class RoleManagerAppearanceMonitor {

        private var isMonitoring = false
        private var start: Long = 0

        fun startMonitoring() {
            isMonitoring = true
            start = SystemClock.uptimeMillis()
        }

        fun consumeHasAppeared(): Boolean {
            if (!isMonitoring) {
                return false
            }
            isMonitoring = false
            val elapsed = SystemClock.uptimeMillis() - start
            return elapsed > THRESHOLD_MS
        }

        companion object {
            /**
             * Some small threshold below which it would be physically too fast for the user
             * to dismiss a dialog.
             */
            private const val THRESHOLD_MS = 200
        }
    }
}

package com.github.redborsch.browserpicker.shared.system.browser

import android.app.Activity
import android.os.Build
import android.os.SystemClock
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.shared.system.createBrowserRoleIntent
import com.github.redborsch.browserpicker.shared.system.roleManager
import com.github.redborsch.log.getLogger

@RequiresApi(Build.VERSION_CODES.Q)
internal class RoleManagerStrategy(
    private val fragment: Fragment,
    private val backupStrategy: DefaultBrowserActionStrategy,
) : DefaultBrowserActionStrategy {

    private val log = getLogger()

    private val defaultBrowserRequest = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        log.v { "Activity result: $it" }
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
        log.v { "Requested browser role" }
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
package com.github.redborsch.browserpicker.shared.system.browser

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.github.redborsch.browserpicker.shared.system.createBrowserRoleIntent
import com.github.redborsch.browserpicker.shared.system.roleManager
import com.github.redborsch.log.getLogger

/**
 * Invisible fragment for [RoleManagerStrategy]. It handles Activity results in a robust way so
 * that even when our Activity is destroyed - we still handle the results and internal state
 * (forced default browser request) properly.
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal class RoleManagerStrategyFragment : Fragment() {

    private val log = getLogger()

    private val appearanceMonitor = RoleManagerAppearanceMonitor()

    private val defaultBrowserRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        log.v { "Activity result: $it, forcedRequest: $forcedRequest" }
        if (it.resultCode == Activity.RESULT_CANCELED) {
            val hasAppeared = appearanceMonitor.consumeHasAppeared()
            if (!hasAppeared) {
                // Role Manager UI was not shown, which indicates that the user has probably checked
                // "Don't ask again" checkbox. However, as we are taking an explicit action here -
                // let's open the default apps settings and allow the user to choose the browser.
                backupStrategy?.launchDefaultBrowserSettings(false)
            }
        } else if (forcedRequest) {
            backupStrategy?.launchDefaultBrowserSettings(true)
        } else {
            notifySucceeded()
        }
    }

    private var forcedRequest: Boolean = false

    var backupStrategy: DefaultBrowserActionStrategy? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log.v { "onCreate $this, savedInstanceState = $savedInstanceState" }

        if (savedInstanceState != null) {
            forcedRequest = savedInstanceState.getBoolean(keyForcedRequest)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(keyForcedRequest, forcedRequest)
    }

    fun launchDefaultBrowserSettings(force: Boolean) {
        val context = context ?: return
        forcedRequest = force
        appearanceMonitor.startMonitoring()
        defaultBrowserRequest.launch(context.roleManager.createBrowserRoleIntent())
        log.v { "Requested browser role" }
    }

    private fun notifySucceeded() {
        fragmentManagerToUse.setFragmentResult(keyOnSuccessResult, Bundle.EMPTY)
    }

    companion object {

        private val prefix get() = RoleManagerStrategyFragment::class.qualifiedName

        private val keyForcedRequest get() =  "$prefix.ForcedRequest"

        private val keyOnSuccessResult get() = "$prefix.OnSuccess"

        private val tag get() = "$prefix.TAG"

        /**
         * Using parent fragment manager to allow reusing the [RoleManagerStrategyFragment]
         * instance across different fragments.
         */
        private val Fragment.fragmentManagerToUse get() = parentFragmentManager

        fun getOrCreateInstance(fragment: Fragment): RoleManagerStrategyFragment {
            val log = getLogger<RoleManagerStrategyFragment>()
            val fm = fragment.fragmentManagerToUse
            var helperFragment = fm.findFragmentByTag(tag) as? RoleManagerStrategyFragment
            log.v { "Existing fragment: $helperFragment" }
            if (helperFragment == null) {
                helperFragment = RoleManagerStrategyFragment()
                fm.commit {
                    add(helperFragment, tag)
                }
            }
            fm.clearFragmentResultListener(keyOnSuccessResult)
            fm.setFragmentResultListener( keyOnSuccessResult, fragment) { _, _ ->
                log.v { "Delivering onSuccess to the fragment" }
                (fragment as? BrowserRequestSucceededListener)?.onBrowserRequestSucceeded()
            }
            return helperFragment
        }
    }
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

package com.github.redborsch.browserpicker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.binding.setContentView
import com.github.redborsch.browserpicker.chooser.BrowserChooserViewModel
import com.github.redborsch.browserpicker.chooser.BrowserIntentFactory
import com.github.redborsch.browserpicker.chooser.ChooserActivityPresenter
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.common.closeChooser
import com.github.redborsch.browserpicker.common.makeCopyWithoutComponent
import com.github.redborsch.browserpicker.common.toSystemChooser
import com.github.redborsch.browserpicker.databinding.ActivityChooserBinding
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.insets.enableEdgeToEdge
import com.github.redborsch.log.dumpForLog
import com.github.redborsch.log.getLogger

class ChooserActivity : FragmentActivity() {

    private val log = getLogger()

    private val viewModel: BrowserChooserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityChooserBinding.inflate(layoutInflater)
        setContentView(binding)
        val presenter = ChooserActivityPresenter(this, binding).apply {
            updateUI()
        }

        addOnMultiWindowModeChangedListener {
            log.d { "Multi window mode changed: $it" }

            presenter.updateUI()
        }

        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // Update intent for the case if this Activity gets killed and recreated
        this.intent = intent

        processIntent(intent)
    }

    private fun processIntent(newIntent: Intent?) {
        var intent = newIntent ?: return

        log.d { "Activity intent: ${intent.dumpForLog()}" }

        if (processInternalIntent(newIntent)) {
            return
        }

        log.d { "Processing normally" }

        intent = maybeConsumeCustomSettings(intent)

        val browserIntentFactory = BrowserIntentFactory(
            intent,
            viewModel.settings.useOriginalIntent,
        ) ?: run {
            maybeReshare(intent)
            return
        }
        viewModel.setBrowserIntentFactory(browserIntentFactory)
    }

    private fun processInternalIntent(intent: Intent): Boolean {
        if (intent.action?.startsWith(ACTION_INTERNAL_PREFIX) != true) {
            return false
        }
        log.d { "Internal action: ${intent.action}" }
        if (intent.action == ACTION_INTERNAL_AUTO_CLOSE) {
            log.d { "Finishing..." }
            closeChooser()
        }
        return true
    }

    private fun maybeConsumeCustomSettings(intent: Intent): Intent {
        return if (intent.hasExtra(EXTRA_CUSTOM_SETTINGS)) {
            viewModel.customBrowserListSettings = BrowserListSettings.deserialize(
                intent.getStringArrayExtra(EXTRA_CUSTOM_SETTINGS)
                    ?.toSet() ?: emptySet()
            )

            Intent(intent).apply {
                removeExtra(EXTRA_CUSTOM_SETTINGS)
            }
        } else {
            viewModel.customBrowserListSettings = null
            intent
        }
    }

    private fun maybeReshare(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return

        Toast.makeText(this, R.string.toast_send_intent_warning, Toast.LENGTH_LONG).show()

        val newShareIntent = intent
            .makeCopyWithoutComponent()
            .toSystemChooser(this)
        startActivity(newShareIntent)
        finishAndRemoveTask()
    }

    companion object {

        private val PREFIX get() = ChooserActivity::class.qualifiedName
        private val ACTION_INTERNAL_PREFIX get() = "$PREFIX.internal"

        private val ACTION_INTERNAL_AUTO_CLOSE = "$ACTION_INTERNAL_PREFIX.auto-close"

        private val EXTRA_CUSTOM_SETTINGS get() = "$PREFIX.CustomSettings"

        fun createIntent(
            context: Context,
            url: String = Settings.getInstance(context).testUrl,
            customSettings: BrowserListSettings? = null,
        ) = Intent(context, ChooserActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = url.toUri()
            if (customSettings != null) {
                putExtra(EXTRA_CUSTOM_SETTINGS, customSettings.serialize().toTypedArray())
            }
        }

        fun createAutoCloseIntent(context: Context): PendingIntent {
            val intent = Intent(context, ChooserActivity::class.java).apply {
                action = ACTION_INTERNAL_AUTO_CLOSE
            }
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_ONE_SHOT)
        }
    }
}

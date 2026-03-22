package com.github.redborsch.browserpicker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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

    private lateinit var presenter: ChooserActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityChooserBinding.inflate(layoutInflater)
        setContentView(binding)
        presenter = ChooserActivityPresenter(this, binding).apply {
            updateUI()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        presenter.updateUI()
    }

    private fun maybeConsumeCustomSettings(intent: Intent): Intent {
        return if (intent.hasExtra(EXTRA_CUSTOM_SETTINGS)) {
            viewModel.browserListSettings = BrowserListSettings.deserialize(
                intent.getStringArrayExtra(EXTRA_CUSTOM_SETTINGS)
                    ?.toSet() ?: emptySet()
            )

            Intent(intent).apply {
                removeExtra(EXTRA_CUSTOM_SETTINGS)
            }
        } else {
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
        finish()
    }

    companion object {

        private val EXTRA_CUSTOM_SETTINGS get() = ChooserActivity::class.qualifiedName + ".CustomSettings"

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
    }
}

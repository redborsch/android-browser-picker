package com.github.redborsch.browserpicker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.chooser.BrowserChooserBottomSheetFragment
import com.github.redborsch.browserpicker.chooser.BrowserIntentFactory
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog
import com.github.redborsch.insets.enableEdgeToEdge
import com.github.redborsch.log.dumpForLog
import com.github.redborsch.log.getLogger

class ChooserActivity : FragmentActivity() {

    private val log = getLogger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        val intent = intent ?: return

        log.d { "Activity intent: ${intent.dumpForLog()}" }

        val browserIntentFactory = BrowserIntentFactory(
            intent,
            Settings.getInstance(this).useOriginalIntent,
        ) ?: run {
            maybeReshare(intent)
            return
        }

        supportFragmentManager.showDialog(defaultFragmentTag, forceReplace = true) {
            BrowserChooserBottomSheetFragment.newInstance(browserIntentFactory)
        }
    }

    private fun maybeReshare(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return

        Toast.makeText(this, R.string.send_intent_warning, Toast.LENGTH_LONG).show()

        val newShareIntent = Intent(intent).apply {
            setComponent(null)
        }
        startActivity(newShareIntent)
        finishAndRemoveTask()
    }
}

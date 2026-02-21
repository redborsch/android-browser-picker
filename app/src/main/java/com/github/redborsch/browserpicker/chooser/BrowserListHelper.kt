package com.github.redborsch.browserpicker.chooser

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.system.createViewIntent
import com.github.redborsch.browserpicker.shared.ui.BrowserListAdapter
import com.github.redborsch.lifecycle.launchOnEachStart
import com.github.redborsch.log.getLogger

class BrowserListHelper(
    private val viewModel: BrowserChooserViewModel,
) {

    private val log = getLogger()

    fun setUp(
        fragment: Fragment,
        browserList: RecyclerView,
        lifecycleOwner: LifecycleOwner,
        uri: Uri,
    ) {
        viewModel.updateBrowserList(uri)

        val adapter = BrowserListAdapter(lifecycleOwner) {
            val intent = createViewIntent(uri, it.packageName)
            fragment.launchAndClose(intent, it)
        }
        browserList.setUp()
        browserList.adapter = adapter

        lifecycleOwner.updateBrowserList(adapter)
    }

    private fun RecyclerView.setUp() {
        layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false,
        )
    }

    private fun LifecycleOwner.updateBrowserList(
        adapter: BrowserListAdapter,
    ) = launchOnEachStart {
        viewModel.installedBrowsers.collect {
            adapter.browserList = it
        }
    }

    private fun Fragment.launchAndClose(intent: Intent, browserData: BrowserData) {
        val activity = activity ?: return
        activity.disablePendingTransitions()
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            log.e(e) { "Error opening $intent, browser $browserData" }
            Toast.makeText(
                activity,
                getString(R.string.open_error, browserData.name),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        activity.close()
    }

    private fun Activity.disablePendingTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun Activity.close() {
        if (Settings.getInstance(this).keepInRecents) {
            finish()
        } else {
            finishAndRemoveTask()
        }
    }
}
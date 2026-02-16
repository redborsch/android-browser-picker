package com.github.redborsch.browserpicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.browserpicker.shared.ui.BrowserListAdapter
import com.github.redborsch.browserpicker.shared.utils.lifecycle.launchOnEachStart

class BrowserChooserUiHelper(
    private val viewModel: BrowserChooserViewModel,
) {

    fun setUp(
        activity: Activity,
        binding: FragmentBrowserChooserBinding,
        lifecycleOwner: LifecycleOwner,
        uri: Uri,
    ) {
        viewModel.updateBrowserList(uri)

        val adapter = BrowserListAdapter(lifecycleOwner) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                setPackage(it.packageName)
            }
            activity.launchAndClose(intent)
        }
        with(binding) {
            link.text = uri.toString()
            browserList.layoutManager = LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false,
            )
            browserList.adapter = adapter
        }
        lifecycleOwner.listenToBrowserListChanges(adapter)
    }

    private fun LifecycleOwner.listenToBrowserListChanges(
        adapter: BrowserListAdapter,
    ) = launchOnEachStart {
        viewModel.installedBrowsers.collect {
            adapter.browserList = it
        }
    }

    private fun Activity.launchAndClose(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
        startActivity(intent)
        finish()
        // TODO make optional
        // finishAndRemoveTask()
    }
}
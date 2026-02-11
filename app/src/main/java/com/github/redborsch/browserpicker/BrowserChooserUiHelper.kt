package com.github.redborsch.browserpicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.browserpicker.shared.ui.BrowserListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BrowserChooserUiHelper(
    private val viewModel: BrowserChooserViewModel,
) {

    fun setUp(
        activity: Activity,
        binding: FragmentBrowserChooserBinding,
        coroutineScope: CoroutineScope,
        uri: Uri,
    ) {
        viewModel.updateBrowserList(uri)

        val adapter = BrowserListAdapter(coroutineScope) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                setPackage(it.packageName)
            }
            activity.launchAndClose(intent)
        }
        with(binding) {
            header.text = activity.getString(R.string.open_with, uri.toString())
            browserList.layoutManager = LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false,
            )
            browserList.adapter = adapter
        }
        coroutineScope.listenToBrowserListChanges(adapter)
    }

    private fun CoroutineScope.listenToBrowserListChanges(
        adapter: BrowserListAdapter,
    ) = launch {
        viewModel.installedBrowsers.collect {
            adapter.browserList = it
        }
    }

    private fun Activity.launchAndClose(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            overridePendingTransition(0, 0)
        }
        startActivity(intent)
        finishAndRemoveTask()
    }
}
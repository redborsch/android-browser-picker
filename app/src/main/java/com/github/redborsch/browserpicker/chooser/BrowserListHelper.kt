package com.github.redborsch.browserpicker.chooser

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.shared.ui.BrowserListAdapter
import com.github.redborsch.lifecycle.launchOnEachStart
import com.github.redborsch.recyclerview.setVerticalLinearLayoutManager

class BrowserListHelper(
    private val viewModel: BrowserChooserViewModel,
    fragment: Fragment,
    lifecycleOwner: LifecycleOwner,
    browserList: RecyclerView,
    private val linkView: TextView,
) {

    private val adapter = BrowserListAdapter(lifecycleOwner)

    init {
        applySettings(viewModel.settings)

        browserList.setVerticalLinearLayoutManager()
        browserList.adapter = adapter

        with(lifecycleOwner) {
            updateIntentFactory(fragment)
            updateBrowserList(adapter)
        }
    }

    private fun LifecycleOwner.updateIntentFactory(fragment: Fragment) {
        launchOnEachStart {
            viewModel.browserIntentFactory.collect { intentFactory ->
                linkView.text = intentFactory.uri.toString()
                adapter.onBrowserSelectedListener = createAdapterListener(fragment, intentFactory)
            }
        }
    }

    private fun applySettings(settings: Settings) {
        if (settings.truncateLink) {
            linkView.maxLines = settings.maxLinkLines
        }
    }

    private fun createAdapterListener(
        fragment: Fragment,
        intentFactory: BrowserIntentFactory,
    ) = BrowserSelectedListener(
        viewModel.handlers,
        intentFactory,
        fragment,
    )

    private fun LifecycleOwner.updateBrowserList(
        adapter: BrowserListAdapter,
    ) = launchOnEachStart {
        viewModel.uriActions.collect {
            adapter.browserList = it
        }
    }
}
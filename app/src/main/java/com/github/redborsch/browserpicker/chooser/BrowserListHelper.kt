package com.github.redborsch.browserpicker.chooser

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.ui.BrowserListAdapter
import com.github.redborsch.lifecycle.launchOnEachStart
import com.github.redborsch.recyclerview.setVerticalLinearLayoutManager

class BrowserListHelper(
    private val viewModel: BrowserChooserViewModel,
) {

    fun setUp(
        fragment: Fragment,
        browserList: RecyclerView,
        lifecycleOwner: LifecycleOwner,
        intentFactory: BrowserIntentFactory,
    ) {
        viewModel.uri = intentFactory.uri

        val adapter = BrowserListAdapter(
            lifecycleOwner,
            createAdapterListener(fragment, intentFactory),
        )
        browserList.setVerticalLinearLayoutManager()
        browserList.adapter = adapter

        lifecycleOwner.updateBrowserList(adapter)
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
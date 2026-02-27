package com.github.redborsch.browserpicker.customizer

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.browserpicker.shared.repository.SettingsEntry
import com.github.redborsch.lifecycle.launchOnEachStart
import com.github.redborsch.recyclerview.setVerticalLinearLayoutManager

class CustomizerListHelper(
    private val viewModel: CustomizerViewModel,
    private val lifecycleOwner: LifecycleOwner,
) {

    private val adapter = CustomizerAdapter(lifecycleOwner)

    fun setUp(list: RecyclerView) {
        list.setVerticalLinearLayoutManager()
        list.adapter = adapter

        lifecycleOwner.updateBrowserList()
    }

    fun collectSettings(): BrowserListSettings {
        val items = adapter.items
        val builder = BrowserListSettings.Builder(items.size)
        adapter.items.forEachIndexed { index, data ->
            builder.addEntry(
                SettingsEntry(
                    data.browserData.packageName,
                    data.isVisible,
                    index,
                )
            )
        }
        return builder.build()
    }

    private fun LifecycleOwner.updateBrowserList() = launchOnEachStart {
        viewModel.items.collect {
            adapter.items = it
        }
    }
}

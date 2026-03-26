package com.github.redborsch.browserpicker.customizer

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.customizer.model.CustomizerViewModel
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

    private fun LifecycleOwner.updateBrowserList() = launchOnEachStart {
        viewModel.data.collect {
            adapter.data = it
        }
    }
}

package com.github.redborsch.browserpicker.customizer

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.lifecycle.launchOnEachStart
import com.github.redborsch.recyclerview.setVerticalLinearLayoutManager

class CustomizerListHelper(
    private val viewModel: CustomizerViewModel,
) {

    fun setUp(
        list: RecyclerView,
        lifecycleOwner: LifecycleOwner,
    ) {
        val adapter = CustomizerAdapter(lifecycleOwner)
        list.setVerticalLinearLayoutManager()
        list.adapter = adapter

        lifecycleOwner.updateBrowserList(adapter)
    }

    private fun LifecycleOwner.updateBrowserList(
        adapter: CustomizerAdapter,
    ) = launchOnEachStart {
        viewModel.installedBrowsers.collect {
            adapter.items = it
        }
    }
/*
    private fun RecyclerView.fixHidingAppBarWhenNotScrolling(
        lifecycleOwner: LifecycleOwner,
        adapter: RecyclerView.Adapter<*>,
    ) {
        log.d { "Installing fixHidingAppBarWhenNotScrolling patch" }
        val list = this
        // Disable until we know for sure we'd need it.
        list.isNestedScrollingEnabled = false

        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    log.d { "Adapter.onChanged, itemCount = ${adapter.itemCount}" }
                    if (adapter.itemCount > 0) {
                        NestedScrollEnableCallback(list, lifecycleOwner, log)

                        adapter.unregisterAdapterDataObserver(this)
                    }
                }
            }
        )
    }
 */
}

/*
private class NestedScrollEnableCallback(
    private val list: RecyclerView,
    private val lifecycleOwner: LifecycleOwner,
    private val log: Logger,
) : Runnable, LifecycleEventObserver {

    init {
        list.post(this)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun run() {
        val range = list.computeVerticalScrollRange()
        // Should in fact exclude AppBar height as it will disable scrolling also in case
        // RecyclerView has just enough space to fit the items. However, a part of the RecyclerView
        // is being hidden from the view, and it should be possible to drag it up.
        val height = list.height
        list.isNestedScrollingEnabled = range > height
        log.d { "RecyclerView.post, range: $range, height $height -> " +
                "isNestedScrollingEnabled = ${list.isNestedScrollingEnabled}" }
        if (range == 0) {
            list.post(this)
        } else {
            lifecycleOwner.lifecycle.removeObserver(this)
            log.d { "Lifecycle observer removed, all done" }
        }
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        log.d { "Lifecycle.onStateChanged $event" }
        when (event) {
            Lifecycle.Event.ON_STOP -> list.removeCallbacks(this)
            Lifecycle.Event.ON_DESTROY -> lifecycleOwner.lifecycle.removeObserver(this)
            else -> {}
        }
    }
}
*/

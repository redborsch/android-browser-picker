package com.github.redborsch.browserpicker.shared.ui

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.databinding.ItemBrowserEntryBinding
import com.github.redborsch.browserpicker.shared.model.BrowserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlin.time.Duration.Companion.milliseconds

class BrowserEntryViewHolder(
    private val binding: ItemBrowserEntryBinding,
    private val onBrowserSelectedListener: OnBrowserSelectedListener?,
) : RecyclerView.ViewHolder(binding.root) {

    private var lastJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var browserData: BrowserData? = null

    init {
        if (onBrowserSelectedListener != null) {
            binding.root.setOnClickListener {
                browserData?.let {
                    onBrowserSelectedListener.onBrowserSelected(it)
                }
            }
        }
    }

    fun bind(browserData: BrowserData, lifecycleOwner: LifecycleOwner) {
        this.browserData = browserData

        val context = binding.root.context

        binding.browserIcon.setImageDrawable(null)
        lastJob = lifecycleOwner.lifecycleScope.launch {
            asyncLoadBrowserName(context, browserData).start()
            asyncLoadBrowserIcon(context, browserData).start()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.asyncLoadBrowserName(context: Context, browserData: BrowserData) = async {
        val browserNameAsync = async {
            browserData.getName(context)
        }
        binding.browserName.text = select {
            onTimeout(10.milliseconds) {
                browserData.packageName
            }
            browserNameAsync.onAwait {
                it
            }
        }
        binding.browserName.text = browserNameAsync.await()
    }

    private fun CoroutineScope.asyncLoadBrowserIcon(context: Context, browserData: BrowserData) = async {
        binding.browserIcon.setImageDrawable(browserData.loadIcon(context))
    }

    fun recycle() {
        browserData = null
        binding.browserIcon.setImageDrawable(null)
        lastJob = null
    }
}

package com.github.redborsch.browserpicker.shared.ui

import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.databinding.ViewHolderBrowserItemBinding
import com.github.redborsch.browserpicker.shared.model.BrowserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BrowserItemViewHolder(
    private val binding: ViewHolderBrowserItemBinding,
    private val onBrowserSelectedListener: OnBrowserSelectedListener,
) : RecyclerView.ViewHolder(binding.root) {

    private var lastJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var browserData: BrowserData? = null

    init {
        binding.root.setOnClickListener {
            browserData?.let {
                onBrowserSelectedListener.onBrowserSelected(it)
            }
        }
    }

    fun update(browserData: BrowserData, coroutineScope: CoroutineScope) {
        this.browserData = browserData

        binding.browserName.text = browserData.name
        binding.browserIcon.setImageDrawable(null)
        lastJob = coroutineScope.launch {
            browserData.icon.collect {
                binding.browserIcon.setImageDrawable(it)
            }
        }
    }

    fun recycle() {
        browserData = null
        binding.browserIcon.setImageDrawable(null)
        lastJob = null
    }
}
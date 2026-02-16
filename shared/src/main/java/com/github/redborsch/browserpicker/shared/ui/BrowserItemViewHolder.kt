package com.github.redborsch.browserpicker.shared.ui

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.databinding.ViewHolderBrowserItemBinding
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.utils.lifecycle.launchOnEachStart
import kotlinx.coroutines.Job

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

    fun update(browserData: BrowserData, lifecycleOwner: LifecycleOwner) {
        this.browserData = browserData

        binding.browserName.text = browserData.name
        binding.browserIcon.setImageDrawable(null)
        lastJob = lifecycleOwner.launchOnEachStart {
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
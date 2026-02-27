package com.github.redborsch.browserpicker.shared.ui

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.databinding.ItemBrowserEntryBinding
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.lifecycle.launchOnEachStart
import kotlinx.coroutines.Job

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

        binding.browserName.text = browserData.getName(context)
        binding.browserIcon.setImageDrawable(null)
        lastJob = browserData.loadIcon(context, lifecycleOwner) {
            binding.browserIcon.setImageDrawable(it)
        }
    }

    fun recycle() {
        browserData = null
        binding.browserIcon.setImageDrawable(null)
        lastJob = null
    }
}

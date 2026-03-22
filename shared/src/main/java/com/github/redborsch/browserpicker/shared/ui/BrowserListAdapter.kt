package com.github.redborsch.browserpicker.shared.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.databinding.ItemBrowserEntryBinding
import com.github.redborsch.browserpicker.shared.model.BrowserData

class BrowserListAdapter(
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<BrowserEntryViewHolder>() {

    var onBrowserSelectedListener: OnBrowserSelectedListener? = null

    private val browserSelectedListenerWrapper = OnBrowserSelectedListener {
        onBrowserSelectedListener?.onBrowserSelected(it)
    }

    var browserList: List<BrowserData> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowserEntryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBrowserEntryBinding.inflate(layoutInflater, parent, false)
        return BrowserEntryViewHolder(binding, browserSelectedListenerWrapper)
    }

    override fun getItemCount(): Int = browserList.size

    override fun onBindViewHolder(holder: BrowserEntryViewHolder, position: Int) =
        holder.bind(browserList[position], lifecycleOwner)

    override fun onViewRecycled(holder: BrowserEntryViewHolder) {
        holder.recycle()
    }
}
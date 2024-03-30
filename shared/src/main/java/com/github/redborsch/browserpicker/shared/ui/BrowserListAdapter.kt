package com.github.redborsch.browserpicker.shared.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.shared.databinding.ViewHolderBrowserItemBinding
import com.github.redborsch.browserpicker.shared.model.BrowserData
import kotlinx.coroutines.CoroutineScope

class BrowserListAdapter(
    private val coroutineScope: CoroutineScope,
    private val onBrowserSelectedListener: OnBrowserSelectedListener,
) : RecyclerView.Adapter<BrowserItemViewHolder>() {

    var browserList: List<BrowserData> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowserItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderBrowserItemBinding.inflate(layoutInflater, parent, false)
        return BrowserItemViewHolder(binding, onBrowserSelectedListener)
    }

    override fun getItemCount(): Int = browserList.size

    override fun onBindViewHolder(holder: BrowserItemViewHolder, position: Int) =
        holder.update(browserList[position], coroutineScope)

    override fun onViewRecycled(holder: BrowserItemViewHolder) {
        holder.recycle()
    }
}
package com.github.redborsch.browserpicker.customizer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.github.redborsch.browserpicker.databinding.ItemCustomizerEntryBinding
import com.github.redborsch.recyclerview.DragRecyclerViewAdapter

class CustomizerAdapter(
    private val lifecycleOwner: LifecycleOwner,
) : DragRecyclerViewAdapter<CustomizerViewHolder, CustomizerData>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomizerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCustomizerEntryBinding.inflate(layoutInflater, parent, false)
        return CustomizerViewHolder(binding, dragController)
    }

    override fun onBindViewHolder(
        holder: CustomizerViewHolder,
        position: Int
    ) {
        holder.bind(items[position], lifecycleOwner)
    }

    override fun onViewRecycled(holder: CustomizerViewHolder) {
        holder.recycle()
    }
}

package com.github.redborsch.browserpicker.customizer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.databinding.ItemCustomizerEntryBinding

class CustomizerAdapter(
    private val lifecycleOwner: LifecycleOwner,
//    private val browserListAdapter: BrowserListAdapter,
) : RecyclerView.Adapter<CustomizerViewHolder>() {

    private var list: ArrayList<CustomizerData> = ArrayList(0)

//    private var draggedViewHolder: RecyclerView.ViewHolder? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(value: List<CustomizerData>) {
        list = ArrayList(value)
        notifyDataSetChanged()
    }

    fun swap(fromPosition: Int, toPosition: Int) {
        if (list.exchange(fromPosition, toPosition)) {
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    fun cancelDrag() {
//        if (draggedViewHolder != null) {
//            (draggedViewHolder!!.itemView as MaterialCardView).setDragged(false)
//            draggedViewHolder = null
//        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomizerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCustomizerEntryBinding.inflate(layoutInflater, parent, false)
        return CustomizerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CustomizerViewHolder,
        position: Int
    ) {
        holder.bind(list[position], lifecycleOwner)
    }

    override fun onViewRecycled(holder: CustomizerViewHolder) {
        holder.recycle()
    }

    override fun getItemCount(): Int = list.size
}

private fun <E> MutableList<E>.exchange(fromPosition: Int, toPosition: Int): Boolean {
    if (fromPosition == toPosition) {
        return false
    }
    if (isValidIndex(fromPosition) && isValidIndex(toPosition)) {
        val from = get(fromPosition)
        set(fromPosition, get(toPosition))
        set(toPosition, from)
        return true
    }
    return false
}

private fun List<*>.isValidIndex(index: Int): Boolean = index in 0..<size

package com.github.redborsch.recyclerview

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

abstract class DragRecyclerViewAdapter<VH : RecyclerView.ViewHolder, Item> :
    RecyclerView.Adapter<VH>() {

    private var _items: ArrayList<Item> = ArrayList(0)
    var items: List<Item>
        get() = _items
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            _items = ArrayList(value)
            notifyDataSetChanged()
        }

    private val _dragController = DragControllerImpl { fromPosition: Int, toPosition: Int ->
        val swapped = _items.exchange(fromPosition, toPosition)
        if (swapped) {
            notifyItemMoved(fromPosition, toPosition)
        }
        swapped
    }
    protected val dragController: DragController get() = _dragController

    @CallSuper
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        _dragController.attach(recyclerView)
    }

    final override fun getItemCount(): Int = _items.size
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

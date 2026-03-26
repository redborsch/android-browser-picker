package com.github.redborsch.recyclerview

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

abstract class DragRecyclerViewAdapter<VH : RecyclerView.ViewHolder, Data : Rearrangeable> :
    RecyclerView.Adapter<VH>() {

    var data: Data? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val _dragController = DragControllerImpl { fromPosition: Int, toPosition: Int ->
        val data = data ?: return@DragControllerImpl false
        val swapped = data.exchange(fromPosition, toPosition)
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

    final override fun getItemCount(): Int = data?.size ?: 0
}

interface Rearrangeable {
    val size: Int

    fun exchange(fromPosition: Int, toPosition: Int): Boolean
}
